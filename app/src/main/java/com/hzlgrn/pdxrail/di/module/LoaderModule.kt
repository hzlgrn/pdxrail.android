package com.hzlgrn.pdxrail.di.module

import com.hzlgrn.pdxrail.data.room.ApplicationRoomLoader
import com.hzlgrn.pdxrail.di.Loader
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class LoaderModule {
    @Binds
    abstract fun bindApplicationRoomLoader(
        impl: ApplicationRoomLoader,
    ): Loader
}