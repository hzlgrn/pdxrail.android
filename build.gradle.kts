buildscript {
    // val version_compose = "1.9.3"
    val version_kotlin = "2.2.20"
    dependencies {
        classpath("com.android.tools.build:gradle:8.13.0")
        classpath("com.google.gms:google-services:4.4.4")
        classpath("com.google.firebase:firebase-crashlytics-gradle:3.0.6")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$version_kotlin")
        classpath("org.jetbrains.kotlin:compose-compiler-gradle-plugin:2.2.20")
    }
    repositories {
        google()
        mavenCentral()
    }
}

tasks.register("clean", Delete::class) {
    delete(getLayout().buildDirectory)
}