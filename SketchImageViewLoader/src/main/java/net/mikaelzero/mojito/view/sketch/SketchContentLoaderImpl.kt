package net.mikaelzero.mojito.view.sketch

import android.content.Context
import android.graphics.Rect
import android.graphics.RectF
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.lifecycle.LifecycleObserver
import net.mikaelzero.mojito.interfaces.OnMojitoViewCallback
import net.mikaelzero.mojito.loader.ContentLoader
import net.mikaelzero.mojito.loader.OnLongTapCallback
import net.mikaelzero.mojito.loader.OnTapCallback
import net.mikaelzero.mojito.tools.ScreenUtils
import net.mikaelzero.mojito.view.sketch.core.SketchImageView
import net.mikaelzero.mojito.view.sketch.core.decode.ImageSizeCalculator
import kotlin.math.abs


/**
 * @Author: MikaelZero
 * @CreateDate: 2020/6/10 10:01 AM
 * @Description:
 */
class SketchContentLoaderImpl : ContentLoader, LifecycleObserver {


    private lateinit var sketchImageView: SketchImageView
    private lateinit var frameLayout: FrameLayout
    private var isLongHeightImage = false
    private var isLongWidthImage = false
    private var screenHeight = 0
    private var screenWidth = 0
    private var longImageHeightOrWidth = 0
    private var onMojitoViewCallback: OnMojitoViewCallback? = null

    override fun providerRealView(): View {
        return sketchImageView
    }

    override fun providerView(): View {
        return frameLayout
    }

    override val displayRect: RectF
        get() {
            val rectF = RectF()
            sketchImageView.zoomer?.getDrawRect(rectF)
            return RectF(rectF)
        }

    override fun init(context: Context, originUrl: String, targetUrl: String?, onMojitoViewCallback: OnMojitoViewCallback?) {
        frameLayout = FrameLayout(context)
        sketchImageView = SketchImageView(context)
        sketchImageView.isZoomEnabled = true
        sketchImageView.options.isDecodeGifImage = true
        frameLayout.addView(sketchImageView)
        screenHeight = ScreenUtils.getScreenHeight(context)
        screenWidth = ScreenUtils.getScreenWidth(context)
        this.onMojitoViewCallback = onMojitoViewCallback
    }

    override fun dispatchTouchEvent(isDrag: Boolean, isActionUp: Boolean, isDown: Boolean, isHorizontal: Boolean): Boolean {
        return when {
            isLongHeightImage -> {
                when {
                    isDrag -> {
                        return false
                    }
                    isActionUp -> {
                        return !isDrag
                    }
                    else -> {
                        val rectF = Rect()
                        sketchImageView.zoomer?.getVisibleRect(rectF)
                        val drawRect = RectF()
                        sketchImageView.zoomer?.getDrawRect(drawRect)
                        onMojitoViewCallback?.onLongImageMove(abs(drawRect.top) / (longImageHeightOrWidth - screenHeight).toFloat())
                        //长图处于顶部  并且有向下滑动的趋势
                        val isTop = sketchImageView.zoomer!!.zoomScale == sketchImageView.zoomer!!.maxZoomScale && rectF.top == 0 && isDown
                        //长图不处于顶部和底部的时候
                        val isCenter = sketchImageView.zoomer!!.maxZoomScale - sketchImageView.zoomer!!.zoomScale <= 0.01f
                                && rectF.top != 0
                                && screenHeight != drawRect.bottom.toInt()
                        //长图处于缩放状态  由于库的bug 会出现 8.99999  和  9
                        val isScale = sketchImageView.zoomer!!.maxZoomScale - sketchImageView.zoomer!!.zoomScale > 0.01f
                        val isBottom = (sketchImageView.zoomer!!.zoomScale == sketchImageView.zoomer!!.maxZoomScale
                                && !isDown
                                && screenHeight == drawRect.bottom.toInt())
//                        Log.e("result", "result:  isTop$isTop    isCenter:$isCenter    isScale:$isScale    isBottom:$isBottom")
                        return isTop || isCenter || isScale || isBottom
                    }
                }
            }
            isLongWidthImage -> {
                val rectF = Rect()
                sketchImageView.zoomer?.getVisibleRect(rectF)
                val result = when {
                    isDrag && !isHorizontal -> {
                        false
                    }
                    isActionUp -> {
                        !isDrag
                    }
                    else -> {
                        val drawRect = RectF()
                        sketchImageView.zoomer?.getDrawRect(drawRect)
                        onMojitoViewCallback?.onLongImageMove(abs(drawRect.left) / abs(drawRect.right - drawRect.left))
                        val isScale = sketchImageView.zoomer!!.maxZoomScale - sketchImageView.zoomer!!.zoomScale > 0.01f
                        return isHorizontal || isScale
                    }
                }
                result
            }
            else -> {
                sketchImageView.zoomer!!.zoomScale > sketchImageView.zoomer!!.fullZoomScale
            }
        }
    }

    override fun dragging(width: Int, height: Int, ratio: Float) {
    }

    override fun beginBackToMin(isResetSize: Boolean) {
        if (isLongHeightImage || isLongWidthImage) {

        } else {
            if (isResetSize) {
                sketchImageView.scaleType = ImageView.ScaleType.CENTER_CROP
            }
        }
    }

    override fun backToNormal() {
    }

    override fun loadAnimFinish() {
        if (isLongHeightImage || isLongWidthImage) {

        } else {
            sketchImageView.scaleType = ImageView.ScaleType.FIT_CENTER
        }
    }

    override fun needReBuildSize(): Boolean {
        return sketchImageView.zoomer!!.zoomScale > sketchImageView.zoomer!!.fullZoomScale
    }

    override fun useTransitionApi(): Boolean {
        return isLongWidthImage || isLongHeightImage || needReBuildSize()
    }

    override fun isLongImage(width: Int, height: Int): Boolean {
        val sizeCalculator = ImageSizeCalculator()
        isLongHeightImage = sizeCalculator.canUseReadModeByHeight(width, height) &&
                height > (ScreenUtils.getScreenHeight(sketchImageView.context) * 1.5)
        isLongWidthImage = sizeCalculator.canUseReadModeByWidth(width, height) &&
                width > (ScreenUtils.getScreenWidth(sketchImageView.context) * 1.5)
        sketchImageView.zoomer?.isReadMode = isLongHeightImage || isLongWidthImage
        if (isLongWidthImage) {
            longImageHeightOrWidth = width
        } else if (isLongHeightImage) {
            longImageHeightOrWidth = height
        }
        if (isLongHeightImage || isLongWidthImage) {

        } else {
            sketchImageView.scaleType = ImageView.ScaleType.CENTER_CROP
        }
        return isLongHeightImage || isLongWidthImage
    }

    override fun onTapCallback(onTapCallback: OnTapCallback) {
        sketchImageView.zoomer?.setOnViewTapListener { view, x, y ->
            onTapCallback.onTap(view, x, y)
        }
    }

    override fun onLongTapCallback(onLongTapCallback: OnLongTapCallback) {
        sketchImageView.zoomer?.setOnViewLongPressListener { view, x, y ->
            onLongTapCallback.onLongTap(view, x, y)
        }
    }

    override fun pageChange(isHidden: Boolean) {

    }

}