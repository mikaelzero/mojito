package net.mikaelzero.mojito.ui

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
import net.mikaelzero.mojito.Mojito.Companion.imageLoader
import net.mikaelzero.mojito.Mojito.Companion.imageViewFactory
import net.mikaelzero.mojito.Mojito.Companion.mojitoConfig
import net.mikaelzero.mojito.MojitoView
import net.mikaelzero.mojito.R
import net.mikaelzero.mojito.bean.FragmentConfig
import net.mikaelzero.mojito.interfaces.IMojitoFragment
import net.mikaelzero.mojito.interfaces.IProgress
import net.mikaelzero.mojito.interfaces.ImageViewLoadFactory
import net.mikaelzero.mojito.interfaces.OnMojitoViewCallback
import net.mikaelzero.mojito.loader.*
import net.mikaelzero.mojito.tools.BitmapUtil
import net.mikaelzero.mojito.tools.MojitoConstant
import net.mikaelzero.mojito.tools.ScreenUtils
import java.io.File


class ImageMojitoFragment : Fragment(), IMojitoFragment, OnMojitoViewCallback {
    lateinit var fragmentConfig: FragmentConfig
    private var showView: View? = null
    private var mImageLoader: ImageLoader? = null
    private var mViewLoadFactory: ImageViewLoadFactory? = null
    private var contentLoader: ContentLoader? = null
    private var mainHandler = Handler(Looper.getMainLooper())
    private var iProgress: IProgress? = null
    private var fragmentCoverLoader: FragmentCoverLoader? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_image, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (context == null || activity == null) {
            return
        }
        if (arguments != null) {
            fragmentConfig = requireArguments().getParcelable(MojitoConstant.KEY_FRAGMENT_PARAMS)!!
        }
        mImageLoader = imageLoader()
        mViewLoadFactory = if (ImageMojitoActivity.multiContentLoader != null) {
            ImageMojitoActivity.multiContentLoader?.providerLoader(fragmentConfig.position)
        } else {
            imageViewFactory()
        }
        fragmentCoverLoader = ImageMojitoActivity.fragmentCoverLoader?.providerInstance()
        imageCoverLayout.removeAllViews()
        val fragmentCoverAttachView = fragmentCoverLoader?.attach(this, fragmentConfig.targetUrl == null || fragmentConfig.autoLoadTarget)
        if (fragmentCoverAttachView != null) {
            imageCoverLayout.visibility = View.VISIBLE
            imageCoverLayout.addView(fragmentCoverAttachView)
        } else {
            imageCoverLayout.visibility = View.GONE
        }

        iProgress = ImageMojitoActivity.progressLoader?.providerInstance()
        iProgress?.attach(fragmentConfig.position, loadingLayout)
        contentLoader = mViewLoadFactory?.newContentLoader()

        mojitoView.setBackgroundAlpha(
            if (ImageMojitoActivity.hasShowedAnimMap[fragmentConfig.position] == true) 1f else if (fragmentConfig.showImmediately) 1f else 0f
        )
        mojitoView.setOnMojitoViewCallback(this)
        mojitoView.setContentLoader(contentLoader, fragmentConfig.originUrl, fragmentConfig.targetUrl)
        showView = contentLoader?.providerRealView()

