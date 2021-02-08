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

package net.mikaelzero.mojito.view.sketch.core.viewfun;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import net.mikaelzero.mojito.view.sketch.core.request.ImageFrom;
import net.mikaelzero.mojito.view.sketch.core.shaper.ImageShaper;
import net.mikaelzero.mojito.view.sketch.core.zoom.ImageZoomer;


/**
 * 这个类负责提供各种 function 开关和属性设置
 */
public abstract class FunctionPropertyView extends FunctionCallbackView {

    public FunctionPropertyView(@NonNull Context context) {
        super(context);
    }

    public FunctionPropertyView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public FunctionPropertyView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    /**
     * 是否开启了手势缩放功能
     */
    @Override
    public boolean isZoomEnabled() {
        return getFunctions().zoomFunction != null;
    }

    /**
     * 开启手势缩放功能
     */
    public void setZoomEnabled(boolean enabled) {
        if (enabled == isZoomEnabled()) {
            return;
        }

        if (enabled) {
            ImageZoomFunction zoomFunction = new ImageZoomFunction(this);
            zoomFunction.onDrawableChanged("setZoomEnabled", null, getDrawable());

            getFunctions().zoomFunction = zoomFunction;
        } else {
            getFunctions().zoomFunction.recycle("setZoomEnabled");

            getFunctions().zoomFunction = null;
        }
    }

    /**
     * 获取缩放功能控制对象
     *
     * @return null：没有开启缩放功能，请先执行 {@link #setZoomEnabled(boolean)} 开启
     */
    @Nullable
    public ImageZoomer getZoomer() {
        return getFunctions().zoomFunction != null ? getFunctions().zoomFunction.getZoomer() : null;
    }
}
