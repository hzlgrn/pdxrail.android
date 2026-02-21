package com.hzlgrn.pdxrail.data.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.android.gms.maps.model.LatLng
import com.hzlgrn.pdxrail.Domain
import com.hzlgrn.pdxrail.data.room.entity.RailLineEntity.Companion.TABLE_NAME
import com.hzlgrn.pdxrail.viewmodel.railsystem.RailSystemMapItem
import timber.log.Timber

@Entity(tableName = TABLE_NAME)
data class RailLineEntity(
    var line: String?,
    var passage: String,
    var type: String,
    var polylineString: String) {

    @PrimaryKey(autoGenerate = true) var id: Int = 0

    companion object {
        const val TABLE_NAME = "railsystem_rail_line"
    }

}

fun RailLineEntity.toRailSystemMapItem(): RailSystemMapItem.Line {
    val polyline = ArrayList<LatLng>()
    val splits = this.polylineString.split(" ")
    splits.forEach {
        val split = it.split(",")
        if (split.count() == 2) {
            try {
                val lat = split[0].toDouble()
                val lng = split[1].toDouble()
                val latLng = LatLng(lat, lng)
                polyline.add(latLng)
            } catch(err: NumberFormatException) {
                Timber.e(err)
            }
        }
    }
    return when (this.line) {
        Domain.RailSystem.MAX_BLUE -> RailSystemMapItem.Line.MaxBlue(polyline = polyline)
        Domain.RailSystem.MAX_GREEN -> RailSystemMapItem.Line.MaxGreen(polyline = polyline)
        Domain.RailSystem.MAX_ORANGE -> RailSystemMapItem.Line.MaxOrange(polyline = polyline)
        Domain.RailSystem.MAX_RED -> RailSystemMapItem.Line.MaxRed(polyline = polyline)
        Domain.RailSystem.MAX_YELLOW -> RailSystemMapItem.Line.MaxYellow(polyline = polyline)
        Domain.RailSystem.MAX_BLUE_GREEN -> RailSystemMapItem.Line.MaxBlueGreen(polyline = polyline)
        Domain.RailSystem.MAX_BLUE_RED -> RailSystemMapItem.Line.MaxBlueRed(polyline = polyline)
        Domain.RailSystem.MAX_GREEN_ORANGE -> RailSystemMapItem.Line.MaxGreenOrange(polyline = polyline)
        Domain.RailSystem.MAX_GREEN_YELLOW -> RailSystemMapItem.Line.MaxGreenYellow(polyline = polyline)
        Domain.RailSystem.MAX_BLUE_GREEN_RED -> RailSystemMapItem.Line.MaxBlueGreenRed(polyline = polyline)
        Domain.RailSystem.MAX_BLUE_GREEN_RED_YELLOW -> RailSystemMapItem.Line.MaxBlueGreenRedYellow(polyline = polyline)
        Domain.RailSystem.WES -> RailSystemMapItem.Line.WES(polyline = polyline)
        Domain.RailSystem.STREETCAR_A_LOOP -> RailSystemMapItem.Line.StreetcarALoop(polyline = polyline)
        Domain.RailSystem.STREETCAR_B_LOOP -> RailSystemMapItem.Line.StreetcarBLoop(polyline = polyline)
        Domain.RailSystem.STREETCAR_NORTH_SOUTH -> RailSystemMapItem.Line.StreetcarNorthSouth(polyline = polyline)
        Domain.RailSystem.STREETCAR_A_B -> RailSystemMapItem.Line.StreetcarAB(polyline = polyline)
        Domain.RailSystem.STREETCAR_NS_B -> RailSystemMapItem.Line.StreetcarNSB(polyline = polyline)
        Domain.RailSystem.STREETCAR_NS_A -> RailSystemMapItem.Line.StreetcarNSA(polyline = polyline)
        Domain.RailSystem.STREETCAR_MAX_A_B_ORANGE -> RailSystemMapItem.Line.StreetcarMaxABOrange(polyline = polyline)
        Domain.RailSystem.STREETCAR_NS_A_B -> RailSystemMapItem.Line.StreetcarNSAB(polyline = polyline)
        else -> RailSystemMapItem.Line.Basic(polyline = polyline)
    }
}