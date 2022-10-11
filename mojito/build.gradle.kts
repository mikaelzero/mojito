import mojito.setupLibraryModule

plugins {
    id("com.android.library")
    id("kotlin-android")
    id("kotlin-parcelize")
    id("com.github.dcendents.android-maven")
}

group = "com.github.mikaelzero"
setupLibraryModule {
    defaultConfig {
        minSdk = 16
        buildFeatures {
            viewBinding = true
        }
    }
}

dependencies {
    implementation(mojito.Library.ANDROIDX_APPCOMPAT)
    implementation(mojito.Library.ANDROIDX_CORE)
    implementation(mojito.Library.ANDROIDX_RECYCLER_VIEW)
    implementation(mojito.Library.KOTLINX_STDLIB)
    implementation(mojito.Library.IMMERSIONBAR)
    //rxjava权限控制
    implementation ("com.cantrowitz:rxbroadcast:2.0.0")
    implementation ("com.github.tbruyelle:rxpermissions:0.11")
    implementation ("com.blankj:utilcodex:1.30.6")
    implementation ("com.github.bumptech.glide:okhttp3-integration:4.11.0")
    implementation("androidx.exifinterface:exifinterface:1.3.3")
    implementation("androidx.viewpager2:viewpager2:1.0.0")
}
