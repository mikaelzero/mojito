package net.mikaelzero.mojito

import android.content.Context
import net.mikaelzero.mojito.impl.DefaultMojitoConfig
import net.mikaelzero.mojito.interfaces.IMojitoConfig
import net.mikaelzero.mojito.interfaces.ImageViewLoadFactory
import net.mikaelzero.mojito.loader.ImageLoader


class Mojito {

    companion object {
        @JvmStatic
        fun with(context: Context?): MojitoWrapper {
            return MojitoWrapper(context)
        }

        @JvmStatic
        fun initialize(
            imageLoader: ImageLoader,
            imageViewLoadFactory: ImageViewLoadFactory
        ) {
            MojitoLoader.instance.mImageLoader = imageLoader
            MojitoLoader.instance.imageViewLoadFactory = imageViewLoadFactory
        }

        @JvmStatic
        fun initialize(
            imageLoader: ImageLoader,
            imageViewLoadFactory: ImageViewLoadFactory,
            mojitoConfig: IMojitoConfig
        ) {
            MojitoLoader.instance.mImageLoader = imageLoader
            MojitoLoader.instance.imageViewLoadFactory = imageViewLoadFactory
            MojitoLoader.instance.mojitoConfig = mojitoConfig
        }

        @JvmStatic
        fun imageLoader(): ImageLoader? {
            return MojitoLoader.instance.mImageLoader
        }

        @JvmStatic
        fun imageViewFactory(): ImageViewLoadFactory? {
            return MojitoLoader.instance.imageViewLoadFactory
        }

        @JvmStatic
        fun mojitoConfig(): IMojitoConfig {
            if (MojitoLoader.instance.mojitoConfig == null) {
                MojitoLoader.instance.mojitoConfig = DefaultMojitoConfig()
            }
            return MojitoLoader.instance.mojitoConfig!!
        }

        @JvmStatic
        fun cleanCache() {
            MojitoLoader.instance.mImageLoader?.cleanCache()
        }


        fun clean() {
            imageLoader()?.cancelAll()
        }
    }
}