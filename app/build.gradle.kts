import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Date
import java.util.Properties

val javaVersion = JavaVersion.VERSION_18

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(javaVersion.majorVersion))
    }
}

kotlin {
    compilerOptions {
        freeCompilerArgs.add("-Xjsr305=strict")
        freeCompilerArgs.add("-Xjvm-default=all")
        freeCompilerArgs.add("-XXLanguage:+PropertyParamAnnotationDefaultTargetMode")

        jvmTarget.set(JvmTarget.JVM_18)
    }
}

plugins {
    alias(libs.plugins.agp)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.android)
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
    implementation(platform(libs.firebase.bom))

    // AndroidX
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core.ktx)

    // Compose
    implementation(libs.androidx.activity.compose)
    implementation(libs.compose.animation)
    implementation(libs.compose.material)
    implementation(libs.compose.material.icons.core.android)
    implementation(libs.compose.material3)
    implementation(libs.compose.ui.tooling)
    implementation(libs.compose.ui.unit)
    implementation(libs.compose.ui.viewbinding)
    androidTestImplementation(libs.compose.ui.test.junit4)

    // Coroutines
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.coroutines.core)

    // Firebase
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics.ndk)

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)
    ksp(libs.hilt.compiler)

    // Lifecycle
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.lifecycle.viewmodel.compose)
    implementation(libs.lifecycle.viewmodel.ktx)

    // Maps
    implementation(libs.play.services.location)
    implementation(libs.play.services.maps)
    implementation(libs.maps.compose)

    // Moshi
    implementation(libs.moshi)
    implementation(libs.moshi.kotlin)
    implementation(libs.retrofit.converter.moshi)

    // Navigation
    implementation(libs.navigation.fragment)

    // Network
    implementation(libs.okhttp)
    implementation(libs.retrofit)

    // Room
    implementation(libs.room.ktx)
    implementation(libs.room.runtime)
    ksp(libs.room.compiler)

    // Utilities
    implementation(libs.android.material)
    implementation(libs.timber)
    implementation(libs.kotlinx.collections.immutable)

    // Testing
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.test.runner)
    androidTestImplementation(libs.espresso.core)
}
