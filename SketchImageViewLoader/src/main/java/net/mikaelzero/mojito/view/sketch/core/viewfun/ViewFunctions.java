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

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import net.mikaelzero.mojito.view.sketch.core.decode.ImageAttrs;
import net.mikaelzero.mojito.view.sketch.core.request.CancelCause;
import net.mikaelzero.mojito.view.sketch.core.request.ErrorCause;
import net.mikaelzero.mojito.view.sketch.core.request.ImageFrom;
import net.mikaelzero.mojito.view.sketch.core.uri.UriModel;

class ViewFunctions {
    @NonNull
    RequestFunction requestFunction;

    @Nullable
    ImageZoomFunction zoomFunction;

    ViewFunctions(FunctionCallbackView view) {
        requestFunction = new RequestFunction(view);

    }

    void onAttachedToWindow() {
        if (requestFunction != null) {
            requestFunction.onAttachedToWindow();
        }

        if (zoomFunction != null) {
            zoomFunction.onAttachedToWindow();
        }
    }

    void onLayout(boolean changed, int left, int top, int right, int bottom) {


        if (requestFunction != null) {
            requestFunction.onLayout(changed, left, top, right, bottom);
        }
        if (zoomFunction != null) {
            zoomFunction.onLayout(changed, left, top, right, bottom);
        }

    }

    void onSizeChanged(int w, int h, int oldw, int oldh) {
        if (requestFunction != null) {
            requestFunction.onSizeChanged(w, h, oldw, oldh);
        }

        if (zoomFunction != null) {
            zoomFunction.onSizeChanged(w, h, oldw, oldh);
        }
    }

    void onDraw(Canvas canvas) {
        if (zoomFunction != null) {
            zoomFunction.onDraw(canvas);
        }


        if (requestFunction != null) {
            requestFunction.onDraw(canvas);
        }
    }

    /**
     * @return true：事件已处理
     */
    boolean onTouchEvent(MotionEvent event) {


        if (requestFunction != null && requestFunction.onTouchEvent(event)) {
            return true;
        }
        //noinspection RedundantIfStatement
        if (zoomFunction != null && zoomFunction.onTouchEvent(event)) {
            return true;
        }
        return false;
    }

    /**
     * @return true：需要调用invalidate()刷新view
     */
    boolean onDrawableChanged(String callPosition, Drawable oldDrawable, Drawable newDrawable) {
        boolean needInvokeInvalidate = false;

        if (requestFunction != null) {
            //noinspection ConstantConditions
            needInvokeInvalidate |= requestFunction.onDrawableChanged(callPosition, oldDrawable, newDrawable);
        }

        if (zoomFunction != null) {
            needInvokeInvalidate |= zoomFunction.onDrawableChanged(callPosition, oldDrawable, newDrawable);
        }

        return needInvokeInvalidate;
    }

    /**
     * @return true：需要设置drawable为null
     */
    boolean onDetachedFromWindow() {
        boolean needSetImageNull = false;

        if (requestFunction != null) {
            //noinspection ConstantConditions
            needSetImageNull |= requestFunction.onDetachedFromWindow();
        }

        if (zoomFunction != null) {
            needSetImageNull |= zoomFunction.onDetachedFromWindow();
        }

        return needSetImageNull;
    }

    /**
     * @return true：需要调用invalidate()刷新view
     */
    boolean onReadyDisplay(@Nullable UriModel uriModel) {
        boolean needInvokeInvalidate = false;

        if (requestFunction != null) {
            //noinspection ConstantConditions
            needInvokeInvalidate |= requestFunction.onReadyDisplay(uriModel);
        }

        if (zoomFunction != null) {
            needInvokeInvalidate |= zoomFunction.onReadyDisplay(uriModel);
        }


        return needInvokeInvalidate;
    }

    /**
     * @return true：需要调用invalidate()刷新view
     */
    boolean onDisplayStarted() {
        boolean needInvokeInvalidate = false;


        if (requestFunction != null) {
            needInvokeInvalidate |= requestFunction.onDisplayStarted();
        }

        if (zoomFunction != null) {
            needInvokeInvalidate |= zoomFunction.onDisplayStarted();
        }


        return needInvokeInvalidate;
    }

    /**
     * @return true：需要调用invalidate()刷新view
     */
    boolean onDisplayCompleted(@NonNull Drawable drawable, @NonNull ImageFrom imageFrom, @NonNull ImageAttrs imageAttrs) {
        boolean needInvokeInvalidate = false;



        if (requestFunction != null) {
            needInvokeInvalidate |= requestFunction.onDisplayCompleted(drawable, imageFrom, imageAttrs);
        }

        if (zoomFunction != null) {
            needInvokeInvalidate |= zoomFunction.onDisplayCompleted(drawable, imageFrom, imageAttrs);
        }

        return needInvokeInvalidate;
    }

    /**
     * @return true：需要调用invalidate()刷新view
     */
    boolean onDisplayError(@NonNull ErrorCause errorCause) {
        boolean needInvokeInvalidate = false;

        if (requestFunction != null) {
            needInvokeInvalidate |= requestFunction.onDisplayError(errorCause);
        }

        if (zoomFunction != null) {
            needInvokeInvalidate |= zoomFunction.onDisplayError(errorCause);
        }

        return needInvokeInvalidate;
    }

    /**
     * @return true：需要调用invalidate()刷新view
     */
    boolean onDisplayCanceled(@NonNull CancelCause cancelCause) {
        boolean needInvokeInvalidate = false;

        if (requestFunction != null) {
            needInvokeInvalidate |= requestFunction.onDisplayCanceled(cancelCause);
        }

        if (zoomFunction != null) {
            needInvokeInvalidate |= zoomFunction.onDisplayCanceled(cancelCause);
        }

        return needInvokeInvalidate;
    }

    /**
     * @return true：需要调用invalidate()刷新view
     */
    boolean onUpdateDownloadProgress(int totalLength, int completedLength) {
        boolean needInvokeInvalidate = false;

        if (requestFunction != null) {
            needInvokeInvalidate |= requestFunction.onUpdateDownloadProgress(totalLength, completedLength);
        }

        if (zoomFunction != null) {
            needInvokeInvalidate |= zoomFunction.onUpdateDownloadProgress(totalLength, completedLength);
        }
       

        return needInvokeInvalidate;
    }
}
