package net.mikaelzero.mojito.interfaces

import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.View
import androidx.annotation.DrawableRes
import androidx.lifecycle.LifecycleOwner
import net.mikaelzero.mojito.loader.ContentLoader
import java.io.File

interface ImageViewLoadFactory {
    fun loadSillContent(view: View, uri: Uri)
    fun loadContentFail(view: View, @DrawableRes drawableResId: Int)
    fun newContentLoader(): ContentLoader
}