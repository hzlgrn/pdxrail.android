package com.hzlgrn.pdxrail.di.module

import com.hzlgrn.pdxrail.data.repository.PdxRailSystemRepository
import com.hzlgrn.pdxrail.di.repository.RailSystemRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun bindPdxRailSystemRepository(
        impl: PdxRailSystemRepository,
    ): RailSystemRepository
}