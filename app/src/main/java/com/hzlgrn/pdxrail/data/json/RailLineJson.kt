package com.hzlgrn.pdxrail.data.json

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RailLineJson(
    val version: Int,
    @Json(name = "rail_lines")
    val railLines: List<RailLine>
) {
    data class RailLine(
        val line: String?,
        val type: String,
        val passage: String,
        val polyline: List<LatLngAO>
    ) {
        data class LatLngAO(
            @Json(name="a") val latitude: Double,
            @Json(name="o") val longitude: Double)
    }
}