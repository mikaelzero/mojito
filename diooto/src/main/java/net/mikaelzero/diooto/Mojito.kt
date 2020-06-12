package net.mikaelzero.diooto

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.net.Uri
import android.view.ContextThemeWrapper
import android.view.View
import android.view.Window
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import net.mikaelzero.diooto.ImageActivity.Companion.startImageActivity
import net.mikaelzero.diooto.config.ContentViewOriginModel
import net.mikaelzero.diooto.config.DiootoConfig
import net.mikaelzero.diooto.interfaces.*
import net.mikaelzero.diooto.loader.ContentLoader
import net.mikaelzero.diooto.loader.ImageLoader
import java.util.*

class Mojito {
    var mContext: Context? = null
    private var mImageLoader: ImageLoader? = null
    private var contentLoader: ContentLoader? = null
    private var imageViewFactory: ImageViewFactory? = null

    private constructor(
        imageLoader: ImageLoader, contentLoader: ContentLoader,
        imageViewFactory: ImageViewFactory
    ) {
        mImageLoader = imageLoader
        this.contentLoader = contentLoader
        this.imageViewFactory = imageViewFactory
    }

    constructor(context: Context?) {
        mContext = context
        diootoConfig = DiootoConfig()
    }

    private var diootoConfig: DiootoConfig? = null
    fun urls(imageUrl: String): Mojito {
        diootoConfig!!.imageUrls = arrayOf(imageUrl)
        return this
    }

    fun fullscreen(isFullScreen: Boolean): Mojito {
        diootoConfig!!.isFullScreen = isFullScreen
        return this
    }

    fun indicatorVisibility(visibility: Int): Mojito {
        diootoConfig!!.indicatorVisibility = visibility
        return this
    }

    fun urls(imageUrls: Array<String?>?): Mojito {
        diootoConfig!!.imageUrls = imageUrls
        return this
    }

    fun immersive(immersive: Boolean): Mojito {
        diootoConfig!!.isImmersive = immersive
        return this
    }

    @JvmOverloads
    fun position(position: Int, headerSize: Int = 0): Mojito {
        diootoConfig!!.headerSize = headerSize
        diootoConfig!!.position = position - headerSize
        return this
    }

    fun views(view: View?): Mojito {
        val views = arrayOfNulls<View>(1)
        views[0] = view
        return views(views)
    }

    fun views(recyclerView: RecyclerView, @IdRes viewId: Int): Mojito {
        val originImageList: MutableList<View?> =
            ArrayList()
        val childCount = recyclerView.childCount
        for (i in 0 until childCount) {
            val originImage = recyclerView.getChildAt(i)
                .findViewById<View>(viewId)
            if (originImage != null) {
                originImageList.add(originImage)
            }
        }
        val layoutManager = recyclerView.layoutManager
        var firstPos = 0
        var lastPos = 0
        val totalCount = layoutManager!!.itemCount - diootoConfig!!.headerSize
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
        val views =
            arrayOfNulls<View>(originImageList.size)
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
        val list: MutableList<ContentViewOriginModel> =
            ArrayList()
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
        diootoConfig!!.contentViewOriginModels = list
        return this
    }

    fun start(): Mojito {
//        if (!diootoConfig!!.isImmersive) {
//            val window = getWindow(mContext)
//            if (window?.attributes?.flags ?: false and WindowManager.LayoutParams.FLAG_FULLSCREEN
//                == WindowManager.LayoutParams.FLAG_FULLSCREEN
//            ) {
//                diootoConfig!!.isFullScreen = true
//            }
//            if (!diootoConfig!!.isFullScreen) {
//                window?.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                    window?.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
//                    window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                    window?.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
//                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//                    window?.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
//                }
//            }
//        }
        if (ImageActivity.iIndicator == null) {
            setIndicator(CircleIndexIndicator())
        }
        if (ImageActivity.iProgress == null) {
            setProgress(DefaultPercentProgress())
        }
        startImageActivity(scanForActivity(mContext), diootoConfig)
        return this
    }

    private fun getWindow(context: Context?): Window? {
        return if (getAppCompActivity(context) != null) {
            getAppCompActivity(context)?.window
        } else {
            scanForActivity(context)?.window
        }
    }

    private fun getAppCompActivity(context: Context?): AppCompatActivity? {
        if (context == null) return null
        if (context is AppCompatActivity) {
            return context
        } else if (context is ContextThemeWrapper) {
            return getAppCompActivity(context.baseContext)
        }
        return null
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
        ImageActivity.iProgress = on
        return this
    }

    fun setIndicator(on: IIndicator?): Mojito {
        ImageActivity.iIndicator = on
        return this
    }

    fun loadPhotoBeforeShowBigImage(on: OnLoadPhotoBeforeShowBigImageListener?): Mojito {
        onLoadPhotoBeforeShowBigImageListener = on
        return this
    }

    fun onVideoLoadEnd(on: OnShowToMaxFinishListener?): Mojito {
        onShowToMaxFinishListener = on
        return this
    }

    fun onFinish(on: OnFinishListener?): Mojito {
        onFinishListener = on
        return this
    }

    fun onProvideVideoView(on: OnProvideViewListener?): Mojito {
        onProvideViewListener = on
        return this
    }

    interface OnLoadPhotoBeforeShowBigImageListener {
        fun loadView(sketchImageView: View?, position: Int)
    }

    interface OnProvideViewListener {
        fun provideView(): View?
    }

    interface OnShowToMaxFinishListener {
        fun onShowToMax(
            mojitoView: MojitoView?,
            sketchImageView: View?,
            progressView: View?
        )
    }

    interface OnFinishListener {
        fun finish(mojitoView: MojitoView?)
    }

    companion object {
        var showImmediatelyFlag = true

        @Volatile
        private var sInstance: Mojito? = null

        @JvmStatic
        fun initialize(
            imageLoader: ImageLoader,
            contentLoader: ContentLoader,
            imageViewFactory: ImageViewFactory
        ) {
            sInstance = Mojito(imageLoader, contentLoader, imageViewFactory)
        }

        @JvmStatic
        fun imageLoader(): ImageLoader? {
            checkNotNull(sInstance) { "You must initialize Diooto before use it!" }
            return sInstance!!.mImageLoader
        }

        @JvmStatic
        fun contentLoader(): ContentLoader? {
            checkNotNull(sInstance) { "You must initialize Diooto before use it!" }
            return sInstance!!.contentLoader
        }

        @JvmStatic
        fun imageViewFactory(): ImageViewFactory? {
            checkNotNull(sInstance) { "You must initialize Diooto before use it!" }
            return sInstance!!.imageViewFactory
        }

        fun prefetch(vararg uris: Uri?) {
            val imageLoader = imageLoader()
            for (uri in uris) {
                imageLoader!!.prefetch(uri)
            }
        }

        var onLoadPhotoBeforeShowBigImageListener: OnLoadPhotoBeforeShowBigImageListener? = null
        var onShowToMaxFinishListener: OnShowToMaxFinishListener? = null
        var onProvideViewListener: OnProvideViewListener? = null

        @JvmField
        var onFinishListener: OnFinishListener? = null
    }
}