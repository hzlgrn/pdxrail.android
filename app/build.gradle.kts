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
    alias(libs.plugins.google.services)
}

val buildTime = Date().time
val keyRing = Properties().apply {
    setProperty("UPLOAD_KEYSTORE_FILE", "com.hzlgrn.pdxrail.jks")
    setProperty("UPLOAD_KEYSTORE_ALIAS", "alias")
    setProperty("UPLOAD_KEYSTORE_PASSWORD", "secret")
    setProperty("HOME_URL", "https://pdxrail.hzlgrn.com")
    setProperty("HOME_HOST", "pdxrail.hzlgrn.com")
    setProperty("API_GOOGLE_KEY", "google-api-key-with-maps-android-enabled")
    setProperty("API_RAIL_SYSTEM_KEY", "rail-system-api-key")
    setProperty("API_RAIL_SYSTEM_URL", "https://pdxrail.hzlgrn.com/")
}.also { properties ->
    file("../com.hzlgrn.pdxrail.keyring").takeIf { it.canRead() }?.let { keyRingFile ->
        properties.apply {
            keyRingFile.inputStream().use { load(it) }
        }
    }
}

android {
    namespace = "com.hzlgrn.pdxrail"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.hzlgrn.pdxrail"
        minSdk = 24
        targetSdk = 36
        versionCode = 13
        versionName = "26.04.01"

        buildConfigField("long", "BUILD_TIME", "${buildTime}L")
        buildConfigField("String", "STORE_ID", "\"$applicationId\"")
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

    signingConfigs {
        create("release") {
            keyAlias = keyRing["UPLOAD_KEYSTORE_ALIAS"] as String
            keyPassword = keyRing["UPLOAD_KEYSTORE_PASSWORD"] as String
            storeFile = rootProject.file(keyRing["UPLOAD_KEYSTORE_FILE"] as String)
            storePassword = keyRing["UPLOAD_KEYSTORE_PASSWORD"] as String
        }
    }

    buildTypes {
        getByName("debug") {
            applicationIdSuffix = ".dbg"
        }
        getByName("release") {
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.getByName("release")
        }
    }


    compileOptions {
        sourceCompatibility = javaVersion
        targetCompatibility = javaVersion
    }
}

androidComponents {
    onVariants { variant ->
        if (variant.buildType == "debug") {
            variant.outputs.forEach { output ->
                output.versionCode.set(1)
                output.versionName.set("0.0.0")
            }
        }
    }
}

ksp {
    arg("dagger.hilt.disableCrossCompilationRootValidation", "true")
}

dependencies {
    implementation(platform(libs.firebase.bom))

    // AndroidX
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.sqlite.framework)

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
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.crashlyticsndk)

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

    // Data module
    implementation(project(":data"))
    implementation(libs.sqlite.copyopenhelper)

    // Ktor OkHttp engine + content negotiation (TLS config and HttpClient built here)
    implementation(libs.ktor.client.okhttp)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.okhttp)

    // Navigation
    implementation(libs.navigation.fragment)

    // SQLDelight Android driver (provided to :data via DI)
    implementation(libs.sqldelight.android.driver)

    // Utilities
    implementation(libs.android.material)
    implementation(libs.timber)
    implementation(libs.kmp.settings)
    implementation(libs.kotlinx.collections.immutable)

    // Testing
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.test.runner)
    androidTestImplementation(libs.espresso.core)
}
