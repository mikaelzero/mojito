package net.mikaelzero.mojito.impl

import net.mikaelzero.mojito.interfaces.IMojitoConfig

/**
 * @Author:         MikaelZero
 * @CreateDate:     2020/6/14 8:58 PM
 * @Description:
 */
open class DefaultMojitoConfig : IMojitoConfig {
    override fun duration(): Long = 250
    override fun maxTransYRatio(): Float = 0.16f
    override fun errorDrawableResId(): Int = 0
    override fun transparentNavigationBar(): Boolean = true
}