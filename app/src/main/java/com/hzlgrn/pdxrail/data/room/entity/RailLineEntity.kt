package com.hzlgrn.pdxrail.data.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.hzlgrn.pdxrail.data.room.entity.RailLineEntity.Companion.TABLE_NAME

@Entity(tableName = TABLE_NAME)
data class RailLineEntity(
    var line: String?,
    var passage: String,
    var type: String,
    var polylineString: String) {

    @PrimaryKey(autoGenerate = true) var id: Int = 0

    companion object {
        const val TABLE_NAME = "rail_line"
    }
}