package net.mikaelzero.mojito.loader;

import java.io.File;

/**
 * @Author: MikaelZero
 * @CreateDate: 2020/6/9 8:41 PM
 * @Description:
 */
public class DefaultImageCallback implements ImageLoader.Callback {
    @Override
    public void onCacheHit(int imageType, File image) {

    }

    @Override
    public void onCacheMiss(int imageType, File image) {

    }

    @Override
    public void onStart() {

    }

    @Override
    public void onProgress(int progress) {

    }

    @Override
    public void onFinish() {

    }

    @Override
    public void onSuccess(File image) {

    }

    @Override
    public void onFail(Exception error) {

    }
}
