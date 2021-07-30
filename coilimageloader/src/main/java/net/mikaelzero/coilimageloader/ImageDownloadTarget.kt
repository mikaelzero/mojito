package net.mikaelzero.coilimageloader

import android.graphics.drawable.Drawable
import net.mikaelzero.coilimageloader.ProgressSupport.expect
import net.mikaelzero.coilimageloader.ProgressSupport.forget


abstract class ImageDownloadTarget constructor(private val mUrl: String) : coil.target.Target, ProgressSupport.ProgressListener {

    override fun onError(error: Drawable?) {
        super.onError(error)
        forget(mUrl)
    }

    override fun onStart(placeholder: Drawable?) {
        super.onStart(placeholder)
        expect(mUrl, this)
    }

    override fun onSuccess(result: Drawable) {
        super.onSuccess(result)
        forget(mUrl)
    }


}