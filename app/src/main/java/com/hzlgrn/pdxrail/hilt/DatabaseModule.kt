package com.hzlgrn.pdxrail.hilt

import android.content.Context
import androidx.room.Room
import com.hzlgrn.pdxrail.BuildConfig
import com.hzlgrn.pdxrail.Domain
import com.hzlgrn.pdxrail.data.room.ApplicationRoom
import com.hzlgrn.pdxrail.data.room.ApplicationRoomLoader
import com.hzlgrn.pdxrail.data.room.ApplicationRoomLoaderImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
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

@Module
@InstallIn(SingletonComponent::class)
abstract class DatabaseLoaderModule {
    @Binds
    abstract fun bindApplicationRoomLoader(
        impl: ApplicationRoomLoaderImpl,
    ): ApplicationRoomLoader
}