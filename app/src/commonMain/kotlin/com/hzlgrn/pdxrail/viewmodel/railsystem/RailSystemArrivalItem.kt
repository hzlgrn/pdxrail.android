package com.hzlgrn.pdxrail.viewmodel.railsystem

import com.hzlgrn.pdxrail.data.geo.LatLon

data class RailSystemArrivalItem(
    val textShortSign: String,
    val scheduled: Long,
    val estimated: Long,
    val markerIcon: ArrivalMarkerIcon,
    val drawableRotation: Float,
    val lineBackground: ArrivalLineBackground,
    val position: LatLon,
) {
    val isMaxStop get() = markerIcon in setOf(
        ArrivalMarkerIcon.MAX_BLUE,
        ArrivalMarkerIcon.MAX_GREEN,
        ArrivalMarkerIcon.MAX_ORANGE,
        ArrivalMarkerIcon.MAX_RED,
        ArrivalMarkerIcon.MAX_YELLOW,
    )
}
