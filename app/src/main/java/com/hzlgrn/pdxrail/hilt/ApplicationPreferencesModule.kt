package com.hzlgrn.pdxrail.hilt

import android.content.Context
import android.content.SharedPreferences
import com.hzlgrn.pdxrail.Domain
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class ApplicationPreferencesModule {
    @Provides
    fun providesApplicationPreferences(
        @ApplicationContext context: Context,
    ): SharedPreferences {
        return context.getSharedPreferences(
            Domain.App.APPLICATION_PREFERENCES,
            Context.MODE_PRIVATE)
    }
}