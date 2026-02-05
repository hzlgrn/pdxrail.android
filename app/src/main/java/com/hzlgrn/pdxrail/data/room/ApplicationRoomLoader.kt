package com.hzlgrn.pdxrail.data.room

import android.content.Context
import com.hzlgrn.pdxrail.di.Loader
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class ApplicationRoomLoader @Inject constructor(
    @ApplicationContext private val context: Context,
    private val applicationRoom: ApplicationRoom,
): Loader {
    override fun load() = applicationRoom.loadRailSystemData(context, applicationRoom)
}