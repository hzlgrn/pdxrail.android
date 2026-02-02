package com.hzlgrn.pdxrail.hilt

import com.hzlgrn.pdxrail.BuildConfig
import com.hzlgrn.pdxrail.data.net.RailSystemService
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.CipherSuite
import okhttp3.ConnectionSpec
import okhttp3.OkHttpClient
import okhttp3.TlsVersion
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.Collections
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Provides
    @Singleton
    fun providesRailSystemService(): RailSystemService {
        return Retrofit.Builder()
            .addConverterFactory(converterFactory)
            .baseUrl(BuildConfig.API_RAIL_SYSTEM_URL)
            .client(okHttpClient)
            .build()
            .create(RailSystemService::class.java)
    }

    private val converterFactory get() = MoshiConverterFactory.create(
            Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build())

    private val okHttpClient get() = OkHttpClient.Builder()
            .connectionSpecs(
                Collections.singletonList(
                    ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                        .tlsVersions(TlsVersion.TLS_1_2)
                        .cipherSuites(
                            CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256,
                            CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256,
                            CipherSuite.TLS_DHE_RSA_WITH_AES_128_GCM_SHA256)
                        .build()))
            .build()
}