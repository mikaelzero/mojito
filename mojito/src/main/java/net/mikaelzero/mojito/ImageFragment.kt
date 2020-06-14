package net.mikaelzero.mojito

import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_image.*
import net.mikaelzero.mojito.Mojito.Companion.contentLoader
import net.mikaelzero.mojito.Mojito.Companion.imageLoader
import net.mikaelzero.mojito.Mojito.Companion.imageViewFactory
import net.mikaelzero.mojito.bean.ContentViewOriginModel
import net.mikaelzero.mojito.interfaces.ImageViewLoadFactory
import net.mikaelzero.mojito.loader.ContentLoader
import net.mikaelzero.mojito.loader.DefaultImageCallback
import net.mikaelzero.mojito.loader.ImageLoader
import net.mikaelzero.mojito.tools.ScreenUtils
import java.io.File


class ImageFragment : Fragment(), ImageLoader.Callback {
    var contentViewOriginModel: ContentViewOriginModel? = null
    var originUrl: String? = null
    var targetUrl: String? = null
    var showView: View? = null
    var position = 0
    var showImmediately = false
    private var mImageLoader: ImageLoader? = null
    private var mViewLoadFactory: ImageViewLoadFactory? = null
    private var contentLoader: ContentLoader? = null
    private var mainHandler = Handler(Looper.getMainLooper())

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_image, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (context == null || activity == null) {
            return
        }
        mImageLoader = imageLoader()
        mViewLoadFactory = imageViewFactory()
        if (arguments != null) {
            originUrl = arguments!!.getString("originUrl")
            targetUrl = arguments!!.getString("targetUrl")
            position = arguments!!.getInt("position")
            showImmediately = arguments!!.getBoolean("showImmediately")
            contentViewOriginModel = arguments!!.getParcelable("model")
        }
        Mojito.iProgress?.attach(position, loadingLayout)
        loadingLayout?.visibility = View.GONE
        contentLoader = contentLoader()?.newInstance()
        mojitoView?.setContentLoader(contentLoader)
        showView = contentLoader?.providerRealView()
        mojitoView?.setOnShowFinishListener { mojitoView, showImmediately ->
            if (targetUrl != null) {
                mImageLoader?.loadImage(showView.hashCode(), Uri.parse(targetUrl), this@ImageFragment)
            }
        }
        mojitoView?.setOnDragListener { view1: MojitoView?, moveX: Float, moveY: Float ->
            Mojito.iIndicator?.move(moveX, moveY)
        }
        mojitoView?.setOnFinishListener {
            if (context is ImageActivity) {
                (context as ImageActivity).finishView()
            }
        }
        mojitoView?.setOnLockListener {
            if (context is ImageActivity) {
                (context as ImageActivity).setViewPagerLock(it)
            }
        }
        mojitoView?.setOnReleaseListener { isToMax: Boolean, isToMin: Boolean ->
            Mojito.iIndicator?.fingerRelease(isToMax, isToMin)
        }
        mImageLoader?.loadImage(showView.hashCode(), Uri.parse(originUrl), object : DefaultImageCallback() {
            override fun onSuccess(image: File) {
                mViewLoadFactory?.loadSillContent(showView!!, Uri.fromFile(image))
                val options = BitmapFactory.Options()
                options.inJustDecodeBounds = true
                BitmapFactory.decodeFile(image.absolutePath, options)
                var h = options.outHeight
                var w = options.outWidth
                if (targetUrl == null) {
                    val isLongImage = contentLoader?.isLongImage(w, h)
                    if (isLongImage != null && isLongImage) {
                        w = ScreenUtils.getScreenWidth(context)
                        h = ScreenUtils.getScreenHeight(context)
                    }
                }
                mojitoView?.putData(
                    contentViewOriginModel!!.getLeft(), contentViewOriginModel!!.getTop(),
                    contentViewOriginModel!!.getWidth(), contentViewOriginModel!!.getHeight(),
                    w, h
                )
                mojitoView?.show(showImmediately && !Mojito.showImmediatelyFlag)
                Mojito.showImmediatelyFlag = false

            }
        })
    }

    fun backToMin() {
        mojitoView?.backToMin()
    }

    companion object {
        fun newInstance(
            originUrl: String?,
            targetUrl: String?,
            position: Int,
            shouldShowAnimation: Boolean,
            contentViewOriginModel: ContentViewOriginModel?
        ): ImageFragment {
            val args = Bundle()
            args.putString("originUrl", originUrl)
            args.putString("targetUrl", targetUrl)
            args.putInt("position", position)
            args.putBoolean("showImmediately", shouldShowAnimation)
            args.putParcelable("model", contentViewOriginModel)
            val fragment = ImageFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onFinish() {

    }

    override fun onSuccess(image: File) {
        mainHandler.post {
            loadingLayout?.visibility = View.GONE
        }
        mViewLoadFactory?.loadSillContent(showView!!, Uri.fromFile(image))
        Mojito.iProgress?.onFinish(position)
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(image.absolutePath, options)
        val h = options.outHeight
        val w = options.outWidth
        contentLoader?.isLongImage(w, h)
    }

    override fun onFail(error: Exception?) {
        Mojito.iProgress?.onFailed(position)
    }

    override fun onCacheHit(imageType: Int, image: File?) {

    }

    override fun onCacheMiss(imageType: Int, image: File?) {
        mainHandler.post {
            loadingLayout?.visibility = View.VISIBLE
        }
        Mojito.iProgress?.onStart(position)
    }

    override fun onProgress(progress: Int) {
        mainHandler.post {
            loadingLayout?.visibility = View.VISIBLE
        }
        Mojito.iProgress?.onProgress(position, progress)
    }
}