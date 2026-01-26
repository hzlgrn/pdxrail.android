buildscript {
    // val version_compose = "1.9.3"
    val versionKotlin = "2.2.20"
    dependencies {
        classpath("com.android.tools.build:gradle:9.0.0")
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
}


tasks.register("clean", Delete::class) {
    delete(getLayout().buildDirectory)
}