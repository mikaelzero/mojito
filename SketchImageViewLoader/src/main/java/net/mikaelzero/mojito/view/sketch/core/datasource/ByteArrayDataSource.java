/*
 * Copyright (C) 2019 Peng fei Pan <panpfpanpf@outlook.me>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.mikaelzero.mojito.view.sketch.core.datasource;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import net.mikaelzero.mojito.view.sketch.core.cache.BitmapPool;
import net.mikaelzero.mojito.view.sketch.core.decode.ImageAttrs;
import net.mikaelzero.mojito.view.sketch.core.decode.NotFoundGifLibraryException;
import net.mikaelzero.mojito.view.sketch.core.drawable.SketchGifDrawable;
import net.mikaelzero.mojito.view.sketch.core.drawable.SketchGifFactory;
import net.mikaelzero.mojito.view.sketch.core.request.ImageFrom;
import net.mikaelzero.mojito.view.sketch.core.util.SketchUtils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 用于读取字节数组格式的图片
 */
public class ByteArrayDataSource implements DataSource {

    @NonNull
    private byte[] data;
    @NonNull
    private ImageFrom imageFrom;

    public ByteArrayDataSource(@NonNull byte[] data, @NonNull ImageFrom imageFrom) {
        this.data = data;
        this.imageFrom = imageFrom;
    }

    @NonNull
    @Override
    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(data);
    }

    @Override
    public long getLength() throws IOException {
        return data.length;
    }

    @Override
    public File getFile(@Nullable File outDir, @Nullable String outName) throws IOException {
        if (outDir == null) {
            return null;
        }

        if (!outDir.exists() && !outDir.getParentFile().mkdirs()) {
            return null;
        }

        File outFile;
        if (!TextUtils.isEmpty(outName)) {
            outFile = new File(outDir, outName);
        } else {
            outFile = new File(outDir, SketchUtils.generatorTempFileName(this, String.valueOf(System.currentTimeMillis())));
        }

        InputStream inputStream = getInputStream();

        OutputStream outputStream;
        try {
            outputStream = new FileOutputStream(outFile);
        } catch (IOException e) {
            SketchUtils.close(inputStream);
            throw e;
        }

        byte[] data = new byte[1024];
        int length;
        try {
            while ((length = inputStream.read(data)) != -1) {
                outputStream.write(data, 0, length);
            }
        } finally {
            SketchUtils.close(outputStream);
            SketchUtils.close(inputStream);
        }

        return outFile;
    }

    @NonNull
    @Override
    public ImageFrom getImageFrom() {
        return imageFrom;
    }

    @NonNull
    @Override
    public SketchGifDrawable makeGifDrawable(@NonNull String key, @NonNull String uri, @NonNull ImageAttrs imageAttrs,
                                             @NonNull BitmapPool bitmapPool) throws IOException, NotFoundGifLibraryException {
        return SketchGifFactory.createGifDrawable(key, uri, imageAttrs, getImageFrom(), bitmapPool, data);
    }
}
