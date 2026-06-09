package com.hzlgrn.pdxrail.data

import com.hzlgrn.pdxrail.data.geo.LatLon
import com.hzlgrn.pdxrail.data.help.PdxRailSystemHelper
import com.hzlgrn.pdxrail.data.model.ArrivalItemData
import com.hzlgrn.pdxrail.viewmodel.railsystem.ArrivalLineBackground
import com.hzlgrn.pdxrail.viewmodel.railsystem.ArrivalMarkerIcon
import com.hzlgrn.pdxrail.viewmodel.railsystem.RailSystemArrivalItem

fun ArrivalItemData.toRailSystemArrivalItem(): RailSystemArrivalItem {
    val textShortSign = shortSign.orEmpty().removePrefix(PdxRailSystemHelper.PREFIX_PORTLAND_STREETCAR)
    return RailSystemArrivalItem(
        textShortSign = textShortSign,
        scheduled = scheduled,
        estimated = estimated ?: 0L,
        markerIcon = markerIconFromShortSign(shortSign),
        lineBackground = backgroundFromShortSign(shortSign),
        drawableRotation = heading,
        position = LatLon(lat, lon),
    )
}

private fun markerIconFromShortSign(shortSign: String?): ArrivalMarkerIcon = when {
    shortSign == null -> ArrivalMarkerIcon.DEFAULT
    PdxRailSystemHelper.isBlue(shortSign) -> ArrivalMarkerIcon.MAX_BLUE
    PdxRailSystemHelper.isGreen(shortSign) -> ArrivalMarkerIcon.MAX_GREEN
    PdxRailSystemHelper.isOrange(shortSign) -> ArrivalMarkerIcon.MAX_ORANGE
    PdxRailSystemHelper.isRed(shortSign) -> ArrivalMarkerIcon.MAX_RED
    PdxRailSystemHelper.isYellow(shortSign) -> ArrivalMarkerIcon.MAX_YELLOW
    PdxRailSystemHelper.isNSLine(shortSign) -> ArrivalMarkerIcon.STREETCAR_NS
    PdxRailSystemHelper.isALoop(shortSign) -> ArrivalMarkerIcon.STREETCAR_A_LOOP
    PdxRailSystemHelper.isBLoop(shortSign) -> ArrivalMarkerIcon.STREETCAR_B_LOOP
    else -> ArrivalMarkerIcon.DEFAULT
}

private fun backgroundFromShortSign(shortSign: String?): ArrivalLineBackground = when {
    shortSign == null -> ArrivalLineBackground.DEFAULT
    PdxRailSystemHelper.isBlue(shortSign) -> ArrivalLineBackground.MAX_BLUE
    PdxRailSystemHelper.isGreen(shortSign) -> ArrivalLineBackground.MAX_GREEN
    PdxRailSystemHelper.isOrange(shortSign) -> ArrivalLineBackground.MAX_ORANGE
    PdxRailSystemHelper.isRed(shortSign) -> ArrivalLineBackground.MAX_RED
    PdxRailSystemHelper.isYellow(shortSign) -> ArrivalLineBackground.MAX_YELLOW
    PdxRailSystemHelper.isNSLine(shortSign) -> ArrivalLineBackground.STREETCAR_NS
    PdxRailSystemHelper.isALoop(shortSign) -> ArrivalLineBackground.STREETCAR_A_LOOP
    PdxRailSystemHelper.isBLoop(shortSign) -> ArrivalLineBackground.STREETCAR_B_LOOP
    else -> ArrivalLineBackground.DEFAULT
}
