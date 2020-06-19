package net.mikaelzero.app

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import net.mikaelzero.mojito.interfaces.CoverLayoutLoader
import net.mikaelzero.mojito.interfaces.IMojitoActivity
import net.mikaelzero.mojito.interfaces.IMojitoFragment

/**
 * @Author:         MikaelZero
 * @CreateDate:     2020/6/17 11:29 AM
 * @Description:
 */
class NumCoverLoader : CoverLayoutLoader {
    lateinit var view: View
    var numTv: TextView? = null
    override fun attach(context: IMojitoActivity) {
        view = LayoutInflater.from(context.getContext()).inflate(R.layout.num_cover_layout, null)
        numTv = view.findViewById(R.id.numTv)
    }

    override fun providerView(): View {
        return view
    }

    override fun move(moveX: Float, moveY: Float) {

    }

    override fun fingerRelease(isToMax: Boolean, isToMin: Boolean) {

    }

    @SuppressLint("SetTextI18n")
    override fun pageChange(iMojitoFragment: IMojitoFragment, totalSize: Int, position: Int) {
        numTv?.text = (position + 1).toString() + "/" + totalSize
    }

}