package com.hzlgrn.pdxrail.data.room.entity

import androidx.room.Entity

@Entity(tableName = "trimet_locid", primaryKeys = ["latlon"])
data class LocIdEntity(
        val latlon: String,
        var updated: Long,
        var csvlocid: String)