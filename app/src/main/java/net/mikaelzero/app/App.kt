package net.mikaelzero.app

import android.app.Application
import net.mikaelzero.mojito.Mojito

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        Mojito.initialize(
            GlideImageLoader.with(this),
            SketchContentViewImplFactory(),
            SketchImageViewLoadFactory()
        )
    }
}