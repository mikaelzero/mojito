buildscript {
    apply(from = "buildSrc/plugins.gradle.kts")
    repositories {
        google()
        maven("https://jitpack.io")
        mavenCentral()
        gradlePluginPortal()
    }
    dependencies {
        classpath(rootProject.extra["androidPlugin"].toString())
        classpath(rootProject.extra["kotlinPlugin"].toString())
        classpath(rootProject.extra["mavenPublishPlugin"].toString())
    }
}
tasks.register<Delete>(name = "clean") {
    group = "build"
    delete(rootProject.buildDir)
}
allprojects {
    repositories {
        google()
        maven("https://jitpack.io")
        mavenCentral()
        gradlePluginPortal()
    }
}
