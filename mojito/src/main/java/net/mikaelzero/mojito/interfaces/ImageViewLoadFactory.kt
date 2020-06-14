package net.mikaelzero.mojito.interfaces

import android.net.Uri
import android.view.View
import java.io.File

interface ImageViewLoadFactory {
    fun loadSillContent(view: View, uri: Uri)
    fun loadAnimatedContent(view: View, imageType: Int, imageFile: File)
}