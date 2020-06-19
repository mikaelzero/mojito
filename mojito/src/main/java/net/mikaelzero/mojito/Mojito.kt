package net.mikaelzero.mojito

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.net.Uri
import android.view.View
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import net.mikaelzero.mojito.ui.ImageMojitoActivity.Companion.startImageActivity
import net.mikaelzero.mojito.bean.ConfigBean
import net.mikaelzero.mojito.bean.ContentViewOriginModel
import net.mikaelzero.mojito.impl.DefaultMojitoConfig
import net.mikaelzero.mojito.interfaces.*
import net.mikaelzero.mojito.loader.IContentViewImplFactory
import net.mikaelzero.mojito.loader.ImageCoverLoader
import net.mikaelzero.mojito.loader.ImageLoader
import net.mikaelzero.mojito.loader.InstanceLoader

//TODO 旋转屏幕没有做处理  使用场景不多 暂时不考虑
class Mojito {

    private object SingletonHolder {
        val holder = Mojito()
    }


    companion object {
        private var isInit = false
        val instance = SingletonHolder.holder

        @JvmStatic
        fun with(context: Context?): Mojito {
            instance.mContext = context
            instance.configBean = ConfigBean()
            return instance
        }

        @JvmStatic
        fun initialize(
            imageLoader: ImageLoader,
            contentLoader: IContentViewImplFactory,
            imageViewLoadFactory: ImageViewLoadFactory
        ) {
            instance.mImageLoader = imageLoader
            instance.contentLoader = contentLoader
            instance.imageViewLoadFactory = imageViewLoadFactory
        }

        @JvmStatic
        fun initialize(
            imageLoader: ImageLoader,
            contentLoader: IContentViewImplFactory,
            imageViewLoadFactory: ImageViewLoadFactory,
            mojitoConfig: IMojitoConfig
        ) {
            instance.mImageLoader = imageLoader
            instance.contentLoader = contentLoader
            instance.imageViewLoadFactory = imageViewLoadFactory
            instance.mojitoConfig = mojitoConfig
        }

        @JvmStatic
        fun imageLoader(): ImageLoader? {
            return instance.mImageLoader
        }

        @JvmStatic
        fun contentLoader(): IContentViewImplFactory? {
            return instance.contentLoader
        }

        @JvmStatic
        fun imageViewFactory(): ImageViewLoadFactory? {
            return instance.imageViewLoadFactory
        }

        @JvmStatic
        fun mojitoConfig(): IMojitoConfig {
            if (instance.mojitoConfig == null) {
                instance.mojitoConfig = DefaultMojitoConfig()
            }
            return instance.mojitoConfig!!
        }


        @JvmStatic
        fun prefetch(vararg uris: Uri?) {
            val imageLoader = imageLoader()
            for (uri in uris) {
                imageLoader?.prefetch(uri)
            }
        }

        @JvmStatic
        fun prefetch(vararg uris: String?) {
            val imageLoader = imageLoader()
            for (uri in uris) {
                imageLoader?.prefetch(Uri.parse(uri))
            }
        }

        var iIndicator: IIndicator? = null
        var coverLayoutLoader: CoverLayoutLoader? = null

        fun clean() {
            instance.mContext = null
            isInit = false
            imageLoader()?.cancelAll()
            instance.onMojitoListener = null
            iIndicator = null
            coverLayoutLoader = null
        }
    }


    private var mContext: Context? = null
    private var mImageLoader: ImageLoader? = null
    private var contentLoader: IContentViewImplFactory? = null
    private var imageViewLoadFactory: ImageViewLoadFactory? = null
    private var mojitoConfig: IMojitoConfig? = null
    private var configBean: ConfigBean? = null
    private var progressLoader: InstanceLoader<IProgress>? = null
    private var imageCoverLoader: InstanceLoader<ImageCoverLoader>? = null


    fun urls(imageUrl: String): Mojito {
        configBean?.originImageUrls = listOf(imageUrl)
        return this
    }

    fun urls(imageUrl: String, targetUrl: String): Mojito {
        configBean?.originImageUrls = listOf(imageUrl)
        configBean?.targetImageUrls = listOf(targetUrl)
        return this
    }


    fun urls(imageUrls: List<String>?): Mojito {
        configBean?.originImageUrls = imageUrls
        return this
    }

