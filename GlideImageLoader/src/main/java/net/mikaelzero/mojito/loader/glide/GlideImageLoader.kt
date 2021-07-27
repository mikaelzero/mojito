package net.mikaelzero.mojito.loader.glide

import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.Log
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import net.mikaelzero.mojito.loader.ImageInfoExtractor
import net.mikaelzero.mojito.loader.ImageLoader
import okhttp3.OkHttpClient
import java.io.File
import java.util.*

open class GlideImageLoader private constructor(val context: Context, okHttpClient: OkHttpClient?) : ImageLoader {
    private val mRequestManager: RequestManager
    private val mFlyingRequestTargets: MutableMap<Int, ImageDownloadTarget> = HashMap(3)

    override fun loadImage(requestId: Int, uri: Uri, onlyRetrieveFromCache: Boolean, callback: ImageLoader.Callback) {
        val target: ImageDownloadTarget = object : ImageDownloadTarget(uri.toString()) {
            override fun onResourceReady(resource: File, transition: Transition<in File>?) {
                super.onResourceReady(resource, transition)
                callback.onSuccess(resource)
            }

            override fun onLoadFailed(errorDrawable: Drawable?) {
                super.onLoadFailed(errorDrawable)
                callback.onFail(GlideLoaderException(errorDrawable))
            }

            override fun onDownloadStart() {
                callback.onStart()
            }

            override fun onProgress(progress: Int) {
                callback.onProgress(progress)
            }

            override fun onDownloadFinish() {
                callback.onFinish()
            }
        }
        rememberTarget(requestId, target)
        downloadImageInto(uri, target, onlyRetrieveFromCache)
    }

    override fun prefetch(uri: Uri) {
        downloadImageInto(uri, PrefetchTarget(), false)
    }

    @Synchronized
    override fun cancel(requestId: Int) {
        clearTarget(mFlyingRequestTargets.remove(requestId))
    }

    @Synchronized
    override fun cancelAll() {
        val targets: List<ImageDownloadTarget> = ArrayList(mFlyingRequestTargets.values)
        for (target in targets) {
            clearTarget(target)
        }
    }

    override fun cleanCache() {
        Glide.get(context).clearMemory()
        Thread(Runnable { Glide.get(context).clearDiskCache() }).start()
    }

    private fun downloadImageInto(uri: Uri?, target: Target<File>, onlyRetrieveFromCache: Boolean) {
        mRequestManager
            .downloadOnly()
            .onlyRetrieveFromCache(onlyRetrieveFromCache)
            .load(uri)
            .into(target)
    }


    @Synchronized
    private fun rememberTarget(requestId: Int, target: ImageDownloadTarget) {
        mFlyingRequestTargets[requestId] = target
    }

    private fun clearTarget(target: ImageDownloadTarget?) {
        if (target != null) {
            mRequestManager.clear(target)
        }
    }

    companion object {
        @JvmOverloads
        fun with(context: Context, okHttpClient: OkHttpClient? = null): GlideImageLoader {
            return GlideImageLoader(context, okHttpClient)
        }
    }

    init {
        GlideProgressSupport.init(Glide.get(context), okHttpClient)
        mRequestManager = Glide.with(context)
    }
}