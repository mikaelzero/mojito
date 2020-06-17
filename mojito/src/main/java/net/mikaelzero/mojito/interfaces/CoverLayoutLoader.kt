package net.mikaelzero.mojito.interfaces

import android.view.View

/**
 * @Author:         MikaelZero
 * @CreateDate:     2020/6/17 11:19 AM
 * @Description:
 */
interface CoverLayoutLoader {
    fun attach(context: IMojitoActivity)
    fun providerView(): View

    /**
     * 拖动的时候  移动的 X 和 Y 距离
     */
    fun move(moveX: Float, moveY: Float)

    /**
     * 手指松开后的状态
     */
    fun fingerRelease(isToMax: Boolean, isToMin: Boolean)
    fun pageChange(iMojitoFragment: IMojitoFragment,totalSize:Int, position: Int)
}