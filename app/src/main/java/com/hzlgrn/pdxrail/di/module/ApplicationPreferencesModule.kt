package com.hzlgrn.pdxrail.di.module

import android.content.Context
import android.content.SharedPreferences
import com.hzlgrn.pdxrail.Domain
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class ApplicationPreferencesModule(private val applicationContext: Context) {
    @Singleton
    @Provides
    fun provideApplicationPreferences(): SharedPreferences {
        return applicationContext.getSharedPreferences(
            Domain.App.APPLICATION_PREFERENCES,
            Context.MODE_PRIVATE)
    }
}