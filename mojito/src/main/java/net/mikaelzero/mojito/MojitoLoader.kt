package net.mikaelzero.mojito

import android.net.Uri
import net.mikaelzero.mojito.interfaces.IMojitoConfig
import net.mikaelzero.mojito.interfaces.ImageViewLoadFactory
import net.mikaelzero.mojito.loader.ImageLoader

/**
 * @Author:         MikaelZero
 * @CreateDate:     2020/6/22 9:43 PM
 * @Description:
 */
class MojitoLoader {
    private object SingletonHolder {
        val holder = MojitoLoader()
    }

    companion object {
        val instance = SingletonHolder.holder

        @JvmStatic
        fun prefetch(vararg uris: Uri?) {
            val imageLoader = Mojito.imageLoader()
            for (uri in uris) {
                imageLoader?.prefetch(uri)
            }
        }

        @JvmStatic
        fun prefetch(vararg uris: String?) {
            val imageLoader = Mojito.imageLoader()
            for (uri in uris) {
                imageLoader?.prefetch(Uri.parse(uri))
            }
        }
    }

    var mImageLoader: ImageLoader? = null
    var imageViewLoadFactory: ImageViewLoadFactory? = null
    var mojitoConfig: IMojitoConfig? = null
}