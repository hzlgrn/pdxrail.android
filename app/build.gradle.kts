import java.util.Date
import java.util.Properties

val versionCompose = "1.10.1"
val versionKotlin = "2.2.21"

plugins {
    id("com.android.application")
    id("com.google.dagger.hilt.android")
    id("com.google.devtools.ksp")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.kotlin.plugin.serialization")
}

val buildTime = Date().time
val keyRing = file("../com.hzlgrn.pdxrail.keyring")
    .takeIf { it.canRead() }?.let { keyRingFile ->
        Properties().apply {
            keyRingFile.inputStream().use { load(it) }
        }
    } ?: Properties().apply {
        setProperty("UPLOAD_KEYSTORE_FILE", "com.hzlgrn.pdxrail.jks")
        setProperty("UPLOAD_KEYSTORE_ALIAS", "alias")
        setProperty("UPLOAD_KEYSTORE_PASSWORD", "secret")
        setProperty("HOME_URL", "https://pdxrail.hzlgrn.com")
        setProperty("HOME_HOST", "pdxrail.hzlgrn.com")
        setProperty("API_GOOGLE_KEY", "google-api-key-with-maps-android-enabled")
        setProperty("API_RAIL_SYSTEM_KEY", "rail-system-api-key")
        setProperty("API_RAIL_SYSTEM_URL", "https://pdxrail.hzlgrn.com/")
    }

android {
    namespace = "com.hzlgrn.pdxrail"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.hzlgrn.pdxrail"
        minSdk = 24
        targetSdk = 36
        versionCode = 10
        versionName = "1.3"

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

        javaCompileOptions {
            annotationProcessorOptions {
                arguments += mapOf(
                    "room.schemaLocation" to "${projectDir}/schemas",
                    "room.incremental" to "true",
                    "toom.expandProjection" to "true",
                )
            }
        }

        vectorDrawables.useSupportLibrary = true
    }
    buildFeatures {
        buildConfig = true
        compose = true
        resValues = true
        viewBinding = true
    }
    buildTypes {
        getByName("debug") {
            applicationIdSuffix = ".dbg"
        }
        getByName("release") {
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_24
        targetCompatibility = JavaVersion.VERSION_24
    }

    signingConfigs {
        create("release") {
            keyAlias = keyRing["UPLOAD_KEYSTORE_ALIAS"] as String
            keyPassword = keyRing["UPLOAD_KEYSTORE_PASSWORD"] as String
            storeFile = rootProject.file(keyRing["UPLOAD_KEYSTORE_FILE"] as String)
            storePassword = keyRing["UPLOAD_KEYSTORE_PASSWORD"] as String
        }
    }

}
dependencies {
    implementation(platform("com.google.firebase:firebase-bom:34.8.0"))

    implementation("androidx.appcompat:appcompat:1.7.1")
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.1")
    implementation("androidx.constraintlayout:constraintlayout-compose:1.1.1")
    implementation("androidx.core:core-ktx:1.17.0")
    implementation("androidx.recyclerview:recyclerview:1.4.0")
    implementation("com.google.android.gms:play-services-location:21.3.0")
    implementation("com.google.android.gms:play-services-maps:20.0.0")
    implementation("com.google.maps.android:maps-compose:7.0.0")
    implementation("com.google.android.material:material:1.13.0")
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-crashlytics-ndk")
    implementation("com.google.maps.android:android-maps-utils:4.0.0")
    implementation("com.jakewharton.timber:timber:5.0.1")
    implementation("com.squareup.okhttp3:okhttp:5.3.2")
    implementation("com.squareup.retrofit2:retrofit:3.0.0")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$versionKotlin")

    // Compose
    implementation("androidx.activity:activity-compose:1.12.2")
    implementation("androidx.compose.animation:animation:$versionCompose")
    implementation("androidx.compose.material:material:$versionCompose")
    implementation("androidx.compose.material3:material3:1.4.0")
    implementation("androidx.compose.ui:ui-tooling:$versionCompose")
    implementation("androidx.compose.ui:ui-viewbinding:$versionCompose")
    implementation("androidx.compose.ui:ui-unit:1.10.1")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:$versionCompose")

    // When using a AppCompat theme
    implementation("com.google.accompanist:accompanist-appcompat-theme:0.36.0")


    // Coroutines
    val versionCoroutines = "1.10.2"
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$versionCoroutines")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:$versionCoroutines")

    // Dagger
    val versionDagger = "2.59"
    implementation("com.google.dagger:dagger:$versionDagger")
    ksp("com.google.dagger:dagger-compiler:$versionDagger")

    // Hilt
    implementation("com.google.dagger:hilt-android:2.59")
    ksp("com.google.dagger:hilt-android-compiler:2.59")
    implementation("androidx.hilt:hilt-navigation-fragment:1.3.0")

    // Lifecycle
    val versionLifecycle = "2.2.0"
    implementation("androidx.lifecycle:lifecycle-extensions:$versionLifecycle")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.10.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.10.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.10.0")

    // Moshi
    val versionMoshi = "1.15.2"
    implementation("com.squareup.moshi:moshi:$versionMoshi")
    implementation("com.squareup.moshi:moshi-kotlin:$versionMoshi")
    implementation("com.squareup.retrofit2:converter-moshi:3.0.0")

    // Room
    val versionRoom = "2.8.4"
    implementation("androidx.room:room-runtime:$versionRoom")
    implementation("androidx.room:room-ktx:$versionRoom")
    ksp("androidx.room:room-compiler:$versionRoom")

    // Navigation
    val versionNavigation = "2.9.6"
    // Jetpack Compose Integration
    implementation("androidx.navigation:navigation-compose:$versionNavigation")

    // Views/Fragments Integration
    implementation("androidx.navigation:navigation-fragment:$versionNavigation")
    implementation("androidx.navigation:navigation-ui:$versionNavigation")

    // Feature module support for Fragments
    implementation("androidx.navigation:navigation-dynamic-features-fragment:$versionNavigation")

    // Testing Navigation
    androidTestImplementation("androidx.navigation:navigation-testing:$versionNavigation")

    // JSON serialization library, works with the Kotlin serialization plugin.
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.10.0")

    testImplementation("junit:junit:4.13.2")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:$versionCoroutines")

    androidTestImplementation("androidx.test:runner:1.7.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.7.0")
}
