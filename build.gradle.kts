buildscript {
    val versionKotlin = "2.3.0"
    dependencies {
        classpath("com.android.tools.build:gradle:8.9.1")
        classpath("com.google.gms:google-services:4.4.4")
        classpath("com.google.firebase:firebase-crashlytics-gradle:3.0.6")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$versionKotlin")
    }
    repositories {
        google()
        mavenCentral()
    }
}
plugins {
    id("com.google.devtools.ksp") version "2.3.4" apply false
    id("com.google.dagger.hilt.android") version "2.58" apply false
    id("org.jetbrains.kotlin.plugin.compose") version "2.3.0"
    id("org.jetbrains.kotlin.plugin.serialization") version "2.3.0"
}
tasks.register("clean", Delete::class) {
    delete(getLayout().buildDirectory)
}