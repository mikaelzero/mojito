package net.mikaelzero.mojito.loader.glide

import android.graphics.drawable.Drawable
import com.bumptech.glide.request.Request
import com.bumptech.glide.request.target.SizeReadyCallback
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.bumptech.glide.util.Util
import java.io.File

class PrefetchTarget private constructor(private val width: Int, private val height: Int) :
    Target<File> {
    private var request: Request? = null

    constructor() : this(
        Target.SIZE_ORIGINAL,
        Target.SIZE_ORIGINAL
    ) {
    }

    override fun onResourceReady(
        resource: File,
        transition: Transition<in File>?
    ) {
        // not interested in result
    }

    /**
     * Immediately calls the given callback with the sizes given in the constructor.
     *
     * @param cb {@inheritDoc}
     */
    override fun getSize(cb: SizeReadyCallback) {
        require(Util.isValidDimensions(width, height)) {
            ("Width and height must both be > 0 or Target#SIZE_ORIGINAL, but given" + " width: "
                    + width + " and height: " + height + ", either provide dimensions in the constructor"
                    + " or call override()")
        }
        cb.onSizeReady(width, height)
    }

    override fun removeCallback(cb: SizeReadyCallback) {
        // Do nothing, we never retain a reference to the callback.
    }

    override fun setRequest(request: Request?) {
        this.request = request
    }

    override fun getRequest(): Request? {
        return request
    }

    override fun onLoadCleared(placeholder: Drawable?) {
        // Do nothing.
    }

    override fun onLoadStarted(placeholder: Drawable?) {
        // Do nothing.
    }

    override fun onLoadFailed(errorDrawable: Drawable?) {
        // Do nothing.
    }

    override fun onStart() {
        // Do nothing.
    }

    override fun onStop() {
        // Do nothing.
    }

    override fun onDestroy() {
        // Do nothing.
    }

}