package net.mikaelzero.mojito.interfaces

import android.content.Context

/**
 * @Author:         MikaelZero
 * @CreateDate:     2020/6/17 3:26 PM
 * @Description:
 */
interface IMojitoActivity {
    fun getCurrentFragment(): IMojitoFragment
    fun getContext():Context
}