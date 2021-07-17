package com.hzlgrn.pdxrail.di.module

import android.content.Context
import com.hzlgrn.pdxrail.data.repository.ArrivalRepository
import com.hzlgrn.pdxrail.data.repository.RailSystemRepository
import com.hzlgrn.pdxrail.data.room.ApplicationRoom
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineScope
import javax.inject.Singleton

@Module
class RoomDatabaseModule(
        context: Context,
        coroutineScope: CoroutineScope
): ApplicationRoom.Instance(
        context,
        coroutineScope) {

    @Singleton
    @Provides
    fun provideApplicationRoom(): ApplicationRoom = pDatabase

    @Singleton
    @Provides
    fun provideArrivalRepository(): ArrivalRepository = ArrivalRepository(pDatabase.arrivalDao())

    @Singleton
    @Provides
    fun provideRailSystemRepository(): RailSystemRepository = RailSystemRepository(pDatabase.railSystemDao())

}