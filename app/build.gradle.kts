import java.util.Date
import java.util.Properties

val javaVersion = JavaVersion.VERSION_18

val versionCompose = "1.10.3"
val versionCoroutines = "1.10.2"
val versionHilt = "2.58"
val versionLifecycle = "2.10.0"
val versionMoshi = "1.15.2"
val versionNavigation = "2.9.7"
val versionRoom = "2.8.4"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(javaVersion.majorVersion))
    }
}

plugins {
    id("com.android.application")
    id("com.google.dagger.hilt.android")
    id("com.google.devtools.ksp")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("org.jetbrains.kotlin.android")
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

        vectorDrawables.useSupportLibrary = true
    }

    buildFeatures {
        buildConfig = true
        compose = true
        dataBinding = true
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
        sourceCompatibility = javaVersion
        targetCompatibility = javaVersion
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

ksp {
    arg("room.schemaLocation", "${projectDir}/schemas")
    arg("room.incremental", "true")
    arg("room.expandProjection", "true")
    arg("dagger.hilt.disableCrossCompilationRootValidation", "true")
}

dependencies {
    implementation(platform("com.google.firebase:firebase-bom:34.8.0"))

    // AndroidX
    implementation("androidx.appcompat:appcompat:1.7.1")
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.1")
    implementation("androidx.constraintlayout:constraintlayout-compose:1.1.1")
    implementation("androidx.core:core-ktx:1.17.0")
    implementation("androidx.recyclerview:recyclerview:1.4.0")

    // Compose
    implementation("androidx.activity:activity-compose:1.12.3")
    implementation("androidx.compose.animation:animation:$versionCompose")
    implementation("androidx.compose.material:material:$versionCompose")
    implementation("androidx.compose.material:material-icons-core-android:1.7.8")
    implementation("androidx.compose.material3:material3:1.4.0")
    implementation("androidx.compose.ui:ui-tooling:$versionCompose")
    implementation("androidx.compose.ui:ui-unit:$versionCompose")
    implementation("androidx.compose.ui:ui-viewbinding:$versionCompose")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:$versionCompose")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:$versionCoroutines")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$versionCoroutines")

    // Firebase
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-crashlytics-ndk")

    // Hilt
    implementation("androidx.hilt:hilt-navigation-fragment:1.3.0")
    implementation("com.google.dagger:hilt-android:$versionHilt")
    ksp("com.google.dagger:hilt-android-compiler:$versionHilt")
    ksp("com.google.dagger:hilt-compiler:$versionHilt")

    // Lifecycle
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$versionLifecycle")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:$versionLifecycle")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:$versionLifecycle")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$versionLifecycle")

    // Maps
    implementation("com.google.android.gms:play-services-location:21.3.0")
    implementation("com.google.android.gms:play-services-maps:20.0.0")
    implementation("com.google.maps.android:android-maps-utils:4.0.0")
    implementation("com.google.maps.android:maps-compose:7.0.0")

    // Moshi
    implementation("com.squareup.moshi:moshi:$versionMoshi")
    implementation("com.squareup.moshi:moshi-kotlin:$versionMoshi")
    implementation("com.squareup.retrofit2:converter-moshi:3.0.0")

    // Navigation
    implementation("androidx.navigation:navigation-compose:$versionNavigation")
    implementation("androidx.navigation:navigation-dynamic-features-fragment:$versionNavigation")
    implementation("androidx.navigation:navigation-fragment:$versionNavigation")
    implementation("androidx.navigation:navigation-ui:$versionNavigation")
    androidTestImplementation("androidx.navigation:navigation-testing:$versionNavigation")

    // Network
    implementation("com.squareup.okhttp3:okhttp:5.3.2")
    implementation("com.squareup.retrofit2:retrofit:3.0.0")

    // Room
    implementation("androidx.room:room-ktx:$versionRoom")
    implementation("androidx.room:room-runtime:$versionRoom")
    ksp("androidx.room:room-compiler:$versionRoom")

    // Utilities
    implementation("com.google.accompanist:accompanist-appcompat-theme:0.36.0")
    implementation("com.google.android.material:material:1.13.0")
    implementation("com.jakewharton.timber:timber:5.0.1")
    implementation("org.jetbrains.kotlinx:kotlinx-collections-immutable:0.4.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.10.0")

    // Testing
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:$versionCoroutines")
    androidTestImplementation("androidx.test:runner:1.7.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.7.0")
}
