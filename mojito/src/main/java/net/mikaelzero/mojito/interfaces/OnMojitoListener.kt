package net.mikaelzero.mojito.interfaces

import android.view.View
import androidx.fragment.app.FragmentActivity
import net.mikaelzero.mojito.MojitoView

/**
 * @Author:         MikaelZero
 * @CreateDate:     2020/6/17 1:39 PM
 * @Description:
 */
interface OnMojitoListener {
    fun onClick(view: View, x: Float, y: Float, position: Int)
    fun onLongClick(fragmentActivity: FragmentActivity?, view: View, x: Float, y: Float, position: Int)
    fun onShowFinish(mojitoView: MojitoView, showImmediately: Boolean)
    fun onMojitoViewFinish()
    fun onDrag(view: MojitoView, moveX: Float, moveY: Float)
    fun onLongImageMove(ratio: Float)
}