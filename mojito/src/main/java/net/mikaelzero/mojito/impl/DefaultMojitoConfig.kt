package net.mikaelzero.mojito.impl

import net.mikaelzero.mojito.interfaces.IMojitoConfig

/**
 * @Author:         MikaelZero
 * @CreateDate:     2020/6/14 8:58 PM
 * @Description:
 */
class DefaultMojitoConfig : IMojitoConfig {
    override fun dragMode(): Int = IMojitoConfig.DRAG_BOTH_BOTTOM_TOP
    override fun duration(): Long = 250
}