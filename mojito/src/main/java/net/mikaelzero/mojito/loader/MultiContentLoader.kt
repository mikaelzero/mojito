package net.mikaelzero.mojito.loader

import net.mikaelzero.mojito.interfaces.ImageViewLoadFactory

/**
 * @Author:         MikaelZero
 * @CreateDate:     2020/6/22 5:16 PM
 * @Description:
 */
interface MultiContentLoader {
    fun providerLoader(position: Int): ImageViewLoadFactory
    fun providerEnableTargetLoad(position: Int): Boolean
}