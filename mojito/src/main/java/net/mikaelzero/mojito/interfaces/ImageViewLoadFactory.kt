package net.mikaelzero.mojito.interfaces

import android.net.Uri
import android.view.View
import androidx.annotation.DrawableRes
import net.mikaelzero.mojito.loader.ContentLoader

interface ImageViewLoadFactory {
    fun loadSillContent(view: View, uri: Uri)
    fun loadContentFail(view: View, @DrawableRes drawableResId: Int)
    fun newContentLoader(): ContentLoader
}