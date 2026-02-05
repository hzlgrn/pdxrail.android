package com.hzlgrn.pdxrail.di.module

import android.content.Context
import androidx.room.Room
import com.hzlgrn.pdxrail.BuildConfig
import com.hzlgrn.pdxrail.Domain
import com.hzlgrn.pdxrail.data.room.ApplicationRoom
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {
    @Singleton
    @Provides
    fun providesApplicationRoom(
        @ApplicationContext context: Context,
    ): ApplicationRoom = if (BuildConfig.DEBUG) {
        Room
            .inMemoryDatabaseBuilder(context, ApplicationRoom::class.java)
            .build()
    } else {
        Room
            .databaseBuilder(context, ApplicationRoom::class.java, Domain.App.DB_NAME)
            .fallbackToDestructiveMigration(dropAllTables = true)
            .build()
    }
}