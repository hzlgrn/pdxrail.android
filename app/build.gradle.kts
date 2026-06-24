import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Date
import java.util.Properties

val javaVersion = JavaVersion.VERSION_18

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.agp)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.google.services)
    alias(libs.plugins.firebase.crashlytics)
}

compose.resources {
    packageOfResClass = "com.hzlgrn.pdxrail.generated.resources"
}

val buildTime = Date().time
val keyRing = Properties().apply {
    setProperty("UPLOAD_KEYSTORE_FILE", "com.hzlgrn.pdxrail.jks")
    setProperty("UPLOAD_KEYSTORE_ALIAS", "alias")
    setProperty("UPLOAD_KEYSTORE_PASSWORD", "secret")
    setProperty("HOME_URL", "https://pdxrail.hzlgrn.com")
    setProperty("HOME_HOST", "pdxrail.hzlgrn.com")
    setProperty("API_RAIL_SYSTEM_KEY", "rail-system-api-key")
    setProperty("API_RAIL_SYSTEM_URL", "https://pdxrail.hzlgrn.com/")
}.also { properties ->
    file("../com.hzlgrn.pdxrail.keyring").takeIf { it.canRead() }?.let { keyRingFile ->
        properties.apply { keyRingFile.inputStream().use { load(it) } }
    }
}

kotlin {
    androidTarget {
        compilations.all {
            compileTaskProvider.configure {
                compilerOptions {
                    jvmTarget.set(JvmTarget.JVM_18)
                    freeCompilerArgs.add("-Xjsr305=strict")
                    freeCompilerArgs.add("-Xjvm-default=all")
                    freeCompilerArgs.add("-XXLanguage:+PropertyParamAnnotationDefaultTargetMode")
                }
            }
        }
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64(),
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.materialIconsExtended)
            implementation(compose.ui)
            implementation(compose.animation)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)

            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)

            implementation(libs.maplibre.compose)

            implementation(project(":data"))
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.datetime)
            implementation(libs.kmp.settings)
            implementation(libs.kotlinx.collections.immutable)
            implementation(libs.kotlinx.serialization.json)
        }

        androidMain.dependencies {
            implementation(libs.lifecycle.viewmodel.compose)
            implementation(libs.lifecycle.runtime.ktx)

            implementation(libs.androidx.activity.compose)
            implementation(libs.androidx.appcompat)
            implementation(libs.androidx.core.ktx)
            implementation(libs.androidx.sqlite.framework)

            implementation(libs.firebase.analytics)
            implementation(libs.firebase.crashlytics)
            implementation(libs.firebase.crashlyticsndk)

            implementation(libs.koin.android)

            implementation(libs.kotlinx.coroutines.android)

            implementation(libs.ktor.client.okhttp)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.okhttp)

            implementation(libs.sqldelight.android.driver)
            implementation(libs.sqlite.copyopenhelper)

            implementation(libs.android.material)
            implementation(libs.timber)
        }

        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
            implementation(libs.sqldelight.native.driver)
        }

        commonTest.dependencies {
            implementation(libs.junit)
            implementation(libs.kotlinx.coroutines.test)
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
        versionCode = 19
        versionName = "26.06.02"

        buildConfigField("long", "BUILD_TIME", "${buildTime}L")
        buildConfigField("String", "STORE_ID", "\"$applicationId\"")
        buildConfigField("String", "API_RAIL_SYSTEM_KEY", "\"${keyRing["API_RAIL_SYSTEM_KEY"] as String}\"")
        buildConfigField("String", "API_RAIL_SYSTEM_URL", "\"${keyRing["API_RAIL_SYSTEM_URL"] as String}\"")
        buildConfigField("String", "HOME_URL", "\"https://${keyRing["HOME_HOST"] as? String}/\"")
        buildConfigField("String", "HOME_HOST", "\"${keyRing["HOME_HOST"] as? String}\"")

        manifestPlaceholders["APPLICATION_LABEL"] = "PDX Rail"
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
        resValues = true
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
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.getByName("release")
            ndk { debugSymbolLevel = "FULL" }
        }
    }

    compileOptions {
        sourceCompatibility = javaVersion
        targetCompatibility = javaVersion
    }
}

dependencies {
    add("androidMainImplementation", platform(libs.firebase.bom))
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
