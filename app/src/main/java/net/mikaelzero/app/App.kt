package net.mikaelzero.app

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex


class App : Application() {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    override fun onCreate() {
        super.onCreate()
    }
}