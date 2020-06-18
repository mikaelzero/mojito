package net.mikaelzero.app

import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import net.mikaelzero.mojito.interfaces.IMojitoFragment
import net.mikaelzero.mojito.loader.ImageCoverLoader

/**
 * @Author:         MikaelZero
 * @CreateDate:     2020/6/18 10:59 AM
 * @Description:
 */
class TargetImageCover(val targetUrl: String) : ImageCoverLoader {
    var view: View? = null
    override fun attach(iMojitoFragment: IMojitoFragment): View? {
        view = LayoutInflater.from(iMojitoFragment.providerContext()!!).inflate(R.layout.target_cover_layout, null)
        val seeTargetImageTv = view?.findViewById<TextView>(R.id.seeTargetImageTv)
        seeTargetImageTv?.setOnClickListener {
            iMojitoFragment.replaceImageUrl(targetUrl)
        }
        return view
    }
}