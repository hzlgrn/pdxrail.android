package com.hzlgrn.pdxrail.data.room

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

interface ApplicationRoomLoader {
    fun load()
}

class ApplicationRoomLoaderImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val applicationRoom: ApplicationRoom,
): ApplicationRoomLoader {
    override fun load() = applicationRoom.loadRailSystemData(context, applicationRoom)
}