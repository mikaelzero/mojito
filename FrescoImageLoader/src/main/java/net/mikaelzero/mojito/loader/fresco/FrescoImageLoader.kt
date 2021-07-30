package net.mikaelzero.mojito.loader.fresco

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import com.facebook.binaryresource.FileBinaryResource
import com.facebook.datasource.DataSource
import com.facebook.drawee.backends.pipeline.DraweeConfig
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.imagepipeline.cache.DefaultCacheKeyFactory
import com.facebook.imagepipeline.core.DefaultExecutorSupplier
import com.facebook.imagepipeline.core.ImagePipelineConfig
import com.facebook.imagepipeline.core.ImagePipelineFactory
import com.facebook.imagepipeline.request.ImageRequest
import net.mikaelzero.mojito.loader.ImageLoader
import java.io.File

class FrescoImageLoader private constructor(private val mAppContext: Context) : ImageLoader {
    private val mExecutorSupplier: DefaultExecutorSupplier = DefaultExecutorSupplier(Runtime.getRuntime().availableProcessors())
    private val mFlyingRequestSources: MutableMap<Int, DataSource<*>> = HashMap(3)

    // we create a temp image file on cache miss to make it work,
    // so we need delete this temp image file when we are detached from window
    // (BigImageView will call cancel).
    private val mCacheMissTempFiles: MutableMap<Int, File?> = HashMap(3)
    @SuppressLint("WrongThread")
    override fun loadImage(requestId: Int, uri: Uri, onlyRetrieveFromCache: Boolean, callback: ImageLoader.Callback) {
        val request = ImageRequest.fromUri(uri)
        val localCache = getCacheFile(request)
        if (onlyRetrieveFromCache && !localCache.exists()) {
            callback.onFail(Exception(""))
            return
        }
        if (localCache.exists()) {
            mExecutorSupplier.forLocalStorageRead().execute { callback.onSuccess(localCache) }
        } else {
            callback.onStart() // ensure `onStart` is called before `onProgress` and `onFinish`
            callback.onProgress(0) // show 0 progress immediately
            val pipeline = Fresco.getImagePipeline()
            val source = pipeline.fetchEncodedImage(request, true)
            source.subscribe(object : ImageDownloadSubscriber(mAppContext) {
                override fun onProgress(progress: Int) {
                    callback.onProgress(progress)
                }

                override fun onSuccess(image: File?) {
                    rememberTempFile(requestId, image)
                    callback.onFinish()
                    callback.onSuccess(image)
                }

                override fun onFail(t: Throwable?) {
                    t!!.printStackTrace()
                    callback.onFail(t as Exception?)
                }
            }, mExecutorSupplier.forBackgroundTasks())
            cancel(requestId)
            rememberSource(requestId, source)
        }
    }

    override fun prefetch(uri: Uri) {
        val pipeline = Fresco.getImagePipeline()
        pipeline.prefetchToDiskCache(
            ImageRequest.fromUri(uri),
            false
        ) // we don't need context, but avoid null
    }

    @Synchronized
    override fun cancel(requestId: Int) {
        closeSource(mFlyingRequestSources.remove(requestId))
        deleteTempFile(mCacheMissTempFiles.remove(requestId))
    }

    @Synchronized
    override fun cancelAll() {
        val sources: List<DataSource<*>> = ArrayList(mFlyingRequestSources.values)
        mFlyingRequestSources.clear()
        for (source in sources) {
            closeSource(source)
        }
        val tempFiles: List<File?> = ArrayList(mCacheMissTempFiles.values)
        mCacheMissTempFiles.clear()
        for (tempFile in tempFiles) {
            deleteTempFile(tempFile)
        }
    }

    override fun cleanCache() {
        val imagePipeline = Fresco.getImagePipeline()
        imagePipeline.clearMemoryCaches()
        imagePipeline.clearDiskCaches()
        imagePipeline.clearCaches()
    }

    @Synchronized
    private fun rememberSource(requestId: Int, source: DataSource<*>) {
        mFlyingRequestSources[requestId] = source
    }

    private fun closeSource(source: DataSource<*>?) {
        source?.close()
    }

    @Synchronized
    private fun rememberTempFile(requestId: Int, tempFile: File?) {
        mCacheMissTempFiles[requestId] = tempFile
    }

    private fun deleteTempFile(tempFile: File?) {
        tempFile?.delete()
    }

    private fun getCacheFile(request: ImageRequest?): File {
        val mainFileCache = ImagePipelineFactory
            .getInstance()
            .mainFileCache
        val cacheKey = DefaultCacheKeyFactory
            .getInstance()
            .getEncodedCacheKey(request, false) // we don't need context, but avoid null
        var cacheFile = request!!.sourceFile
        // http://crashes.to/s/ee10638fb31
        if (mainFileCache.hasKey(cacheKey) && mainFileCache.getResource(cacheKey) != null) {
            cacheFile = (mainFileCache.getResource(cacheKey) as FileBinaryResource).file
        }
        return cacheFile
    }

    companion object {
        @JvmOverloads
        fun with(
            appContext: Context,
            imagePipelineConfig: ImagePipelineConfig? = null, draweeConfig: DraweeConfig? = null
        ): FrescoImageLoader {
            Fresco.initialize(appContext, imagePipelineConfig, draweeConfig)
            return FrescoImageLoader(appContext)
        }
    }

}