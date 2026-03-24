package com.hzlgrn.pdxrail.di.module

import com.hzlgrn.pdxrail.BuildConfig
import com.hzlgrn.pdxrail.data.net.PdxRailSystemClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import okhttp3.CipherSuite
import okhttp3.ConnectionSpec
import okhttp3.OkHttpClient
import okhttp3.TlsVersion
import java.util.Collections
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Provides
    @Singleton
    fun providesRailSystemClient(): PdxRailSystemClient {
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

        return PdxRailSystemClient(
            httpClient = httpClient,
            baseUrl = BuildConfig.API_RAIL_SYSTEM_URL,
            apiKey = BuildConfig.API_RAIL_SYSTEM_KEY,
        )
    }
}
