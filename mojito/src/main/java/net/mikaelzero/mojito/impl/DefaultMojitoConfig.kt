package net.mikaelzero.mojito.impl

import net.mikaelzero.mojito.interfaces.IMojitoConfig

/**
 * @Author:         MikaelZero
 * @CreateDate:     2020/6/14 8:58 PM
 * @Description:
 */
class DefaultMojitoConfig : IMojitoConfig {
    override fun duration(): Long = 300
    override fun maxTransYRatio(): Float = 0.16f
}