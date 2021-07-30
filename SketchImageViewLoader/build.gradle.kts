import mojito.setupLibraryModule

plugins {
    id("com.android.library")
    id("kotlin-android")
    id("com.github.dcendents.android-maven")
}

group = "com.github.mikaelzero"
setupLibraryModule {
    defaultConfig {
        minSdk = 16
    }
}

dependencies {
    implementation(mojito.Library.KOTLINX_STDLIB)
    implementation(mojito.Library.ANDROIDX_APPCOMPAT)
    implementation(mojito.Library.ANDROIDX_CORE)
    implementation(mojito.Library.OKHTTP)
    implementation("me.panpf:sketch-gif:2.7.1")
    implementation("androidx.exifinterface:exifinterface:1.3.2")
    implementation(project(":mojito"))
}

