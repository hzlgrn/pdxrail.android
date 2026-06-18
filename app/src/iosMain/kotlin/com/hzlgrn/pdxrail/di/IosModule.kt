package com.hzlgrn.pdxrail.di

import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.hzlgrn.pdxrail.data.db.AppDatabase
import com.hzlgrn.pdxrail.data.help.PdxRailSystemHelper
import com.hzlgrn.pdxrail.data.net.PdxRailSystemClient
import com.russhwolf.settings.NSUserDefaultsSettings
import com.russhwolf.settings.Settings
import io.ktor.client.HttpClient
import io.ktor.client.engine.darwin.Darwin
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.serialization.json.Json
import org.koin.dsl.module
import platform.Foundation.NSBundle
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDefaults

object IosModule {
    val all = listOf(networkModule, databaseModule, preferencesModule)
}

private val networkModule = module {
    single {
        val httpClient = HttpClient(Darwin) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }
        val baseUrlHost = NSBundle.mainBundle.infoDictionary?.get("API_RAIL_SYSTEM_URL") as? String ?: ""
        val baseUrl = "https://$baseUrlHost"
        val apiKey = NSBundle.mainBundle.infoDictionary?.get("API_RAIL_SYSTEM_KEY") as? String ?: ""
        PdxRailSystemClient(
            httpClient = httpClient,
            baseUrl = baseUrl,
            apiKey = apiKey,
        )
    }
}

private val databaseModule = module {
    single {
        val driver = NativeSqliteDriver(
            schema = AppDatabase.Schema,
            name = PdxRailSystemHelper.DB_NAME,
            onConfiguration = { config ->
                config.copy(extendedConfig = config.extendedConfig.copy(basePath = getDatabaseDirectory()))
            }
        )
        copyDatabaseFromBundleIfNeeded()
        AppDatabase(driver)
    }
}

private val preferencesModule = module {
    single<Settings> {
        NSUserDefaultsSettings(NSUserDefaults.standardUserDefaults)
    }
}

private fun getDatabaseDirectory(): String {
    val fileManager = NSFileManager.defaultManager
    val urls = fileManager.URLsForDirectory(
        directory = platform.Foundation.NSDocumentDirectory,
        inDomains = platform.Foundation.NSUserDomainMask,
    )
    return (urls.firstOrNull() as? platform.Foundation.NSURL)?.path ?: ""
}

@OptIn(ExperimentalForeignApi::class)
private fun copyDatabaseFromBundleIfNeeded() {
    val fileManager = NSFileManager.defaultManager
    val docDir = getDatabaseDirectory()
    val destPath = "$docDir/${PdxRailSystemHelper.DB_NAME}"

    if (!fileManager.fileExistsAtPath(destPath)) {
        val bundlePath = NSBundle.mainBundle.pathForResource(
            name = PdxRailSystemHelper.DB_NAME.removeSuffix(".db"),
            ofType = "db",
        ) ?: return
        fileManager.copyItemAtPath(bundlePath, destPath, null)
    }
}
