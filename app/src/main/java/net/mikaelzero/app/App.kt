package net.mikaelzero.app

import android.app.Application
import net.mikaelzero.mojito.Mojito
import net.mikaelzero.mojito.loader.glide.GlideImageLoader
import net.mikaelzero.mojito.view.sketch.SketchContentViewImplFactory
import net.mikaelzero.mojito.view.sketch.SketchImageViewLoadFactory

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