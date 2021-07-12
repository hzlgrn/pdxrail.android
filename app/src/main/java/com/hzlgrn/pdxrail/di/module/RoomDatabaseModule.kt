package com.hzlgrn.pdxrail.di.module

import android.content.Context
import com.hzlgrn.pdxrail.data.repository.RailSystemRepository
import com.hzlgrn.pdxrail.data.repository.RailSystemArrivalRepository
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
    fun provideTriMetRepository(): RailSystemArrivalRepository = RailSystemArrivalRepository(pDatabase.triMetDao())

    @Singleton
    @Provides
    fun provideRailSystemMapViewRepository(): RailSystemRepository = RailSystemRepository(pDatabase.railSystemDao())

}