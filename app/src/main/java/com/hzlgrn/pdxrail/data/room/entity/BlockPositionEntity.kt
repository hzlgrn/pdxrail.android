package com.hzlgrn.pdxrail.data.room.entity

import androidx.room.Entity
import com.hzlgrn.pdxrail.data.net.railsystem.WsV2ArrivalsResponse
import com.hzlgrn.pdxrail.data.room.entity.BlockPositionEntity.Companion.TABLE_NAME

@Entity(tableName = TABLE_NAME, primaryKeys = ["id"])
data class BlockPositionEntity (
        val id: Long,
        var routeNumber: Int,
        var signMessage: String?,
        var heading: Int,
        var nextStopSeq: Int,
        var tripID: String,
        var at: Long,
        var signMessageLong: String?,
        var lastLocID: Long?,
        var nextLocID: Long?,
        var lastStopSeq: Int?,
        var vehicleID: Int?,
        var newTrip: Boolean,
        var direction: Int,

        var lat: Double, var lng: Double
) {
        constructor(model: WsV2ArrivalsResponse.Arrival.BlockPosition) : this(
            model.id,
            model.routeNumber,
            model.signMessage,
            model.heading,
            model.nextStopSeq,
            model.tripID,
            model.at,
            model.signMessageLong,
            model.lastLocID,
            model.nextLocID,
            model.lastStopSeq,
            model.vehicleID,
            model.newTrip,
            model.direction,
            model.lat,
            model.lng
        )

        companion object {
            const val TABLE_NAME = "railsystem_block_position"
        }

}