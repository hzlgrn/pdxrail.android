package com.hzlgrn.pdxrail.viewmodel.railsystem

import org.maplibre.compose.sources.GeoJsonData
import org.maplibre.spatialk.geojson.Feature
import org.maplibre.spatialk.geojson.FeatureCollection
import org.maplibre.spatialk.geojson.LineString
import org.maplibre.spatialk.geojson.Position
import org.maplibre.spatialk.geojson.toJson

private const val EMPTY_FC_JSON = """{"type":"FeatureCollection","features":[]}"""

fun List<RailSystemMapItem.Line>.toGeoJsonString(): String {
    val features = filter { it.polyline.size >= 2 }.map { line ->
        Feature(
            geometry = LineString(line.polyline.map { Position(it.lon, it.lat) }),
            properties = Unit,
        )
    }
    return FeatureCollection(features).toJson()
}

fun List<RailSystemMapItem.Line>?.toGeoJsonDataJsonString(): GeoJsonData.JsonString = GeoJsonData.JsonString(this?.toGeoJsonString() ?: EMPTY_FC_JSON)
