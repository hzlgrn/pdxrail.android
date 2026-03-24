package com.hzlgrn.pdxrail.data.model.json

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RailLineJson(
    val version: Int,
    @SerialName("rail_lines") val railLines: List<RailLine>,
) {
    @Serializable
    data class RailLine(
        val line: String?,
        val type: String,
        val passage: String,
        val polyline: List<LatLonAO>,
    ) {
        @Serializable
        data class LatLonAO(
            @SerialName("a") val latitude: Double,
            @SerialName("o") val longitude: Double,
        )
    }
}
