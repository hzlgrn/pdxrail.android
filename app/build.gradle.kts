import java.util.Date
import java.util.Properties

val versionCompose = "1.9.3"
val versionKotlin = "2.2.20"

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.plugin.compose") version "2.2.0"
    id("com.google.devtools.ksp")
}

val buildTime = Date().time
val keyRing = file("../com.hzlgrn.pdxrail.keyring")
    .takeIf { it.canRead() }?.let { keyRingFile ->
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

android {
    namespace = "com.hzlgrn.pdxrail"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.hzlgrn.pdxrail"
        versionCode = 10
        versionName = "1.3"
        minSdk = 24
        targetSdk = 36

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

    /*
    composeCompiler {
        enableStrongSkippingMode = true
    }
     */

    signingConfigs {
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
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$versionKotlin")

    // Compose
    implementation("androidx.activity:activity-compose:1.11.0")
    implementation("androidx.compose.animation:animation:$versionCompose")
    implementation("androidx.compose.material:material:1.9.3")
    implementation("androidx.compose.ui:ui-tooling:$versionCompose")
    // When using a AppCompat theme
    implementation("com.google.accompanist:accompanist-appcompat-theme:0.36.0")

    androidTestImplementation("androidx.compose.ui:ui-test-junit4:$versionCompose")

    // Coroutines
    val versionCoroutines = "1.5.2"
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$versionCoroutines")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.2")

    // Dagger
    val versionDagger = "2.57.2"
    implementation("com.google.dagger:dagger:$versionDagger")
    ksp("com.google.dagger:dagger-compiler:$versionDagger")

    // Lifecycle
    val versionLifecycle = "2.2.0"
    implementation("androidx.lifecycle:lifecycle-extensions:$versionLifecycle")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.9.4")

    // Moshi
    val versionMoshi = "1.15.2"
    implementation("com.squareup.moshi:moshi:$versionMoshi")
    implementation("com.squareup.moshi:moshi-kotlin:$versionMoshi")
    implementation("com.squareup.retrofit2:converter-moshi:3.0.0")

    // Room
    val versionRoom = "2.8.2"
    implementation("androidx.room:room-runtime:$versionRoom")
    implementation("androidx.room:room-ktx:$versionRoom")
    ksp("androidx.room:room-compiler:$versionRoom")

    testImplementation("junit:junit:4.13.2")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:$versionCoroutines")

    androidTestImplementation("androidx.test:runner:1.7.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.7.0")
}
