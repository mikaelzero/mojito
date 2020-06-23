package net.mikaelzero.mojito.interfaces

import android.net.Uri
import android.view.View
import androidx.lifecycle.LifecycleOwner
import net.mikaelzero.mojito.loader.ContentLoader
import java.io.File

interface ImageViewLoadFactory {
    fun loadSillContent(view: View, uri: Uri)
    fun newContentLoader(): ContentLoader
}