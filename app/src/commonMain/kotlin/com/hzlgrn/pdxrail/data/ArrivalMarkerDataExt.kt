package com.hzlgrn.pdxrail.data

import com.hzlgrn.pdxrail.data.geo.LatLon
import com.hzlgrn.pdxrail.data.help.PdxRailSystemHelper
import com.hzlgrn.pdxrail.data.model.ArrivalMarkerData
import com.hzlgrn.pdxrail.viewmodel.railsystem.RailSystemMapItem

fun ArrivalMarkerData.toRailSystemMapItem(): RailSystemMapItem.Marker.Arrival {
    val position = LatLon(lat, lon)
    val sign = shortSign
    return when {
        sign == null -> RailSystemMapItem.Marker.Arrival.Default(position, heading.toFloat())
        PdxRailSystemHelper.isBlue(sign) -> RailSystemMapItem.Marker.Arrival.MaxBlue(position, heading.toFloat())
        PdxRailSystemHelper.isGreen(sign) -> RailSystemMapItem.Marker.Arrival.MaxGreen(position, heading.toFloat())
        PdxRailSystemHelper.isOrange(sign) -> RailSystemMapItem.Marker.Arrival.MaxOrange(position, heading.toFloat())
        PdxRailSystemHelper.isRed(sign) -> RailSystemMapItem.Marker.Arrival.MaxRed(position, heading.toFloat())
        PdxRailSystemHelper.isYellow(sign) -> RailSystemMapItem.Marker.Arrival.MaxYellow(position, heading.toFloat())
        PdxRailSystemHelper.isNSLine(sign) -> RailSystemMapItem.Marker.Arrival.NSLine(position, heading.toFloat())
        PdxRailSystemHelper.isALoop(sign) -> RailSystemMapItem.Marker.Arrival.ALoop(position, heading.toFloat())
        PdxRailSystemHelper.isBLoop(sign) -> RailSystemMapItem.Marker.Arrival.BLoop(position, heading.toFloat())
        else -> RailSystemMapItem.Marker.Arrival.Default(position, heading.toFloat())
    }
}
