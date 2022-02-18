buildscript {
    apply(from = "buildSrc/plugins.gradle.kts")
    repositories {
        mavenCentral()
        google()
        maven("https://jitpack.io")
        maven("https://oss.sonatype.org/content/repositories/public")
        gradlePluginPortal()
    }
    dependencies {
        classpath(rootProject.extra["androidPlugin"].toString())
        classpath(rootProject.extra["kotlinPlugin"].toString())
        classpath(rootProject.extra["mavenPublishPlugin"].toString())
        classpath("io.github.didi.dokit:dokitx-plugin:3.5.0.1")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.5.21")
    }
}
tasks.register<Delete>(name = "clean") {
    group = "build"
    delete(rootProject.buildDir)
}
allprojects {
    repositories {
        mavenCentral()
        google()
        maven("https://jitpack.io")
        maven("https://oss.sonatype.org/content/repositories/public")
        gradlePluginPortal()
    }
}
