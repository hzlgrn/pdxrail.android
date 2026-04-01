package com.hzlgrn.pdxrail.data.help

import com.hzlgrn.pdxrail.data.geo.LatLon

object PdxRailSystemHelper {

    const val DB_NAME = "pdxrail.db"

    const val SETTING_HAS_SHOWN_HELP_DIALOG = "has_shown_help_dialog"
    fun LatLon.isNWLovejoyAND22nd() =
        lat == 45.52974474648903 && lon == -122.69688015146481

    fun LatLon.isPioneerPlace() =
        lat == 45.51849907171159 && lon == -122.6777873516982

    /**
     * Bounds for the city of Portland
     */
    fun LatLon.isInCity() =
        lat < 45.536915 && lat > 45.504861 && lon > -122.698876 && lon < -122.667121

    /**
     * Radius is in Feet, represented as a Long for convenience in later calculations
     */
    fun LatLon.radiusPingLocIds(isStreetCar: Boolean) = when {
        isNWLovejoyAND22nd() -> 200L
        isStreetCar -> 50L
        isPioneerPlace() -> 100L
        isInCity() -> 125L
        else -> 200L
    }

    val REGION_RECT_NW = LatLon(45.698441, -123.183735)
    val REGION_RECT_SE = LatLon(45.214177, -122.303207)

    const val PREFIX_PORTLAND_STREETCAR = "Portland Streetcar "
    const val STREETCAR_IN_FULLSIGN = "streetcar"

    const val MAX = "MAX"
    const val WES = "WES"

    const val STOP_MAX = MAX
    const val STOP_COMMUTER = "CR"
    const val STOP_STREETCAR = "SC"

    const val MAX_BLUE = "B"
    const val MAX_GREEN = "G"
    const val MAX_ORANGE= "O"
    const val MAX_RED = "R"
    const val MAX_YELLOW = "Y"

    const val MAX_BLUE_GREEN = "BG"
    const val MAX_BLUE_RED = "BR"
    const val MAX_GREEN_ORANGE = "GO"
    const val MAX_GREEN_YELLOW = "GY"

    const val MAX_BLUE_GREEN_RED = "BGR"

    const val MAX_BLUE_GREEN_RED_YELLOW = "BGRY"

    const val STREETCAR_A_LOOP = "AL"
    const val STREETCAR_B_LOOP = "BL"
    const val STREETCAR_NORTH_SOUTH = "NS"

    const val STREETCAR_A_B = "AL/BL"
    const val STREETCAR_NS_B = "NS/BL"
    const val STREETCAR_NS_A = "NS/AL"
    const val STREETCAR_MAX_A_B_ORANGE = "O/AL/BL"
    const val STREETCAR_NS_A_B = "NS/AL/BL"

    object CAMERA {
        val TARGET = LatLon(TARGET_LAT, TARGET_LNG)
        const val TARGET_LAT = 45.5231
        const val TARGET_LNG = -122.6765
        const val ZOOM = 15f
    }

    fun isBlue(shortSign: String) = shortSign.contains("blue",true)
    fun isGreen(shortSign: String) = shortSign.contains("green",true)
    fun isOrange(shortSign: String) = shortSign.contains("orange",true)
    fun isRed(shortSign: String) = shortSign.contains("red",true)
    fun isYellow(shortSign: String) = shortSign.contains("yellow",true)
    fun isNSLine(shortSign: String) = shortSign.contains("ns line",true)
    fun isALoop(shortSign: String): Boolean {
        return shortSign.contains("a loop",true)
                || shortSign.contains("loop a",true)
    }
    fun isBLoop(shortSign: String): Boolean {
        return shortSign.contains("b loop",true)
                || shortSign.contains("loop b",true)
    }

}