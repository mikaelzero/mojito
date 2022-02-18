package net.mikaelzero.mojito.photoviewimageviewloader

import android.content.Context
import android.graphics.RectF
import android.view.View
import android.widget.ImageView
import androidx.lifecycle.LifecycleObserver
import com.github.chrisbanes.photoview.PhotoView
import net.mikaelzero.mojito.Mojito.Companion.mojitoConfig
import net.mikaelzero.mojito.interfaces.OnMojitoViewCallback
import net.mikaelzero.mojito.loader.ContentLoader
import net.mikaelzero.mojito.loader.OnLongTapCallback
import net.mikaelzero.mojito.loader.OnTapCallback
import net.mikaelzero.mojito.tools.ScreenUtils


/**
 * @Author: MikaelZero
 * @CreateDate: 2020/6/10 10:01 AM
 * @Description:
 */
class PhotoViewContentLoaderImpl : ContentLoader, LifecycleObserver {


    private lateinit var sketchImageView: PhotoView
    private var screenHeight = 0
    private var screenWidth = 0
    private var onMojitoViewCallback: OnMojitoViewCallback? = null

    override fun providerRealView(): View {
        return sketchImageView
    }

    override fun providerView(): View {
        return sketchImageView
    }

    override val displayRect: RectF
        get() {
            return sketchImageView.displayRect
        }

    override fun init(context: Context, originUrl: String, targetUrl: String?, onMojitoViewCallback: OnMojitoViewCallback?) {
        sketchImageView = PhotoView(context)
        screenHeight = if (mojitoConfig().transparentNavigationBar()) ScreenUtils.getScreenHeight(context) else ScreenUtils.getAppScreenHeight(context)
        screenWidth = ScreenUtils.getScreenWidth(context)
        this.onMojitoViewCallback = onMojitoViewCallback
    }

    override fun dispatchTouchEvent(isDrag: Boolean, isActionUp: Boolean, isDown: Boolean, isHorizontal: Boolean): Boolean {
        return !sketchImageView.isZoomable
    }

    override fun dragging(width: Int, height: Int, ratio: Float) {
    }

    override fun beginBackToMin(isResetSize: Boolean) {
        if (isResetSize) {
            sketchImageView.scaleType = ImageView.ScaleType.CENTER_CROP
        }
    }

    override fun backToNormal() {
    }

    override fun loadAnimFinish() {
        sketchImageView.scaleType = ImageView.ScaleType.FIT_CENTER
    }

    override fun needReBuildSize(): Boolean {
        return sketchImageView.isZoomable
    }

    override fun useTransitionApi(): Boolean {
        return needReBuildSize()
    }

    override fun isLongImage(width: Int, height: Int): Boolean {
        sketchImageView.scaleType = ImageView.ScaleType.CENTER_CROP
        return false
    }

    override fun onTapCallback(onTapCallback: OnTapCallback) {
        sketchImageView.setOnPhotoTapListener { view, x, y ->
            onTapCallback.onTap(view, x, y)
        }
    }

    override fun onLongTapCallback(onLongTapCallback: OnLongTapCallback) {
        sketchImageView.attacher.setOnLongClickListener {
            onLongTapCallback.onLongTap(it, -1f, -1f)
            true
        }
    }

    override fun pageChange(isHidden: Boolean) {

    }

}