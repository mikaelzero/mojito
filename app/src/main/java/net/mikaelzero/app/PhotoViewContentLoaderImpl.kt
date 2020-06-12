package net.mikaelzero.app

import android.content.Context
import android.graphics.Color
import android.graphics.RectF
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import com.github.chrisbanes.photoview.PhotoView
import net.mikaelzero.diooto.interfaces.ContentType
import net.mikaelzero.diooto.loader.ContentLoader

/**
 * @Author: MikaelZero
 * @CreateDate: 2020/6/10 10:01 AM
 * @Description:
 */
class PhotoViewContentLoaderImpl : ContentLoader {
    lateinit var photoView: PhotoView

    override fun providerRealView(): PhotoView {
        return photoView
    }

    override fun providerView(): View {
        return photoView
    }

    override val currentScale: Float
        get() = photoView.scale

    override val displayRect: RectF
        get() = photoView.displayRect

    override fun init(context: Context) {
        photoView = PhotoView(context)
        photoView.scaleType = ImageView.ScaleType.CENTER_CROP
    }


    override val contentType: ContentType
        get() = ContentType.IMAGE

    override val imageCurrentRatio: Float
        get() = photoView.scale

    override fun dispatchTouchEvent(): Boolean {
        return photoView.scale > photoView.minimumScale
    }

    override fun dragging(width: Int, height: Int, ratio: Float) {
    }

    override fun beginBackToMin() {
        //TODO 如果放大的情况下   向左平移  设置crop会马上变成中间的截取的图片 待优化
        photoView.scaleType = ImageView.ScaleType.CENTER_CROP
    }

    override fun loadFinish() {
        photoView.scaleType = ImageView.ScaleType.FIT_CENTER
    }
}