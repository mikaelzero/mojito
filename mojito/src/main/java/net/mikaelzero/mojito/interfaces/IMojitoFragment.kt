package net.mikaelzero.mojito.interfaces

import androidx.fragment.app.Fragment

/**
 * @Author:         MikaelZero
 * @CreateDate:     2020/6/17 1:25 PM
 * @Description:
 */
interface IMojitoFragment {
    fun providerContext(): Fragment?
    fun loadTargetUrl()
    fun backToMin()
}