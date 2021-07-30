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
    implementation("com.facebook.fresco:fresco:2.1.0")
    implementation("androidx.annotation:annotation:1.2.0")
    implementation(project(":mojito"))
}
