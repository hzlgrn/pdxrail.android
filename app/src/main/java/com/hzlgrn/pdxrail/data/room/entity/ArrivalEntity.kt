package com.hzlgrn.pdxrail.data.room.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.hzlgrn.pdxrail.data.net.railsystem.WsV2ArrivalsResponse
import com.hzlgrn.pdxrail.data.room.entity.ArrivalEntity.Companion.TABLE_NAME

@Entity(tableName = TABLE_NAME, primaryKeys = ["id"],
    foreignKeys = [ForeignKey(
        entity = BlockPositionEntity::class,
        onDelete = ForeignKey.NO_ACTION,
        onUpdate = ForeignKey.NO_ACTION,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("blockPositionId"))],
    indices = [Index("blockPositionId")]
)
data class ArrivalEntity(
    val id: String,
    var feet: Int,
    var inCongestion: Boolean?,
    var departed: Boolean?,
    var scheduled: Long,
    var loadPercentage: Int?,
    var shortSign: String?,
    var estimated: Long?,
    var detoured: Boolean,
    var tripID: String?,
    var dir: Int,
    var blockID: Long,
    //var detour: List<Long>,
    var route: Int,
    var piece: String?,
    var fullSign: String?,
    var dropOffOnly: Boolean?,
    var vehicleID: String?,
    var showMilesAway: Boolean?,
    var locid: Long,
    var newTrip: Boolean,
    var status: String
) {

    var blockPositionId: Long? = null

    constructor(model: WsV2ArrivalsResponse.Arrival) : this(
        model.id,
        model.feet?:0,
        model.inCongestion,
        model.departed,
        model.scheduled,
        model.loadPercentage,
        model.shortSign,
        model.estimated,
        model.detoured,
        model.tripId,
        model.dir,
        model.blockID,
        model.route,
        model.piece,
        model.fullSign,
        model.dropOffOnly,
        model.vehicleID,
        model.showMilesAway,
        model.locid,
        model.newTrip,
        model.status
    )

    companion object {
        const val TABLE_NAME = "trimet_arrival"
    }

}