package net.moyokoo.diooto.tools;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

import me.panpf.sketch.decode.ImageAttrs;
import me.panpf.sketch.request.CancelCause;
import me.panpf.sketch.request.DisplayListener;
import me.panpf.sketch.request.ErrorCause;
import me.panpf.sketch.request.ImageFrom;

public abstract class DisplayListenerAdapter implements DisplayListener {
    @Override
    public void onStarted() {

    }

    @Override
    public void onError(@NonNull ErrorCause cause) {

    }

    @Override
    public void onCanceled(@NonNull CancelCause cause) {

    }

    @Override
    public void onCompleted(@NonNull Drawable drawable, @NonNull ImageFrom imageFrom, @NonNull ImageAttrs imageAttrs) {

    }
}
