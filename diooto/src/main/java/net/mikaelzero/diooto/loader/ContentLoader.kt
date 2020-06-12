package net.mikaelzero.diooto.loader

import android.content.Context
import android.graphics.RectF
import android.view.View
import net.mikaelzero.diooto.interfaces.ContentType

/**
 * @Author: MikaelZero
 * @CreateDate: 2020/6/10 9:54 AM
 * @Description:
 */
interface ContentLoader {
    val currentScale: Float
    val displayRect: RectF
    val contentType: ContentType
    val imageCurrentRatio: Float
    fun init(context: Context)
    fun providerView(): View
    fun providerRealView(): View
    fun dispatchTouchEvent(): Boolean
    fun dragging(width: Int, height: Int, ratio: Float)
    fun beginBackToMin()
    fun loadFinish()
}