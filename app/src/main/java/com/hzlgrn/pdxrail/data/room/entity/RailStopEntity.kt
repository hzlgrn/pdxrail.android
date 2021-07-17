package com.hzlgrn.pdxrail.data.room.entity

import androidx.room.Entity
import com.hzlgrn.pdxrail.data.room.entity.RailStopEntity.Companion.TABLE_NAME

@Entity(tableName = TABLE_NAME, primaryKeys = ["uniqueid"])
data class RailStopEntity(
    var uniqueid: String,
    val station: String?,
    var line: String?,
    var type : String,
    var latitude: Double,
    var longitude: Double) {

    companion object {
        const val TABLE_NAME = "railsystem_rail_stop"
    }

}