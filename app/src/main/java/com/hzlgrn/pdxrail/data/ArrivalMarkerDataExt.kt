package com.hzlgrn.pdxrail.data

import com.google.android.gms.maps.model.LatLng
import com.hzlgrn.pdxrail.data.help.PdxRailSystemHelper
import com.hzlgrn.pdxrail.data.model.ArrivalMarkerData
import com.hzlgrn.pdxrail.viewmodel.railsystem.RailSystemMapItem

fun ArrivalMarkerData.toRailSystemMapItem(): RailSystemMapItem.Marker.Arrival {
    val position = LatLng(lat, lon)
    val sign = shortSign
    return when {
        sign == null -> RailSystemMapItem.Marker.Arrival.Default(position, heading)
        PdxRailSystemHelper.isBlue(sign) -> RailSystemMapItem.Marker.Arrival.MaxBlue(position, heading)
        PdxRailSystemHelper.isGreen(sign) -> RailSystemMapItem.Marker.Arrival.MaxGreen(position, heading)
        PdxRailSystemHelper.isOrange(sign) -> RailSystemMapItem.Marker.Arrival.MaxOrange(position, heading)
        PdxRailSystemHelper.isRed(sign) -> RailSystemMapItem.Marker.Arrival.MaxRed(position, heading)
        PdxRailSystemHelper.isYellow(sign) -> RailSystemMapItem.Marker.Arrival.MaxYellow(position, heading)
        PdxRailSystemHelper.isNSLine(sign) -> RailSystemMapItem.Marker.Arrival.NSLine(position, heading)
        PdxRailSystemHelper.isALoop(sign) -> RailSystemMapItem.Marker.Arrival.ALoop(position, heading)
        PdxRailSystemHelper.isBLoop(sign) -> RailSystemMapItem.Marker.Arrival.BLoop(position, heading)
        else -> RailSystemMapItem.Marker.Arrival.Default(position, heading)
    }
}