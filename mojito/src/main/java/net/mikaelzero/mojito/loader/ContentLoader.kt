package net.mikaelzero.mojito.loader

import android.content.Context
import android.graphics.RectF
import android.view.View
import net.mikaelzero.mojito.interfaces.ContentType

/**
 * @Author: MikaelZero
 * @CreateDate: 2020/6/10 9:54 AM
 * @Description:
 */
interface ContentLoader {
    val displayRect: RectF
    val contentType: ContentType
    fun init(context: Context)
    fun providerView(): View
    fun providerRealView(): View
    fun dispatchTouchEvent(isDrag:Boolean,isActionUp:Boolean,isDown:Boolean,isRight:Boolean): Boolean
    fun dragging(width: Int, height: Int, ratio: Float)
    fun beginBackToMin()
    fun loadAnimFinish()
    fun needReBuildSize():Boolean
    fun isLongImage(width: Int, height: Int):Boolean
}