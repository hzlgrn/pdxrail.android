package com.hzlgrn.pdxrail.di.module

import android.content.Context
import com.hzlgrn.pdxrail.BuildConfig
import com.russhwolf.settings.Settings
import com.russhwolf.settings.SharedPreferencesSettings
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class PreferencesModule {
    @Provides
    fun providesSettings(
        @ApplicationContext context: Context,
    ): Settings {
        val sharedPrefs = context.getSharedPreferences(
            "${BuildConfig.APPLICATION_ID}.settings",
            Context.MODE_PRIVATE,
        )
        return SharedPreferencesSettings(sharedPrefs)
    }
}
