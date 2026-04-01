package com.hzlgrn.pdxrail.data

import com.google.android.gms.maps.model.LatLng
import com.hzlgrn.pdxrail.data.geo.LatLon
import com.hzlgrn.pdxrail.data.help.PdxRailSystemHelper
import com.hzlgrn.pdxrail.data.model.RailSystemMapData
import com.hzlgrn.pdxrail.viewmodel.railsystem.RailSystemMapItem

fun LatLng.toLatLon() = LatLon(latitude, longitude)

fun RailSystemMapData.toRailSystemMapItem(): RailSystemMapItem {
    return when (this) {
        is RailSystemMapData.Stop -> toRailSystemMapItem()
        is RailSystemMapData.Line -> toRailSystemMapItem()
    }
}

fun RailSystemMapData.Stop.toRailSystemMapItem(): RailSystemMapItem {
    val position = LatLng(lat, lon)
    return when (type) {
        PdxRailSystemHelper.STOP_MAX -> RailSystemMapItem.Marker.Stop.MaxStop(
            position = position,
            uniqueId = RailSystemMapItem.Marker.MarkerId(uniqueId),
            stationText = station,
        )
        PdxRailSystemHelper.STOP_STREETCAR -> RailSystemMapItem.Marker.Stop.StreetcarStop(
            position = position,
            uniqueId = RailSystemMapItem.Marker.MarkerId(uniqueId),
            stationText = station,
        )

        PdxRailSystemHelper.STOP_COMMUTER -> RailSystemMapItem.Marker.Stop.CommuterStop(
            position = position,
            uniqueId = RailSystemMapItem.Marker.MarkerId(uniqueId),
            stationText = station,
        )
        else -> RailSystemMapItem.Marker.Undefined(position = position)
    }
}

fun RailSystemMapData.Line.toRailSystemMapItem(): RailSystemMapItem.Line {
    val polyline = ArrayList<LatLng>()
    polylineString.split(" ").forEach { point ->
        val parts = point.split(",")
        if (parts.size == 2) {
            val lat = parts[0].toDoubleOrNull() ?: return@forEach
            val lng = parts[1].toDoubleOrNull() ?: return@forEach
            polyline.add(LatLng(lat, lng))
        }
    }
    return when (line) {
        PdxRailSystemHelper.MAX_BLUE -> RailSystemMapItem.Line.MaxBlue(polyline)
        PdxRailSystemHelper.MAX_GREEN -> RailSystemMapItem.Line.MaxGreen(polyline)
        PdxRailSystemHelper.MAX_ORANGE -> RailSystemMapItem.Line.MaxOrange(polyline)
        PdxRailSystemHelper.MAX_RED -> RailSystemMapItem.Line.MaxRed(polyline)
        PdxRailSystemHelper.MAX_YELLOW -> RailSystemMapItem.Line.MaxYellow(polyline)
        PdxRailSystemHelper.MAX_BLUE_GREEN -> RailSystemMapItem.Line.MaxBlueGreen(polyline)
        PdxRailSystemHelper.MAX_BLUE_RED -> RailSystemMapItem.Line.MaxBlueRed(polyline)
        PdxRailSystemHelper.MAX_GREEN_ORANGE -> RailSystemMapItem.Line.MaxGreenOrange(polyline)
        PdxRailSystemHelper.MAX_GREEN_YELLOW -> RailSystemMapItem.Line.MaxGreenYellow(polyline)
        PdxRailSystemHelper.MAX_BLUE_GREEN_RED -> RailSystemMapItem.Line.MaxBlueGreenRed(polyline)
        PdxRailSystemHelper.MAX_BLUE_GREEN_RED_YELLOW -> RailSystemMapItem.Line.MaxBlueGreenRedYellow(polyline)
        PdxRailSystemHelper.WES -> RailSystemMapItem.Line.WES(polyline)
        PdxRailSystemHelper.STREETCAR_A_LOOP -> RailSystemMapItem.Line.StreetcarALoop(polyline)
        PdxRailSystemHelper.STREETCAR_B_LOOP -> RailSystemMapItem.Line.StreetcarBLoop(polyline)
        PdxRailSystemHelper.STREETCAR_NORTH_SOUTH -> RailSystemMapItem.Line.StreetcarNorthSouth(polyline)
        PdxRailSystemHelper.STREETCAR_A_B -> RailSystemMapItem.Line.StreetcarAB(polyline)
        PdxRailSystemHelper.STREETCAR_NS_B -> RailSystemMapItem.Line.StreetcarNSB(polyline)
        PdxRailSystemHelper.STREETCAR_NS_A -> RailSystemMapItem.Line.StreetcarNSA(polyline)
        PdxRailSystemHelper.STREETCAR_MAX_A_B_ORANGE -> RailSystemMapItem.Line.StreetcarMaxABOrange(polyline)
        PdxRailSystemHelper.STREETCAR_NS_A_B -> RailSystemMapItem.Line.StreetcarNSAB(polyline)
        else -> RailSystemMapItem.Line.Basic(polyline)
    }
}
