@file:Suppress("unused", "SpellCheckingInspection")

package mojito

object Library {

    // CORE

    private const val COROUTINES_VERSION = "1.5.21"
    const val KOTLINX_COROUTINES_ANDROID = "org.jetbrains.kotlinx:kotlinx-coroutines-android:$COROUTINES_VERSION"
    const val KOTLINX_STDLIB = "org.jetbrains.kotlin:kotlin-stdlib:$COROUTINES_VERSION"

    const val ANDROIDX_ACTIVITY = "androidx.activity:activity-ktx:1.2.4"
    const val ANDROIDX_ANNOTATION = "androidx.annotation:annotation:1.2.0"
    const val ANDROIDX_COLLECTION = "androidx.collection:collection-ktx:1.1.0"
    const val ANDROIDX_CONSTRAINT_LAYOUT = "androidx.constraintlayout:constraintlayout:2.0.4"
    const val ANDROIDX_CORE = "androidx.core:core-ktx:1.6.0"
    const val ANDROIDX_EXIF_INTERFACE = "androidx.exifinterface:exifinterface:1.3.2"
    const val ANDROIDX_MULTIDEX = "androidx.multidex:multidex:2.0.1"
    const val ANDROIDX_RECYCLER_VIEW = "androidx.recyclerview:recyclerview:1.2.1"
    const val ANDROIDX_VECTOR_DRAWABLE_ANIMATED = "androidx.vectordrawable:vectordrawable-animated:1.1.0"

    private const val APPCOMPAT_VERSION = "1.3.1"
    const val ANDROIDX_APPCOMPAT = "androidx.appcompat:appcompat:$APPCOMPAT_VERSION"
    const val ANDROIDX_APPCOMPAT_RESOURCES = "androidx.appcompat:appcompat-resources:$APPCOMPAT_VERSION"

    private const val LIFECYCLE_VERSION = "2.3.1"
    const val ANDROIDX_LIFECYCLE_COMMON = "androidx.lifecycle:lifecycle-common-java8:$LIFECYCLE_VERSION"
    const val ANDROIDX_LIFECYCLE_RUNTIME = "androidx.lifecycle:lifecycle-runtime:$LIFECYCLE_VERSION"
    const val ANDROIDX_LIFECYCLE_VIEW_MODEL = "androidx.lifecycle:lifecycle-viewmodel-ktx:$LIFECYCLE_VERSION"

    const val COMPOSE_VERSION = "1.0.0-rc02"
    const val COMPOSE_FOUNDATION = "androidx.compose.foundation:foundation:$COMPOSE_VERSION"
    const val COMPOSE_UI_TEST_JUNIT = "androidx.compose.ui:ui-test-junit4:$COMPOSE_VERSION"
    const val COMPOSE_UI_TEST_MANIFEST = "androidx.compose.ui:ui-test-manifest:$COMPOSE_VERSION"

    const val MATERIAL = "com.google.android.material:material:1.4.0"

    const val ANDROID_SVG = "com.caverock:androidsvg-aar:1.4"

    const val OKHTTP = "com.squareup.okhttp3:okhttp:4.9.1"

    const val OKIO = "com.squareup.okio:okio:2.10.0"

    const val GLIDE = "com.github.bumptech.glide:glide:4.11.0"
    const val BASE_ADAPTER = "com.github.CymChad:BaseRecyclerViewAdapterHelper:3.0.4"
    const val IMMERSIONBAR = "com.geyifeng.immersionbar:immersionbar:3.2.1"
    const val SWIPELAYOUT = "com.daimajia.swipelayout:library:1.2.0@aar"
    const val FRESCO = "com.facebook.fresco:fresco:2.1.0"
    const val ART_PLAY_CORE = "org.salient.artvideoplayer:artplayer-core:1.1.0"
    const val ART_PLAY_IJK = "org.salient.artvideoplayer:artplayer-ijk:1.1.0"
    const val ART_PLAY_V7A = "org.salient.artvideoplayer:artplayer-armv7a:1.1.0"
    const val COIL = "io.coil-kt:coil:2.1.0"

    // TEST

    const val JUNIT = "junit:junit:4.13.2"

    const val KOTLINX_COROUTINES_TEST = "org.jetbrains.kotlinx:kotlinx-coroutines-test:$COROUTINES_VERSION"

    private const val ANDROIDX_TEST_VERSION = "1.4.0"
    const val ANDROIDX_TEST_CORE = "androidx.test:core-ktx:$ANDROIDX_TEST_VERSION"
    const val ANDROIDX_TEST_JUNIT = "androidx.test.ext:junit-ktx:1.1.3"
    const val ANDROIDX_TEST_RULES = "androidx.test:rules:$ANDROIDX_TEST_VERSION"
    const val ANDROIDX_TEST_RUNNER = "androidx.test:runner:$ANDROIDX_TEST_VERSION"

    const val ROBOLECTRIC = "org.robolectric:robolectric:4.6.1"
}
