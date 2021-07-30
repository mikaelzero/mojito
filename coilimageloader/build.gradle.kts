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
    implementation("io.coil-kt:coil-base:1.3.0")
    implementation("io.coil-kt:coil-gif:1.3.0")
    implementation("io.coil-kt:coil-svg:1.3.0")
    implementation(project(":mojito"))
}
