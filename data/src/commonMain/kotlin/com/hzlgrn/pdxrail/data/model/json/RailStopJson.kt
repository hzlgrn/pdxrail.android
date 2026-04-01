package com.hzlgrn.pdxrail.data.model.json

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RailStopJson(
    val version: Int,
    @SerialName("rail_stops") val railStops: List<RailStop>,
) {
    @Serializable
    data class RailStop(
        val station: String?,
        val line: String?,
        val type: String,
        val uniqueid: String,
        val lat: Double,
        val lon: Double,
    )
}
