package com.hzlgrn.pdxrail

import com.hzlgrn.pdxrail.activity.BuildConfig

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
        const val WsV2Arrivals = "ws/V2/arrivals?appID=${BuildConfig.KEY_API_RAIL_SYSTEM}&json=true&showPosition=true"
        const val WsV1Stops = "ws/V1/stops?appID=${BuildConfig.KEY_API_RAIL_SYSTEM}&json=true"

        const val PREFIX_PORTLAND_STREETCAR = "Portland Streetcar "
    }

}