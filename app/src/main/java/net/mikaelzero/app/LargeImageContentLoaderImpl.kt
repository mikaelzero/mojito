package net.mikaelzero.app

import android.content.Context
import android.graphics.Rect
import android.graphics.RectF
import android.util.Log
import android.view.View
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import net.mikaelzero.diooto.interfaces.ContentType
import net.mikaelzero.diooto.loader.ContentLoader
import kotlin.math.roundToInt

/**
 * @Author: MikaelZero
 * @CreateDate: 2020/6/10 10:01 AM
 * @Description:
 */
abstract class  LargeImageContentLoaderImpl : ContentLoader {
    lateinit var subsamplingScaleImageView: SubsamplingScaleImageView

    override fun providerRealView(): View {
        return subsamplingScaleImageView
    }

    override val currentScale: Float
        get() = subsamplingScaleImageView.scale

    override val displayRect: RectF
        get() {
            val rectF = Rect()
            subsamplingScaleImageView.visibleFileRect(rectF)
            return RectF(rectF)
        }

    override fun init(context: Context) {
        subsamplingScaleImageView = SubsamplingScaleImageView(context)
        subsamplingScaleImageView.minScale = 1f
        subsamplingScaleImageView.setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_CENTER_CROP)
    }

    override val contentType: ContentType =
        ContentType.IMAGE

    override val imageCurrentRatio: Float
        get() = subsamplingScaleImageView.scale / subsamplingScaleImageView.maxScale

    override fun dispatchTouchEvent(): Boolean {
        return (subsamplingScaleImageView.scale * 1000).roundToInt() / 1000f > 1
//         subsamplingScaleImageView.scale > subsamplingScaleImageView.minScale ||

    }

    override fun dragging(width: Int, height: Int, ratio: Float) {
        Log.e("ratio", "ratio:$ratio")
//        subsamplingScaleImageView.setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_CUSTOM)
//        subsamplingScaleImageView.minScale = ratio
//        subsamplingScaleImageView.setScaleAndCenter(ratio, PointF(0f,0f))

//        subsamplingScaleImageView.minScale = 1f
//        subsamplingScaleImageView.isZoomEnabled = false
//        subsamplingScaleImageView.setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_CENTER_CROP)
//        subsamplingScaleImageView.setScaleAndCenter(1f, PointF(0f, 0f))
    }

    override fun beginBackToMin() {}

    override fun loadFinish() {
        subsamplingScaleImageView.setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_CENTER_INSIDE)
//        subsamplingScaleImageView.setMinimumScaleType(SubsamplingScaleImageViewDragClose.SCALE_TYPE_CUSTOM)
//        subsamplingScaleImageView.minScale = 1f
//        subsamplingScaleImageView.setScaleAndCenter(1f, PointF(0f, 0f))
    }
}