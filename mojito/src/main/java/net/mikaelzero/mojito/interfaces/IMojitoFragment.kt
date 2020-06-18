package net.mikaelzero.mojito.interfaces

import android.content.Context

/**
 * @Author:         MikaelZero
 * @CreateDate:     2020/6/17 1:25 PM
 * @Description:
 */
interface IMojitoFragment {
    fun providerContext():Context?
    fun replaceImageUrl(url:String)
    fun backToMin()
}