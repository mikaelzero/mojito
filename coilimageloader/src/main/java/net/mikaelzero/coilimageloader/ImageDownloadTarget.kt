package net.mikaelzero.coilimageloader

import android.content.Context
import android.graphics.drawable.Drawable
import net.mikaelzero.coilimageloader.ProgressSupport.expect
import net.mikaelzero.coilimageloader.ProgressSupport.forget
import java.io.File


abstract class ImageDownloadTarget constructor(
    private val context: Context,
    private val mUrl: String,
) : coil.target.Target, ProgressSupport.ProgressListener {
    private var sCounter = 0
    private val mTempFile: File

    init {
        mTempFile = File(context.cacheDir, System.currentTimeMillis().toString() + "_" + nextCounter())
    }

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


    @Synchronized
    fun nextCounter(): Int {
        sCounter++
        return sCounter
    }
}