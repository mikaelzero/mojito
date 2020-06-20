package net.mikaelzero.mojito.impl

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.gyf.immersionbar.ImmersionBar
import net.mikaelzero.mojito.R
import net.mikaelzero.mojito.interfaces.IMojitoFragment
import net.mikaelzero.mojito.loader.FragmentCoverLoader

/**
 * @Author:         MikaelZero
 * @CreateDate:     2020/6/18 10:59 AM
 * @Description:
 */
class DefaultTargetFragmentCover : FragmentCoverLoader {
    var view: View? = null
    override fun attach(iMojitoFragment: IMojitoFragment, autoLoadTarget: Boolean): View? {
        if (autoLoadTarget) {
            return null
        }
        view = LayoutInflater.from(iMojitoFragment.providerContext()?.context).inflate(R.layout.default_target_cover_layout, null)
        view?.setPadding(0, ImmersionBar.getStatusBarHeight(iMojitoFragment.providerContext()!!), 0, 0)
        val seeTargetImageTv = view?.findViewById<TextView>(R.id.seeTargetImageTv)
        seeTargetImageTv?.setOnClickListener {
            iMojitoFragment.loadTargetUrl()
        }
        view?.visibility = View.GONE
        return view
    }

    // true  has cache
    override fun imageCacheHandle(isCache: Boolean, hasTargetUrl: Boolean) {
        if (hasTargetUrl) {
            if (isCache) {
                view?.visibility = View.GONE
            } else {
                view?.visibility = View.VISIBLE
            }
        } else {
            view?.visibility = View.GONE
        }
    }

    override fun fingerRelease(isToMax: Boolean, isToMin: Boolean) {
        if (view == null || view!!.visibility == View.GONE) {
            return
        }
    }


    override fun move(moveX: Float, moveY: Float) {
        if (view == null || view!!.visibility == View.GONE) {
            return
        }
        Log.e("move", "move move move")
    }
}