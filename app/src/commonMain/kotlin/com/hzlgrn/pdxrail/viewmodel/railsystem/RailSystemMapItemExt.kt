package com.hzlgrn.pdxrail.viewmodel.railsystem

import org.maplibre.compose.sources.GeoJsonData

private const val EMPTY_FC_JSON = """{"type":"FeatureCollection","features":[]}"""

fun List<RailSystemMapItem.Line>.toGeoJsonString(): String {
    val features = filter { it.polyline.size >= 2 }.joinToString(",") { line ->
        val coords = line.polyline.joinToString(",") { ll -> "[${ll.lon},${ll.lat}]" }
        """{"type":"Feature","geometry":{"type":"LineString","coordinates":[$coords]},"properties":null}"""
    }
    return """{"type":"FeatureCollection","features":[$features]}"""
}

fun List<RailSystemMapItem.Line>?.toGeoJsonDataJsonString(): GeoJsonData.JsonString = GeoJsonData.JsonString(this?.toGeoJsonString() ?: EMPTY_FC_JSON)
