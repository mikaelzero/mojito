package net.mikaelzero.mojito.loader

/**
 * @Author: MikaelZero
 * @CreateDate: 2020/6/13 1:11 PM
 * @Description:
 */
 interface IContentViewImplFactory {
    fun newInstance(): ContentLoader
}