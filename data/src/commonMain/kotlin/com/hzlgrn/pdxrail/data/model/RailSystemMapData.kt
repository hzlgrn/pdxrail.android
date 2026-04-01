package com.hzlgrn.pdxrail.data.model

sealed class RailSystemMapData {
    data class Stop(
        val uniqueId: String,
        val station: String?,
        val type: String,
        val lat: Double,
        val lon: Double,
    ) : RailSystemMapData()

    data class Line(
        val line: String?,
        val passage: String,
        val type: String,
        val polylineString: String,
    ) : RailSystemMapData()
}
