package net.mikaelzero.mojito.loader.glide

import com.bumptech.glide.Glide
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader
import com.bumptech.glide.load.model.GlideUrl
import okhttp3.*
import okio.*
import java.io.IOException
import java.io.InputStream
import java.util.*

object GlideProgressSupport {
    private fun createInterceptor(listener: ResponseProgressListener): Interceptor {
        return Interceptor { chain: Interceptor.Chain ->
            val request = chain.request()
            val response = chain.proceed(request)
            response.newBuilder()
                .body(
                    OkHttpProgressResponseBody(
                        request.url, response.body,
                        listener
                    )
                )
                .build()
        }
    }

    fun init(glide: Glide, okHttpClient: OkHttpClient?) {
        val builder: OkHttpClient.Builder = okHttpClient?.newBuilder() ?: OkHttpClient.Builder()
        builder.addNetworkInterceptor(
            createInterceptor(
                DispatchingProgressListener()
            )
        )
        glide.registry.replace(
            GlideUrl::class.java, InputStream::class.java,
            OkHttpUrlLoader.Factory(builder.build())
        )
    }

    @JvmStatic
    fun forget(url: String) {
        DispatchingProgressListener.forget(url)
    }

    @JvmStatic
    fun expect(url: String, listener: ProgressListener?) {
        DispatchingProgressListener.expect(url, listener)
    }

    interface ProgressListener {
        fun onDownloadStart()
        fun onProgress(progress: Int)
        fun onDownloadFinish()
    }

    private interface ResponseProgressListener {
        fun update(
            url: HttpUrl,
            bytesRead: Long,
            contentLength: Long
        )
    }

    private class DispatchingProgressListener : ResponseProgressListener {
        override fun update(
            url: HttpUrl,
            bytesRead: Long,
            contentLength: Long
        ) {
            val key = getRawKey(url.toString())
            val listener =
                LISTENERS[key] ?: return
            val lastProgress = PROGRESSES[key]
            if (lastProgress == null) {
                // ensure `onStart` is called before `onProgress` and `onFinish`
                listener.onDownloadStart()
            }
            if (contentLength <= bytesRead) {
                listener.onDownloadFinish()
                forget(key)
                return
            }
            val progress = (bytesRead.toFloat() / contentLength * 100).toInt()
            if (lastProgress == null || progress != lastProgress) {
                PROGRESSES[key] = progress
                listener.onProgress(progress)
            }
        }

        companion object {
            private val LISTENERS: MutableMap<String, ProgressListener?> =
                HashMap()
            private val PROGRESSES: MutableMap<String, Int> =
                HashMap()
            private const val URL_QUERY_PARAM_START = "\\?"
            fun forget(url: String) {
                LISTENERS.remove(
                    getRawKey(
                        url
                    )
                )
                PROGRESSES.remove(
                    getRawKey(
                        url
                    )
                )
            }

            fun expect(
                url: String,
                listener: ProgressListener?
            ) {
                LISTENERS[getRawKey(
                    url
                )] = listener
            }

            private fun getRawKey(formerKey: String): String {
                return formerKey.split(URL_QUERY_PARAM_START.toRegex())
                    .toTypedArray()[0]
            }
        }
    }

    private class OkHttpProgressResponseBody internal constructor(
        private val mUrl: HttpUrl, private val mResponseBody: ResponseBody?,
        private val mProgressListener: ResponseProgressListener
    ) : ResponseBody() {
        private var mBufferedSource: BufferedSource? = null
        override fun contentType(): MediaType? {
            return mResponseBody!!.contentType()
        }

        override fun contentLength(): Long {
            return mResponseBody!!.contentLength()
        }

        override fun source(): BufferedSource {
            if (mBufferedSource == null) {
                mBufferedSource = source(mResponseBody!!.source()).buffer()
            }
            return mBufferedSource!!
        }

        private fun source(source: Source): Source {
            return object : ForwardingSource(source) {
                private var mTotalBytesRead = 0L

                @Throws(IOException::class)
                override fun read(sink: Buffer, byteCount: Long): Long {
                    val bytesRead = super.read(sink, byteCount)
                    val fullLength = mResponseBody!!.contentLength()
                    if (bytesRead == -1L) { // this source is exhausted
                        mTotalBytesRead = fullLength
                    } else {
                        mTotalBytesRead += bytesRead
                    }
                    mProgressListener.update(mUrl, mTotalBytesRead, fullLength)
                    return bytesRead
                }
            }
        }

    }
}