package com.hzlgrn.pdxrail.di.component

import android.content.SharedPreferences
import com.hzlgrn.pdxrail.data.net.RailSystemService
import com.hzlgrn.pdxrail.data.repository.RailSystemRepository
import com.hzlgrn.pdxrail.data.repository.RailSystemArrivalRepository
import com.hzlgrn.pdxrail.data.room.ApplicationRoom
import com.hzlgrn.pdxrail.di.module.ApplicationPreferencesModule
import com.hzlgrn.pdxrail.di.module.RetrofitServiceModule
import com.hzlgrn.pdxrail.di.module.RoomDatabaseModule
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [
    ApplicationPreferencesModule::class,
    RoomDatabaseModule::class,
    RetrofitServiceModule::class])
interface ForApplicationComponent : ApplicationComponent {
    fun provideApplicationRoom(): ApplicationRoom
    fun provideApplicationPreferences(): SharedPreferences
    fun provideTriMetService(): RailSystemService
    fun provideTriMetRepository(): RailSystemArrivalRepository
    fun provideRailSystemMapViewRepository(): RailSystemRepository
}