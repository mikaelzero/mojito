package net.mikaelzero.mojito.loader.fresco

import android.content.Context
import androidx.annotation.WorkerThread
import com.facebook.common.memory.PooledByteBuffer
import com.facebook.common.memory.PooledByteBufferInputStream
import com.facebook.common.references.CloseableReference
import com.facebook.datasource.BaseDataSubscriber
import com.facebook.datasource.DataSource
import net.mikaelzero.mojito.loader.fresco.IOUtils.closeQuietly
import net.mikaelzero.mojito.loader.fresco.IOUtils.copy
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

abstract class ImageDownloadSubscriber(context: Context) : BaseDataSubscriber<CloseableReference<PooledByteBuffer?>?>() {
    private val mTempFile: File = File(context.cacheDir, System.currentTimeMillis().toString() + "_" + nextCounter())

    @Volatile
    private var mFinished = false
    override fun onProgressUpdate(dataSource: DataSource<CloseableReference<PooledByteBuffer?>?>) {
        if (!mFinished) {
            onProgress((dataSource.progress * 100).toInt())
        }
    }

    override fun onNewResultImpl(dataSource: DataSource<CloseableReference<PooledByteBuffer?>?>) {
        if (!dataSource.isFinished) {
            return
        }
        val closeableRef = dataSource.result
        // if we try to retrieve image file by cache key, it will return null
        // so we need to create a temp file, little bit hack :(
        var inputStream: PooledByteBufferInputStream? = null
        var outputStream: FileOutputStream? = null
        try {
            if (closeableRef != null) {
                inputStream = PooledByteBufferInputStream(closeableRef.get())
                outputStream = FileOutputStream(mTempFile)
                copy(inputStream, outputStream)
                mFinished = true
                onSuccess(mTempFile)
            }
        } catch (e: IOException) {
            onFail(e)
        } finally {
            CloseableReference.closeSafely(closeableRef)
            closeQuietly(inputStream)
            closeQuietly(outputStream)
        }
    }

    override fun onFailureImpl(dataSource: DataSource<CloseableReference<PooledByteBuffer?>?>) {
        mFinished = true
        onFail(RuntimeException("onFailureImpl"))
    }

    @WorkerThread
    protected abstract fun onProgress(progress: Int)

    @WorkerThread
    protected abstract fun onSuccess(image: File?)

    @WorkerThread
    protected abstract fun onFail(t: Throwable?)

    companion object {
        private var sCounter = 0

        @Synchronized
        private fun nextCounter(): Int {
            sCounter++
            return sCounter
        }
    }
}