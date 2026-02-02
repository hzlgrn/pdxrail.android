package com.hzlgrn.pdxrail.hilt

import com.hzlgrn.pdxrail.data.room.ApplicationRoom
import com.hzlgrn.pdxrail.data.room.dao.ArrivalDao
import com.hzlgrn.pdxrail.data.room.dao.RailSystemDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DaoModule {
    @Singleton
    @Provides
    fun providesArrivalDao(applicationRoom: ApplicationRoom): ArrivalDao =
        applicationRoom.arrivalDao()

    @Singleton
    @Provides
    fun providesRailSystemDao(applicationRoom: ApplicationRoom): RailSystemDao =
        applicationRoom.railSystemDao()
}