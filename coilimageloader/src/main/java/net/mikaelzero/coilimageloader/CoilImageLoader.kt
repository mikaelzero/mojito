package net.mikaelzero.coilimageloader

import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build.VERSION.SDK_INT
import androidx.core.net.toFile
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.decode.SvgDecoder
import coil.request.Disposable
import coil.request.ImageRequest
import coil.util.CoilUtils
import net.mikaelzero.mojito.loader.ImageLoader
import okhttp3.Cache
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.OkHttpClient
import java.io.File
import java.util.ArrayList
import java.util.HashMap

/**
 * @Author:         MikaelZero
 * @CreateDate:     2021/7/23 11:07 上午
 * @Description:
 */
class CoilImageLoader private constructor(val context: Context) : ImageLoader {

    private fun getImageLoader(context: Context): coil.ImageLoader {
        return coil.ImageLoader.Builder(context)
            .crossfade(true)
            .componentRegistry {
                if (SDK_INT >= 28) {
                    add(ImageDecoderDecoder(context))
                } else {
                    add(GifDecoder())
                }
                add(SvgDecoder(context))
            }
            .okHttpClient {
                OkHttpClient.Builder()
                    .cache(CoilUtils.createDefaultCache(context))
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

    override fun loadImage(requestId: Int, uri: Uri?, onlyRetrieveFromCache: Boolean, callback: ImageLoader.Callback?) {
        val localCache = uri.toString().getCoilCacheFile()
        if (onlyRetrieveFromCache && (localCache == null || !localCache.exists())) {
            callback?.onFail(Exception(""))
            return
        }
        val imageLoader = getImageLoader(context)
        val request = ImageRequest.Builder(context)
            .data(uri)
            .memoryCacheKey(uri.toString())
            .target(object : ImageDownloadTarget(context, uri?.toString().orEmpty()) {
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

    private fun String?.toFile(): File? {
        if (this == null) {
            return null
        }
        val f = File(this)
        return if (f.exists()) f else null
    }

    fun Uri?.getCoilCacheFile(): File? {
        if (this?.scheme == "file" || this?.scheme == "content") {
            if (this.path == null) {
                return null
            }
            return File(this.path!!)
        } else {
            return this.toString().getCoilCacheFile()
        }
    }

    fun String?.getCoilCacheFile(): File? {
        return this?.toFile() ?: this?.toHttpUrlOrNull()?.let { u ->
            CoilUtils.createDefaultCache(context).directory.listFiles()?.lastOrNull { it.name.endsWith(".1") && it.name.contains(Cache.key(u)) }
        }
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
        getImageLoader(context).memoryCache.clear()
        CoilUtils.createDefaultCache(context).directory.delete()
    }


    companion object {
        fun with(context: Context): CoilImageLoader {
            return CoilImageLoader(context)
        }
    }
}