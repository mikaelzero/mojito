import mojito.Library
import mojito.setupAppModule

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-parcelize")
}
setupAppModule {
    defaultConfig {
        minSdk = 21
        applicationId = "net.mikaelzero.app"
        multiDexEnabled = true
        buildFeatures {
            viewBinding = true
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles("proguard-rules.pro")
        }
    }
}

dependencies {

    implementation(Library.KOTLINX_STDLIB)

    implementation(Library.ANDROIDX_ACTIVITY)
    implementation(Library.ANDROIDX_APPCOMPAT)
    implementation(Library.ANDROIDX_CONSTRAINT_LAYOUT)
    implementation(Library.ANDROIDX_CORE)
    implementation(Library.ANDROIDX_LIFECYCLE_VIEW_MODEL)
    implementation(Library.ANDROIDX_MULTIDEX)
    implementation(Library.ANDROIDX_RECYCLER_VIEW)
    implementation(Library.MATERIAL)

    implementation(Library.GLIDE)
    implementation(Library.BASE_ADAPTER)
    implementation(Library.IMMERSIONBAR)
    implementation(Library.SWIPELAYOUT)
    implementation(Library.FRESCO)
    implementation(Library.ART_PLAY_CORE)
    implementation(Library.ART_PLAY_IJK)
    implementation(Library.ART_PLAY_V7A)
    implementation(Library.COIL)

    implementation(project(":mojito"))
    implementation(project(":GlideImageLoader"))
    implementation(project(":FrescoImageLoader"))
    implementation(project(":SketchImageViewLoader"))
    implementation(project(":coilimageloader"))
    implementation(project(":PhotoViewImageViewLoader"))

    debugImplementation("io.github.didi.dokit:dokitx:3.5.0-beta01")
    releaseImplementation("io.github.didi.dokit:dokitx-no-op:3.5.0-beta01")
}
