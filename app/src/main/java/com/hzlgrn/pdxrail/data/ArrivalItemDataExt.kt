package com.hzlgrn.pdxrail.data

import com.google.android.gms.maps.model.LatLng
import com.hzlgrn.pdxrail.R
import com.hzlgrn.pdxrail.data.help.PdxRailSystemHelper
import com.hzlgrn.pdxrail.data.model.ArrivalItemData
import com.hzlgrn.pdxrail.viewmodel.railsystem.RailSystemArrivalItem

fun ArrivalItemData.toRailSystemArrivalItem(): RailSystemArrivalItem {
    val textShortSign = shortSign.orEmpty().removePrefix(PdxRailSystemHelper.PREFIX_PORTLAND_STREETCAR)
    return RailSystemArrivalItem(
        textShortSign = textShortSign,
        scheduled = scheduled,
        estimated = estimated ?: 0L,
        drawableArrivalMarker = drawableFromShortSign(shortSign),
        backgroundColorArrival = backgroundColorFromShortSign(shortSign),
        drawableRotation = heading,
        latlng = LatLng(lat, lon),
    )
}

private fun drawableFromShortSign(shortSign: String?): Int = when {
    shortSign == null -> R.drawable.marker_max_arrival
    PdxRailSystemHelper.isBlue(shortSign) -> R.drawable.marker_max_arrival_blue
    PdxRailSystemHelper.isGreen(shortSign) -> R.drawable.marker_max_arrival_green
    PdxRailSystemHelper.isOrange(shortSign) -> R.drawable.marker_max_arrival_orange
    PdxRailSystemHelper.isRed(shortSign) -> R.drawable.marker_max_arrival_red
    PdxRailSystemHelper.isYellow(shortSign) -> R.drawable.marker_max_arrival_yellow
    PdxRailSystemHelper.isNSLine(shortSign) -> R.drawable.marker_streetcar_ns_line
    PdxRailSystemHelper.isALoop(shortSign) -> R.drawable.marker_streetcar_a_loop
    PdxRailSystemHelper.isBLoop(shortSign) -> R.drawable.marker_streetcar_b_loop
    else -> R.drawable.marker_max_arrival
}

private fun backgroundColorFromShortSign(shortSign: String?): Int = when {
    shortSign == null -> android.R.color.white
    PdxRailSystemHelper.isBlue(shortSign) -> R.color.max_blue_line_background
    PdxRailSystemHelper.isGreen(shortSign) -> R.color.max_green_line_background
    PdxRailSystemHelper.isOrange(shortSign) -> R.color.max_orange_line_background
    PdxRailSystemHelper.isRed(shortSign) -> R.color.max_red_line_background
    PdxRailSystemHelper.isYellow(shortSign) -> R.color.max_yellow_line_background
    PdxRailSystemHelper.isNSLine(shortSign) -> R.color.portland_streetcar_north_south_line_background
    PdxRailSystemHelper.isALoop(shortSign) -> R.color.portland_streetcar_a_loop_background
    PdxRailSystemHelper.isBLoop(shortSign) -> R.color.portland_streetcar_b_loop_background
    else -> android.R.color.white
}