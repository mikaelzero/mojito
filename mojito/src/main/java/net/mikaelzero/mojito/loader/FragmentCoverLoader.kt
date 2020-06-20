package net.mikaelzero.mojito.loader

import android.view.View
import net.mikaelzero.mojito.interfaces.IMojitoFragment

/**
 * @Author:         MikaelZero
 * @CreateDate:     2020/6/18 10:54 AM
 * @Description:   每个fragment都会出现
 */
interface FragmentCoverLoader  {
    //对于查看原图的情况  如果 autoLoadTarget 为 true  需要对view进行隐藏
    fun attach(iMojitoFragment: IMojitoFragment, autoLoadTarget: Boolean): View?
    fun imageCacheHandle(isCache: Boolean,hasTargetUrl:Boolean)

    /**
     * 拖动的时候  移动的 X 和 Y 距离
     */
    fun move(moveX: Float, moveY: Float)

    /**
     * 手指松开后的状态
     */
    fun fingerRelease(isToMax: Boolean, isToMin: Boolean)
}