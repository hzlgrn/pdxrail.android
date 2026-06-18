package com.hzlgrn.pdxrail.di

import android.content.Context
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.hzlgrn.pdxrail.BuildConfig
import com.hzlgrn.pdxrail.data.db.AppDatabase
import com.hzlgrn.pdxrail.data.help.PdxRailSystemHelper
import com.hzlgrn.pdxrail.data.net.PdxRailSystemClient
import com.russhwolf.settings.Settings
import com.russhwolf.settings.SharedPreferencesSettings
import io.github.reline.sqlite.db.CopyConfig
import io.github.reline.sqlite.db.CopySource
import io.github.reline.sqlite.db.MigrationStrategy
import io.github.reline.sqlite.db.SQLiteCopyOpenHelper
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import okhttp3.CipherSuite
import okhttp3.ConnectionSpec
import okhttp3.OkHttpClient
import okhttp3.TlsVersion
import org.koin.dsl.module
import java.util.Collections

object AndroidModule {
    val all = listOf(networkModule, databaseModule, preferencesModule)
}

private val networkModule = module {
    single {
        val okHttpClient = OkHttpClient.Builder()
            .connectionSpecs(
                Collections.singletonList(
                    ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                        .tlsVersions(TlsVersion.TLS_1_2)
                        .cipherSuites(
                            CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256,
                            CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256,
                            CipherSuite.TLS_DHE_RSA_WITH_AES_128_GCM_SHA256,
                        )
                        .build()
                )
            )
            .build()

        val httpClient = HttpClient(OkHttp) {
            engine { preconfigured = okHttpClient }
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }

        PdxRailSystemClient(
            httpClient = httpClient,
            baseUrl = BuildConfig.API_RAIL_SYSTEM_URL,
            apiKey = BuildConfig.API_RAIL_SYSTEM_KEY,
        )
    }
}

private val databaseModule = module {
    single {
        val context: Context = get()
        val driver = AndroidSqliteDriver(
            schema = AppDatabase.Schema,
            context = context,
            factory = SQLiteCopyOpenHelper.Factory(
                copyConfig = CopyConfig(
                    copySource = CopySource.FromAssetPath(PdxRailSystemHelper.DB_NAME),
                    migrationStrategy = MigrationStrategy.Destructive,
                ),
                delegate = FrameworkSQLiteOpenHelperFactory(),
            ),
            name = PdxRailSystemHelper.DB_NAME,
        )
        AppDatabase(driver)
    }
}

private val preferencesModule = module {
    single<Settings> {
        val context: Context = get()
        val sharedPrefs = context.getSharedPreferences(
            "${BuildConfig.APPLICATION_ID}.settings",
            Context.MODE_PRIVATE,
        )
        SharedPreferencesSettings(sharedPrefs)
    }
}