        contentLoader?.onTapCallback(object : OnTapCallback {
            override fun onTap(view: View, x: Float, y: Float) {
                mojitoView.backToMin()
                ImageMojitoActivity.onMojitoListener?.onClick(view, x, y, fragmentConfig.position)
            }
        })
        contentLoader?.onLongTapCallback(object : OnLongTapCallback {
            override fun onLongTap(view: View, x: Float, y: Float) {
                if (!mojitoView.isDrag) {
                    ImageMojitoActivity.onMojitoListener?.onLongClick(activity, view, x, y, fragmentConfig.position)
                }
            }
        })
        val isFile: Boolean = File(fragmentConfig.originUrl).isFile
        val uri = if (isFile) {
            Uri.fromFile(File(fragmentConfig.originUrl))
        } else {
            Uri.parse(fragmentConfig.originUrl)
        }
        mImageLoader?.loadImage(showView.hashCode(), uri, !isFile, object : DefaultImageCallback() {
            override fun onSuccess(image: File) {
                mainHandler.post {
                    if (isDetached || context == null) {
                        return@post
                    }
                    mViewLoadFactory?.loadSillContent(showView!!, Uri.fromFile(image))
                    startAnim(image)
                }
            }

            override fun onFail(error: Exception) {
                mainHandler.post {
                    if (isDetached || context == null) {
                        return@post
                    }
                    startAnim(ScreenUtils.getScreenWidth(context), ScreenUtils.getScreenHeight(context), originLoadFail = true, needLoadImageUrl = fragmentConfig.originUrl)
                }
            }
        })
    }


    private fun startAnim(image: File) {
        val realSizes = getRealSizeFromFile(image)
        startAnim(realSizes[0], realSizes[1])
    }


    private fun startAnim(w: Int, h: Int, originLoadFail: Boolean = false, needLoadImageUrl: String = "") {
        if (!fragmentConfig.showImmediately) {
            ImageMojitoActivity.onMojitoListener?.onStartAnim(fragmentConfig.position)
        }
        if (fragmentConfig.viewParams == null) {
            mojitoView?.showWithoutView(w, h, if (ImageMojitoActivity.hasShowedAnimMap[fragmentConfig.position] == true) true else fragmentConfig.showImmediately)
        } else {
            mojitoView?.putData(
                fragmentConfig.viewParams!!.getLeft(), fragmentConfig.viewParams!!.getTop(),
                fragmentConfig.viewParams!!.getWidth(), fragmentConfig.viewParams!!.getHeight(),
                w, h
            )
            mojitoView?.show(if (ImageMojitoActivity.hasShowedAnimMap[fragmentConfig.position] == true) true else fragmentConfig.showImmediately)
        }

        val targetEnable = if (ImageMojitoActivity.multiContentLoader == null) {
            true
        } else {
            ImageMojitoActivity.multiContentLoader?.providerEnableTargetLoad(fragmentConfig.position) ?: false
        }
        //查看原图的情况下  如果缩略图加载失败了  需要先加载缩略图  再根据条件判断是否要去加载原图
        if (originLoadFail && needLoadImageUrl.isNotEmpty()) {
            loadImageWithoutCache(needLoadImageUrl, fragmentConfig.targetUrl != null && targetEnable)
        } else if (fragmentConfig.targetUrl != null && targetEnable) {
            replaceImageUrl(fragmentConfig.targetUrl!!)
        } else if (needLoadImageUrl.isNotEmpty()) {
            loadImageWithoutCache(needLoadImageUrl)
        }
    }


    private fun replaceImageUrl(url: String, forceLoadTarget: Boolean = false) {
        /**
         * forceLoadTarget 查看原图功能
         * 如果打开了自动加载原图  则隐藏查看原图
         * 如果关闭了自动加载原图:
         * 1. 需要用户点击按钮 才进行加载  forceLoadTarget 为true  强制加载目标图
         * 2. 默认进入的时候 判断是否有缓存  有的话直接加载 隐藏查看原图按钮
         */
        val onlyRetrieveFromCache: Boolean = if (forceLoadTarget) {
            !forceLoadTarget
        } else {
            !fragmentConfig.autoLoadTarget
        }
        mImageLoader?.loadImage(showView.hashCode(), Uri.parse(url), onlyRetrieveFromCache, object : DefaultImageCallback() {
            override fun onStart() {
                handleImageOnStart()
            }

            override fun onProgress(progress: Int) {
                handleImageOnProgress(progress)
            }

            override fun onFail(error: Exception?) {
                loadImageFail(onlyRetrieveFromCache)
            }

            override fun onSuccess(image: File) {
                mainHandler.post {
                    if (isDetached || context == null) {
                        return@post
                    }
                    handleImageOnSuccess(image)
                }
            }
        })
    }

    /**
     *  如果图片还未加载出来  则加载图片  最后通知修改宽高
     */
    private fun loadImageWithoutCache(url: String, needHandleTarget: Boolean = false) {
        mImageLoader?.loadImage(showView.hashCode(), Uri.parse(url), false, object : DefaultImageCallback() {
            override fun onStart() {
                handleImageOnStart()
            }

            override fun onProgress(progress: Int) {
                handleImageOnProgress(progress)
            }

            override fun onFail(error: Exception?) {
                loadImageFail(false)
            }

            override fun onSuccess(image: File) {
                mainHandler.post {
                    if (isDetached || context == null) {
                        return@post
                    }
                    handleImageOnSuccess(image)
                    val realSizes = getRealSizeFromFile(image)
                    mojitoView?.resetSize(realSizes[0], realSizes[1])
                    if (needHandleTarget) {
                        replaceImageUrl(fragmentConfig.targetUrl!!)
                    }
                }
            }
        })
    }

    private fun handleImageOnStart() {
        mainHandler.post {
            if (isDetached || context == null) {
                return@post
            }
            if (loadingLayout?.visibility == View.GONE) {
                loadingLayout?.visibility = View.VISIBLE
            }
            iProgress?.onStart(fragmentConfig.position)
        }
    }

    private fun handleImageOnProgress(progress: Int) {
        mainHandler.post {
            if (isDetached || context == null) {
                return@post
            }
            if (loadingLayout?.visibility == View.GONE) {
                loadingLayout?.visibility = View.VISIBLE
            }
            iProgress?.onProgress(fragmentConfig.position, progress)
        }
    }

    private fun handleImageOnSuccess(image: File) {
        if (loadingLayout?.visibility == View.VISIBLE) {
            loadingLayout?.visibility = View.GONE
        }
        fragmentCoverLoader?.imageCacheHandle(isCache = true, hasTargetUrl = true)
        mViewLoadFactory?.loadSillContent(showView!!, Uri.fromFile(image))
    }

    override fun loadTargetUrl() {
        if (fragmentConfig.targetUrl == null) {
            fragmentCoverLoader?.imageCacheHandle(isCache = false, hasTargetUrl = false)
        } else {
            replaceImageUrl(fragmentConfig.targetUrl!!, true)
        }
    }

    private fun getRealSizeFromFile(image: File): Array<Int> {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(image.absolutePath, options)
        val arr = BitmapUtil.getAdjustSize(image.path, options)
        var w = arr[0]
        var h = arr[1]
        val isLongImage = contentLoader?.isLongImage(w, h)
        if (isLongImage != null && isLongImage) {
            w = ScreenUtils.getScreenWidth(context)
            h = ScreenUtils.getScreenHeight(context)
        }
        return arrayOf(w, h)
    }

    private fun loadImageFail(onlyRetrieveFromCache: Boolean) {
        if (!onlyRetrieveFromCache) {
            val errorDrawableResId = mojitoConfig().errorDrawableResId()
            if (errorDrawableResId != 0) {
                mViewLoadFactory?.loadContentFail(showView!!, errorDrawableResId)
            }
        }
        mainHandler.post {
            if (isDetached || context == null) {
                return@post
            }
            if (loadingLayout?.visibility == View.GONE) {
                loadingLayout?.visibility = View.VISIBLE
            }
            iProgress?.onFailed(fragmentConfig.position)
            fragmentCoverLoader?.imageCacheHandle(isCache = false, hasTargetUrl = true)
        }
    }

    private fun postToMain(r: Runnable) {
        mainHandler.post {
            if (isDetached || context == null) {
                return@post
            }
            r.run()
        }
    }

    override fun backToMin() {
        mojitoView?.backToMin()
    }

    override fun providerContext(): Fragment {
        return this
    }

    override fun onResume() {
        contentLoader?.pageChange(false)
        super.onResume()
    }

    override fun onPause() {
        contentLoader?.pageChange(true)
        super.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mImageLoader?.cancel(showView.hashCode())
    }

    companion object {
        fun newInstance(fragmentConfig: FragmentConfig): ImageMojitoFragment {
            val args = Bundle()
            args.putParcelable(MojitoConstant.KEY_FRAGMENT_PARAMS, fragmentConfig)
            val fragment = ImageMojitoFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onMojitoViewFinish() {
        ImageMojitoActivity.onMojitoListener?.onMojitoViewFinish(fragmentConfig.position)
        if (context is ImageMojitoActivity) {
            (context as ImageMojitoActivity).finishView()
        }
    }

    override fun onDrag(view: MojitoView, moveX: Float, moveY: Float) {
        ImageMojitoActivity.iIndicator?.move(moveX, moveY)
        ImageMojitoActivity.activityCoverLoader?.move(moveX, moveY)
        fragmentCoverLoader?.move(moveX, moveY)
        ImageMojitoActivity.onMojitoListener?.onDrag(view, moveX, moveY)
    }

    override fun onRelease(isToMax: Boolean, isToMin: Boolean) {
        ImageMojitoActivity.iIndicator?.fingerRelease(isToMax, isToMin)
        fragmentCoverLoader?.fingerRelease(isToMax, isToMin)
        ImageMojitoActivity.activityCoverLoader?.fingerRelease(isToMax, isToMin)
    }

    override fun showFinish(mojitoView: MojitoView, showImmediately: Boolean) {
        ImageMojitoActivity.onMojitoListener?.onShowFinish(mojitoView, showImmediately)
        if (!showImmediately) {
            ImageMojitoActivity.hasShowedAnimMap[fragmentConfig.position] = true
        }
    }

    override fun onLongImageMove(ratio: Float) {
        ImageMojitoActivity.onMojitoListener?.onLongImageMove(ratio)
    }

    override fun onLock(isLock: Boolean) {
        if (context is ImageMojitoActivity) {
            (context as ImageMojitoActivity).setViewPagerLock(isLock)
        }
    }

}