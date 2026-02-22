package com.hzlgrn.pdxrail.data.json

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RailStopJson(
    val version: Int,
    @Json(name = "rail_stops")
    val railStops: List<RailStop>,
) {
    data class RailStop(
        val station: String?,
        val line: String?,
        val type: String,
        val uniqueid: String,
        val lat: Double,
        val lon: Double
    )
}