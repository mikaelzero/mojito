package net.mikaelzero.mojito

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import net.mikaelzero.mojito.bean.ActivityConfig
import net.mikaelzero.mojito.impl.DefaultMojitoConfig
import net.mikaelzero.mojito.interfaces.IMojitoConfig
import net.mikaelzero.mojito.interfaces.ImageViewLoadFactory
import net.mikaelzero.mojito.loader.ImageLoader
import net.mikaelzero.mojito.tools.DataWrapUtil
import net.mikaelzero.mojito.ui.ImageMojitoActivity


class Mojito {

    companion object {
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
            DataWrapUtil.remove()
            imageLoader()?.cancelAll()
        }

        fun start(context: Context?, builder: MojitoBuilder.() -> Unit = {}) {
            val configBean = MojitoBuilder().apply(builder).build()
            ImageMojitoActivity.hasShowedAnimMap[configBean.position] = false
            DataWrapUtil.put(configBean)
            val activity = scanForActivity(context)
            val intent = Intent(activity, ImageMojitoActivity::class.java)
            activity?.startActivity(intent)
            activity?.overridePendingTransition(0, 0)
        }

        private fun scanForActivity(context: Context?): Activity? {
            if (context == null) return null
            if (context is Activity) {
                return context
            } else if (context is ContextWrapper) {
                return scanForActivity(context.baseContext)
            }
            return null
        }
    }
}