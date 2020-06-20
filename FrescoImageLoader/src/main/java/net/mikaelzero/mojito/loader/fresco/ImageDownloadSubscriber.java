package net.mikaelzero.mojito.loader.fresco;

import android.content.Context;

import androidx.annotation.WorkerThread;

import com.facebook.common.memory.PooledByteBuffer;
import com.facebook.common.memory.PooledByteBufferInputStream;
import com.facebook.common.references.CloseableReference;
import com.facebook.datasource.BaseDataSubscriber;
import com.facebook.datasource.DataSource;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public abstract class ImageDownloadSubscriber
        extends BaseDataSubscriber<CloseableReference<PooledByteBuffer>> {
    private static int sCounter = 0;

    private final File mTempFile;

    private volatile boolean mFinished;

    public ImageDownloadSubscriber(Context context) {
        // no need for any file extension, use a counter to avoid conflict.
        mTempFile =
            new File(context.getCacheDir(), System.currentTimeMillis() + "_" + nextCounter());
    }

    private static synchronized int nextCounter() {
        sCounter++;
        return sCounter;
    }

    @Override
    public void onProgressUpdate(DataSource<CloseableReference<PooledByteBuffer>> dataSource) {
        if (!mFinished) {
            onProgress((int) (dataSource.getProgress() * 100));
        }
    }

    @Override
    protected void onNewResultImpl(DataSource<CloseableReference<PooledByteBuffer>> dataSource) {
        if (!dataSource.isFinished()) {
            return;
        }

        CloseableReference<PooledByteBuffer> closeableRef = dataSource.getResult();
        // if we try to retrieve image file by cache key, it will return null
        // so we need to create a temp file, little bit hack :(
        PooledByteBufferInputStream inputStream = null;
        FileOutputStream outputStream = null;
        try {
            if (closeableRef != null) {
                inputStream = new PooledByteBufferInputStream(closeableRef.get());
                outputStream = new FileOutputStream(mTempFile);
                IOUtils.copy(inputStream, outputStream);

                mFinished = true;
                onSuccess(mTempFile);
            }
        } catch (IOException e) {
            onFail(e);
        } finally {
            CloseableReference.closeSafely(closeableRef);
            IOUtils.closeQuietly(inputStream);
            IOUtils.closeQuietly(outputStream);
        }
    }

    @Override
    protected void onFailureImpl(DataSource<CloseableReference<PooledByteBuffer>> dataSource) {
        mFinished = true;
        onFail(new RuntimeException("onFailureImpl"));
    }

    @WorkerThread
    protected abstract void onProgress(int progress);

    @WorkerThread
    protected abstract void onSuccess(File image);

    @WorkerThread
    protected abstract void onFail(Throwable t);
}
