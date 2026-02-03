package com.hzlgrn.pdxrail.data.room.entity

import androidx.room.Entity
import com.google.android.gms.maps.model.LatLng
import com.hzlgrn.pdxrail.Domain
import com.hzlgrn.pdxrail.data.railsystem.RailSystemMapItem
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

fun RailStopEntity.toRailSystemMapItem(): RailSystemMapItem {
    val position = LatLng(this.latitude, this.longitude)
    return when (this.type) {
        Domain.RailSystem.STOP_MAX ->
            RailSystemMapItem.Marker.Stop.MaxStop(
                position = position,
                uniqueId = RailSystemMapItem.Marker.MarkerId(this.uniqueid),
                stationText = this.station,
            )
        Domain.RailSystem.STOP_STREETCAR ->
            RailSystemMapItem.Marker.Stop.MaxStop(
                position = position,
                uniqueId = RailSystemMapItem.Marker.MarkerId(this.uniqueid),
                stationText = this.station,
            )
        else -> RailSystemMapItem.Marker.Undefined(position = position)
    }
}