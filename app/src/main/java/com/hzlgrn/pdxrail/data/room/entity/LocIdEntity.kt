package com.hzlgrn.pdxrail.data.room.entity

import androidx.room.Entity
import com.hzlgrn.pdxrail.data.room.entity.LocIdEntity.Companion.TABLE_NAME

@Entity(tableName = TABLE_NAME, primaryKeys = ["latlon"])
data class LocIdEntity(
        val latlon: String,
        var updated: Long,
        var csvlocid: String) {

        companion object {
                const val TABLE_NAME = "railsystem_locid"
        }

}