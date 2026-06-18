package com.hzlgrn.pdxrail.viewmodel.railsystem

import kotlinx.collections.immutable.ImmutableList
import org.maplibre.compose.sources.GeoJsonData

sealed class RailSystemMapState {
    data object Idle : RailSystemMapState()
    data object Loading : RailSystemMapState()
    data class Display(
        val maxStopData: ImmutableList<RailSystemMapItem.Marker.Stop.MaxStop>,
        val streetcarStopData: ImmutableList<RailSystemMapItem.Marker.Stop.StreetcarStop>,
        val commuterStopData: ImmutableList<RailSystemMapItem.Marker.Stop.CommuterStop>,

        val maxStops: GeoJsonData.JsonString,
        val streetcarStops: GeoJsonData.JsonString,

        val blueFC: GeoJsonData.JsonString,
        val greenFC: GeoJsonData.JsonString,
        val orangeFC: GeoJsonData.JsonString,
        val redFC: GeoJsonData.JsonString,
        val yellowFC: GeoJsonData.JsonString,
        val wesFC: GeoJsonData.JsonString,
        val blueGreenFC: GeoJsonData.JsonString,
        val blueRedFC: GeoJsonData.JsonString,
        val greenOrangeFC: GeoJsonData.JsonString,
        val greenYellowFC: GeoJsonData.JsonString,
        val bgrFC: GeoJsonData.JsonString,
        val bgryFC: GeoJsonData.JsonString,
        val scAFC: GeoJsonData.JsonString,
        val scBFC: GeoJsonData.JsonString,
        val scNsFC: GeoJsonData.JsonString,
        val scAbFC: GeoJsonData.JsonString,
        val scNsaFC: GeoJsonData.JsonString,
        val scNsbFC: GeoJsonData.JsonString,
        val scNsabFC: GeoJsonData.JsonString,
        val scMaxAbOrFC: GeoJsonData.JsonString,
        val basicFC: GeoJsonData.JsonString,

    ) : RailSystemMapState()
}
