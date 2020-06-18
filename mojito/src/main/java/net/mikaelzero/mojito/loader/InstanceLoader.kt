package net.mikaelzero.mojito.loader

/**
 * @Author:         MikaelZero
 * @CreateDate:     2020/6/18 9:45 AM
 * @Description:
 */
interface InstanceLoader<T> {
    fun providerInstance(): T
}