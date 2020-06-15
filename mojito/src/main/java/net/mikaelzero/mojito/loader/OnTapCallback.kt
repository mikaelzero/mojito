package net.mikaelzero.mojito.loader

import android.view.View

/**
 * @Author:         MikaelZero
 * @CreateDate:     2020/6/15 10:35 AM
 * @Description:
 */
interface OnTapCallback {
    fun onTap(view: View,x:Float,y:Float)
}