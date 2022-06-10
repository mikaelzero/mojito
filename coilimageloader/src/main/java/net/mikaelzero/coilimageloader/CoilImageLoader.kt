package net.mikaelzero.coilimageloader

import android.content.ContentResolver
import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build.VERSION.SDK_INT
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.decode.SvgDecoder
import coil.imageLoader
import coil.request.Disposable
import coil.request.ImageRequest
import okhttp3.OkHttpClient
import java.io.File
import kotlin.collections.set


/**
 * @Author:         MikaelZero
 * @CreateDate:     2021/7/23 11:07 上午
 * @Description:
 */
class CoilImageLoader private constructor(val context: Context) : net.mikaelzero.mojito.loader.ImageLoader {

    private val SCHEMES = setOf(
        ContentResolver.SCHEME_FILE,
        ContentResolver.SCHEME_ANDROID_RESOURCE,
        ContentResolver.SCHEME_CONTENT
    )

    private fun getImageLoader(context: Context): coil.ImageLoader {
        return coil.ImageLoader.Builder(context)
            .crossfade(true)
            .components {
                if (SDK_INT >= 28) {
                    add(ImageDecoderDecoder.Factory())
                } else {
                    add(GifDecoder.Factory())
                }
                add(SvgDecoder.Factory())
            }
            .okHttpClient {
                OkHttpClient.Builder()
                    .addInterceptor { chain ->
                        val request = chain.request()
                        val response = chain.proceed(request)
                        response.newBuilder()
                            .body(
                                ProgressSupport.OkHttpProgressResponseBody(
                                    request.url, response.body,
                                    ProgressSupport.DispatchingProgressListener()
                                )
                            )
                            .build()
                    }
                    .build()
            }
            .build()
    }

    private val mFlyingRequestTargets: MutableMap<Int, Disposable> = HashMap(3)

    override fun loadImage(requestId: Int, uri: Uri?, onlyRetrieveFromCache: Boolean, callback: net.mikaelzero.mojito.loader.ImageLoader.Callback?) {
        val localCache = uri.toString().getCoilCacheFile()
        if (onlyRetrieveFromCache && (localCache == null || !localCache.exists())) {
            callback?.onFail(Exception(""))
            return
        }
        val imageLoader = getImageLoader(context)
        val request = ImageRequest.Builder(context)
            .data(uri)
            .memoryCacheKey(uri.toString())
            .target(object : ImageDownloadTarget(uri?.toString().orEmpty()) {
                override fun onSuccess(result: Drawable) {
                    super.onSuccess(result)
                    val resource = uri.getCoilCacheFile()
                    if (resource?.exists() == true) {
                        callback?.onSuccess(resource)
                    } else {
                        callback?.onFail(CoilLoaderException(null))
                    }
                }

                override fun onError(error: Drawable?) {
                    super.onError(error)
                    callback?.onFail(CoilLoaderException(error))
                }

                override fun onDownloadStart() {
                    callback?.onStart()
                }

                override fun onProgress(progress: Int) {
                    callback?.onProgress(progress)
                }

                override fun onDownloadFinish() {
                    callback?.onFinish()
                }

            })
            .build()
        rememberTarget(requestId, imageLoader.enqueue(request))
    }

    fun Uri?.getCoilCacheFile(): File? {
        if (SCHEMES.contains(this?.scheme)) {
            if (this?.path == null) {
                return null
            }
            return File(this.path!!)
        } else {
            return this.toString().getCoilCacheFile()
        }
    }

    fun String?.getCoilCacheFile(): File? {
        return context.imageLoader.diskCache?.get(this.orEmpty())?.data?.toFile()
    }

    override fun prefetch(uri: Uri?) {
        val imageLoader = getImageLoader(context)
        val request = ImageRequest.Builder(context)
            .data(uri)
            .build()

        imageLoader.enqueue(request)
    }

    override fun cancel(requestId: Int) {
        clearTarget(mFlyingRequestTargets.remove(requestId))
    }

    private fun clearTarget(target: Disposable?) {
        target?.dispose()
    }

    @Synchronized
    private fun rememberTarget(requestId: Int, target: Disposable) {
        mFlyingRequestTargets[requestId] = target
    }

    override fun cancelAll() {
        val targets: List<Disposable> = ArrayList(mFlyingRequestTargets.values)
        for (target in targets) {
            clearTarget(target)
        }
    }

    override fun cleanCache() {
        getImageLoader(context).memoryCache?.clear()
        context.imageLoader.diskCache?.clear()
    }


    companion object {
        fun with(context: Context): CoilImageLoader {
            return CoilImageLoader(context)
        }
    }
}