package com.hzlgrn.pdxrail.di.module

import com.hzlgrn.pdxrail.data.db.AppDatabase
import com.hzlgrn.pdxrail.data.net.PdxRailSystemClient
import com.hzlgrn.pdxrail.data.repository.RailSystemRepository
import com.hzlgrn.pdxrail.data.repository.railsystem.PdxRailSystemRepository
import com.russhwolf.settings.Settings
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {
    @Provides
    @Singleton
    fun provideRailSystemRepository(
        appDatabase: AppDatabase,
        pdxRailSystemClient: PdxRailSystemClient,
        settings: Settings,
    ): RailSystemRepository = PdxRailSystemRepository(appDatabase, pdxRailSystemClient, settings)
}
