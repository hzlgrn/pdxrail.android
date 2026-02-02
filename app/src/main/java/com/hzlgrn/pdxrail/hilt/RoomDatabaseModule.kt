package com.hzlgrn.pdxrail.hilt

import android.content.Context
import androidx.room.Room
import com.hzlgrn.pdxrail.BuildConfig
import com.hzlgrn.pdxrail.Domain
import com.hzlgrn.pdxrail.data.room.ApplicationRoom
import com.hzlgrn.pdxrail.data.room.dao.ArrivalDao
import com.hzlgrn.pdxrail.data.room.dao.RailSystemDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RoomDatabaseModule {

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
    }/*.also { applicationRoom ->
        coroutineScope.launch {
            withContext(Dispatchers.IO) {
                loadRailSystemData(context, applicationRoom)
            }
        }
    }
    */

    @Singleton
    @Provides
    fun providesArrivalDao(applicationRoom: ApplicationRoom): ArrivalDao = applicationRoom.arrivalDao()

    @Singleton
    @Provides
    fun providesRailSystemDao(applicationRoom: ApplicationRoom): RailSystemDao = applicationRoom.railSystemDao()
}