    fun urls(imageUrls: List<String>?, targetImageUrls: List<String>?): Mojito {
        configBean?.originImageUrls = imageUrls
        configBean?.targetImageUrls = targetImageUrls
        return this
    }

    @JvmOverloads
    fun position(position: Int, headerSize: Int = 0): Mojito {
        configBean?.headerSize = headerSize
        configBean?.position = position - headerSize
        return this
    }

    fun views(view: View?): Mojito {
        val views = arrayOfNulls<View>(1)
        views[0] = view
        return views(views)
    }

    fun views(recyclerView: RecyclerView, @IdRes viewId: Int): Mojito {
        val originImageList = mutableListOf<View?>()
        val childCount = recyclerView.childCount
        for (i in 0 until childCount) {
            val originImage = recyclerView.getChildAt(i).findViewById<View>(viewId)
            if (originImage != null) {
                originImageList.add(originImage)
            }
        }
        val layoutManager = recyclerView.layoutManager
        var firstPos = 0
        var lastPos = 0
        val totalCount = layoutManager!!.itemCount - (configBean?.headerSize ?: 0)
        if (layoutManager is GridLayoutManager) {
            firstPos = layoutManager.findFirstVisibleItemPosition()
            lastPos = layoutManager.findLastVisibleItemPosition()
        } else if (layoutManager is LinearLayoutManager) {
            firstPos = layoutManager.findFirstVisibleItemPosition()
            lastPos = layoutManager.findLastVisibleItemPosition()
        } else if (layoutManager is StaggeredGridLayoutManager) {

        }
        fillPlaceHolder(originImageList, totalCount, firstPos, lastPos)
        val views = arrayOfNulls<View>(originImageList.size)
        for (i in originImageList.indices) {
            views[i] = originImageList[i]
        }
        return views(views)
    }

    private fun fillPlaceHolder(originImageList: MutableList<View?>, totalCount: Int, firstPos: Int, lastPos: Int) {
        if (firstPos > 0) {
            for (pos in firstPos downTo 1) {
                originImageList.add(0, null)
            }
        }
        if (lastPos < totalCount) {
            for (i in totalCount - 1 - lastPos downTo 1) {
                originImageList.add(null)
            }
        }
    }

    fun views(views: Array<View?>): Mojito {
        val list = mutableListOf<ContentViewOriginModel>()
        for (imageView in views) {
            val imageBean = ContentViewOriginModel()
            if (imageView == null) {
                imageBean.left = 0
                imageBean.top = 0
                imageBean.width = 0
                imageBean.height = 0
            } else {
                val location = IntArray(2)
                imageView.getLocationOnScreen(location)
                imageBean.left = location[0]
                imageBean.top = location[1]
                imageBean.width = imageView.width
                imageBean.height = imageView.height
            }
            list.add(imageBean)
        }
        configBean?.contentViewOriginModels = list
        return this
    }


    fun start(): Mojito {
        if (!isInit) {
            isInit = true
            startImageActivity(scanForActivity(mContext), configBean)
        }
        return this
    }

    private fun scanForActivity(context: Context?): Activity? {
        if (context == null) return null
        if (context is Activity) {
            return context
        } else if (context is ContextWrapper) {
            return scanForActivity(context.baseContext)
        }
        return null
    }


    fun setProgressLoader(loader: InstanceLoader<IProgress>): Mojito {
        this.progressLoader = loader
        return this
    }

    fun setImageCoverLoader(loader: InstanceLoader<ImageCoverLoader>): Mojito {
        this.imageCoverLoader = loader
        return this
    }


    fun setCoverLayoutLoader(on: CoverLayoutLoader): Mojito {
        coverLayoutLoader = on
        return this
    }

    fun setIndicator(on: IIndicator?): Mojito {
        iIndicator = on
        return this
    }


    private var onMojitoListener: OnMojitoListener? = null

    fun setOnMojitoListener(onMojitoListener: OnMojitoListener): Mojito {
        this.onMojitoListener = onMojitoListener
        return this
    }

    fun mojitoListener(): OnMojitoListener? {
        return instance.onMojitoListener
    }

    fun progressLoader(): InstanceLoader<IProgress>? {
        return instance.progressLoader
    }

    fun imageCoverLoader(): InstanceLoader<ImageCoverLoader>? {
        return instance.imageCoverLoader
    }
}