package net.mikaelzero.mojito

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.net.Uri
import android.view.View
import androidx.annotation.IdRes
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import net.mikaelzero.mojito.ImageActivity.Companion.startImageActivity
import net.mikaelzero.mojito.bean.ContentViewOriginModel
import net.mikaelzero.mojito.bean.ConfigBean
import net.mikaelzero.mojito.interfaces.*
import net.mikaelzero.mojito.loader.DefaultMojitoConfig
import net.mikaelzero.mojito.loader.IContentViewImplFactory
import net.mikaelzero.mojito.loader.IMojitoConfig
import net.mikaelzero.mojito.loader.ImageLoader

//TODO 旋转屏幕没有做处理  使用场景不多 暂时不考虑
class Mojito {
    var mContext: Context? = null
    private var mImageLoader: ImageLoader? = null
    private var contentLoader: IContentViewImplFactory? = null
    private var imageViewLoadFactory: ImageViewLoadFactory? = null
    private var mojitoConfig: IMojitoConfig? = null

    private constructor(
        imageLoader: ImageLoader, contentLoader: IContentViewImplFactory,
        imageViewLoadFactory: ImageViewLoadFactory
    ) {
        mImageLoader = imageLoader
        this.contentLoader = contentLoader
        this.imageViewLoadFactory = imageViewLoadFactory
    }

    private constructor(
        imageLoader: ImageLoader, contentLoader: IContentViewImplFactory,
        imageViewLoadFactory: ImageViewLoadFactory, mojitoConfig: IMojitoConfig
    ) {
        mImageLoader = imageLoader
        this.contentLoader = contentLoader
        this.imageViewLoadFactory = imageViewLoadFactory
        this.mojitoConfig = mojitoConfig
    }

    constructor(context: Context?) {
        mContext = context
        configBean = ConfigBean()
    }

    private var configBean: ConfigBean? = null
    fun urls(imageUrl: String): Mojito {
        configBean?.originImageUrls = listOf(imageUrl)
        return this
    }


    fun urls(imageUrls: List<String>?): Mojito {
        configBean?.originImageUrls = imageUrls
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
            val gridLayMan = layoutManager
            firstPos = gridLayMan.findFirstVisibleItemPosition()
            lastPos = gridLayMan.findLastVisibleItemPosition()
        } else if (layoutManager is LinearLayoutManager) {
            val linLayMan = layoutManager
            firstPos = linLayMan.findFirstVisibleItemPosition()
            lastPos = linLayMan.findLastVisibleItemPosition()
        }
        fillPlaceHolder(originImageList, totalCount, firstPos, lastPos)
        val views = arrayOfNulls<View>(originImageList.size)
        for (i in originImageList.indices) {
            views[i] = originImageList[i]
        }
        return views(views)
    }

    private fun fillPlaceHolder(
        originImageList: MutableList<View?>,
        totalCount: Int,
        firstPos: Int,
        lastPos: Int
    ) {
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
        startImageActivity(scanForActivity(mContext), configBean)
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

    fun setProgress(on: IProgress?): Mojito {
        iProgress = on
        return this
    }

    fun setIndicator(on: IIndicator?): Mojito {
        iIndicator = on
        return this
    }


    fun setMojitoConfig(on: IMojitoConfig?) {
        mojitoConfig = on
    }


    companion object {
        var showImmediatelyFlag = true

        @Volatile
        private var sInstance: Mojito? = null

        @JvmStatic
        fun initialize(
            imageLoader: ImageLoader,
            contentLoader: IContentViewImplFactory,
            imageViewLoadFactory: ImageViewLoadFactory
        ) {
            sInstance = Mojito(imageLoader, contentLoader, imageViewLoadFactory)
        }

        @JvmStatic
        fun initialize(
            imageLoader: ImageLoader,
            contentLoader: IContentViewImplFactory,
            imageViewLoadFactory: ImageViewLoadFactory,
            mojitoConfig: IMojitoConfig
        ) {
            sInstance = Mojito(imageLoader, contentLoader, imageViewLoadFactory, mojitoConfig)
        }

        @JvmStatic
        fun imageLoader(): ImageLoader? {
            checkNotNull(sInstance) { "You must initialize Mojito before use it!" }
            return sInstance!!.mImageLoader
        }

        @JvmStatic
        fun contentLoader(): IContentViewImplFactory? {
            checkNotNull(sInstance) { "You must initialize Mojito before use it!" }
            return sInstance!!.contentLoader
        }

        @JvmStatic
        fun imageViewFactory(): ImageViewLoadFactory? {
            checkNotNull(sInstance) { "You must initialize Mojito before use it!" }
            return sInstance!!.imageViewLoadFactory
        }

        @JvmStatic
        fun mojitoConfig(): IMojitoConfig {
            if (sInstance!!.mojitoConfig == null) {
                sInstance!!.mojitoConfig = DefaultMojitoConfig()
            }
            return sInstance!!.mojitoConfig!!
        }

        fun prefetch(vararg uris: Uri?) {
            val imageLoader = imageLoader()
            for (uri in uris) {
                imageLoader!!.prefetch(uri)
            }
        }

        fun prefetch(vararg uris: String?) {
            val imageLoader = imageLoader()
            for (uri in uris) {
                imageLoader!!.prefetch(Uri.parse(uri))
            }
        }

        var iIndicator: IIndicator? = null

        var iProgress: IProgress? = null

    }
}