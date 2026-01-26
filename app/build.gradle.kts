import java.util.Date
import java.util.Properties

plugins {
    id("com.android.application")
    id("kotlin-kapt")
    kotlin("android")
}

val buildTime = Date().getTime()
val keyRing = file("../com.hzlgrn.pdxrail.keyring").takeIf { it.canRead() }?.let { keyRingFile ->
    Properties().apply {
        keyRingFile.inputStream().use { load(it) }
    }
} ?: Properties().apply {
    setProperty("UPLOAD_KEYSTORE_FILE", "")
    setProperty("UPLOAD_KEYSTORE_ALIAS", "")
    setProperty("UPLOAD_KEYSTORE_PASSWORD", "")

    setProperty("HOME_URL", "")
    setProperty("HOME_HOST", "")

    setProperty("API_GOOGLE_KEY", "")

    setProperty("API_RAIL_SYSTEM_KEY", "")
    setProperty("API_RAIL_SYSTEM_URL", "")
}

val version_compose = "1.9.3"
val version_kotlin = "2.2.20"

android {
    namespace = "com.hzlgrn.pdxrail"
    compileSdkVersion(36)

    defaultConfig {
        applicationId = "com.hzlgrn.pdxrail"
        versionCode = 10
        versionName = "1.3"
        minSdkVersion(24)
        targetSdkVersion(36)

        buildConfigField("long", "BUILD_TIME", "${buildTime}L")
        buildConfigField("String", "API_RAIL_SYSTEM_KEY", "\"${keyRing["API_RAIL_SYSTEM_KEY"] as String}\"")
        buildConfigField("String", "API_RAIL_SYSTEM_URL", "\"${keyRing["API_RAIL_SYSTEM_URL"] as String}\"")
        buildConfigField("String", "HOME_URL", "\"https://${keyRing["HOME_HOST"] as? String}/\"")
        buildConfigField("String", "HOME_HOST", "\"${keyRing["HOME_HOST"] as? String}\"")

        manifestPlaceholders["APPLICATION_LABEL"] = "PDX Rail"
        manifestPlaceholders["API_GOOGLE_KEY"] = keyRing["API_GOOGLE_KEY"] as String
        manifestPlaceholders["HOME_HOST"] = keyRing["HOME_HOST"] as String
        manifestPlaceholders["HOME_URL"] = "https://${keyRing["HOME_HOST"] as String}/"

        resValue("string", "asset_statements", """[{
            "relation": ["delegate_permission/common.handle_all_urls"], 
            "target": {
                "namespace": "web", 
                "site": "https://${keyRing["HOME_HOST"] as String}/"
            }
        }]""")
        vectorDrawables.useSupportLibrary = true

        javaCompileOptions {
            annotationProcessorOptions {
                arguments += mapOf(
                    "room.schemaLocation" to "${projectDir}/schemas",
                    "room.incremental" to "true",
                    "toom.expandProjection" to "true",
                )
            }
        }
    }
    buildFeatures {
        buildConfig = true
        compose = true
        viewBinding = true
    }
    buildTypes {
        getByName("debug") {
            applicationIdSuffix = ".dbg"
        }
        getByName("release") {
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_24
        targetCompatibility = JavaVersion.VERSION_24
    }
    composeOptions {
        kotlinCompilerExtensionVersion = version_compose
    }


    signingConfigs {
        getByName("debug") {
            keyAlias = "debug"
            keyPassword = "password"
            storeFile = rootProject.file("com.hzlgrn.pdxrail.jks.debug")
            storePassword = "password"
        }

        // todo: make conditional if release?
        create("release") {
            keyAlias = keyRing["UPLOAD_KEYSTORE_ALIAS"] as String
            keyPassword = keyRing["UPLOAD_KEYSTORE_PASSWORD"] as String
            storeFile = rootProject.file(keyRing["UPLOAD_KEYSTORE_FILE"] as String)
            storePassword = keyRing["UPLOAD_KEYSTORE_PASSWORD"] as String
        }
    }

}
dependencies {
    implementation(platform("com.google.firebase:firebase-bom:34.4.0"))

    implementation("androidx.appcompat:appcompat:1.7.1")
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.1")
    implementation("androidx.constraintlayout:constraintlayout-compose:1.1.1")
    implementation("androidx.core:core-ktx:1.17.0")
    implementation("androidx.recyclerview:recyclerview:1.4.0")
    implementation("com.google.android.gms:play-services-location:21.3.0")
    implementation("com.google.android.gms:play-services-maps:19.2.0")
    implementation("com.google.android.material:material:1.13.0")
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-crashlytics-ndk")
    implementation("com.google.maps.android:android-maps-utils:3.19.0")
    implementation("com.jakewharton.timber:timber:5.0.1")
    implementation("com.squareup.okhttp3:okhttp:5.2.1")
    implementation("com.squareup.retrofit2:retrofit:3.0.0")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$version_kotlin")

    // Compose
    implementation("androidx.activity:activity-compose:1.11.0")
    implementation("androidx.compose.animation:animation:$version_compose")
    implementation("androidx.compose.material:material:1.9.3")
    implementation("androidx.compose.ui:ui-tooling:$version_compose")
    // When using a AppCompat theme
    implementation("com.google.accompanist:accompanist-appcompat-theme:0.36.0")

    androidTestImplementation("androidx.compose.ui:ui-test-junit4:$version_compose")

    // Coroutines
    val version_coroutines = "1.5.2"
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$version_coroutines")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.2")

    // Dagger
    val version_dagger = "2.57.2"
    implementation("com.google.dagger:dagger:$version_dagger")
    kapt("com.google.dagger:dagger-compiler:$version_dagger")

    // Lifecycle
    val version_lifecycle = "2.2.0"
    implementation("androidx.lifecycle:lifecycle-extensions:$version_lifecycle")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.9.4")

    // Moshi
    val version_moshi = "1.15.2"
    implementation("com.squareup.moshi:moshi:$version_moshi")
    implementation("com.squareup.moshi:moshi-kotlin:$version_moshi")
    implementation("com.squareup.retrofit2:converter-moshi:3.0.0")

    // Room
    val version_room = "2.8.2"
    implementation("androidx.room:room-runtime:$version_room")
    implementation("androidx.room:room-ktx:$version_room")
    kapt("androidx.room:room-compiler:$version_room")

    testImplementation("junit:junit:4.13.2")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:$version_coroutines")

    androidTestImplementation("androidx.test:runner:1.7.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.7.0")
}
