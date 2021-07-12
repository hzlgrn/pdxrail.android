package com.hzlgrn.pdxrail

import com.google.android.gms.maps.model.LatLng

class Domain {

    object App {
        const val APPLICATION_PREFERENCES = "application_preferences"
        const val DB_NAME = "application.db"

        enum class PREFERENCE(var type: String) {
            DIALOG_HELP_PERMISSION("dialog_help_permission_shown"),
            MENU_MAP_TYPE("menu_map_type_int")
        }
    }

    object Intent {
        const val GEO = "geo:"
    }

    object RailSystem {
        val REGION_RECT_NW = LatLng(45.698441, -123.183735)
        val REGION_RECT_SE = LatLng(45.214177, -122.303207)

        const val WsV2Arrivals = "ws/V2/arrivals?appID=${BuildConfig.KEY_API_RAIL_SYSTEM}&json=true&showPosition=true"
        const val WsV1Stops = "ws/V1/stops?appID=${BuildConfig.KEY_API_RAIL_SYSTEM}&json=true"

        const val PREFIX_PORTLAND_STREETCAR = "Portland Streetcar "

        const val STOP_MAX = "MAX"
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

        const val WES = "WES"

        const val STREETCAR_A_LOOP = "AL"
        const val STREETCAR_B_LOOP = "BL"
        const val STREETCAR_NORTH_SOUTH = "NS"

        const val STREETCAR_A_B = "AL/BL"
        const val STREETCAR_NS_B = "NS/BL"
        const val STREETCAR_NS_A = "NS/AL"
        const val STREETCAR_MAX_A_B_ORANGE = "O/AL/BL"
        const val STREETCAR_NS_A_B = "NS/AL/BL"
    }

}