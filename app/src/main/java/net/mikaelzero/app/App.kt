package net.mikaelzero.app

import android.app.Application
import net.mikaelzero.diooto.Mojito

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        Mojito.initialize(
            GlideImageLoader.with(this),
            PhotoViewContentLoaderImpl(),
            PhotoImageViewFactory()
        )
    }
}