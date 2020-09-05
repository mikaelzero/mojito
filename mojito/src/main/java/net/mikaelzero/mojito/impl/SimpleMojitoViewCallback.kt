package net.mikaelzero.mojito.impl

import android.util.Log
import android.view.View
import androidx.fragment.app.FragmentActivity
import net.mikaelzero.mojito.MojitoView
import net.mikaelzero.mojito.interfaces.OnMojitoListener

/**
 * @Author:         MikaelZero
 * @CreateDate:     2020/6/17 1:44 PM
 * @Description:
 */
 abstract class SimpleMojitoViewCallback : OnMojitoListener {
    override fun onClick(view: View, x: Float, y: Float, position: Int) {

    }

    override fun onLongClick(fragmentActivity: FragmentActivity?, view: View, x: Float, y: Float, position: Int) {
    }

    override fun onShowFinish(mojitoView: MojitoView, showImmediately: Boolean) {
    }

    override fun onMojitoViewFinish() {
    }

    override fun onDrag(view: MojitoView, moveX: Float, moveY: Float) {
    }

   override fun onLongImageMove(ratio: Float) {
   }

}