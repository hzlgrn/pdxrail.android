package com.hzlgrn.pdxrail.viewmodel.railsystem

import kotlinx.collections.immutable.ImmutableList
import org.maplibre.compose.sources.GeoJsonData

sealed class RailSystemArrivals {
    data object Idle : RailSystemArrivals()
    data object Loading : RailSystemArrivals()

    /**
     * The latest arrivals poll failed and there is no fresh data to display.
     * Shown instead of a misleading "no arrivals" state.
     */
    data object Error : RailSystemArrivals()

    data class Display(
        val details: ImmutableList<RailSystemArrivalItem>,
        val arrivalBlueFeatures: GeoJsonData.JsonString,
        val arrivalGreenFeatures: GeoJsonData.JsonString,
        val arrivalOrangeFeatures: GeoJsonData.JsonString,
        val arrivalRedFeatures: GeoJsonData.JsonString,
        val arrivalYellowFeatures: GeoJsonData.JsonString,
        val arrivalNSFeatures: GeoJsonData.JsonString,
        val arrivalALoopFeatures: GeoJsonData.JsonString,
        val arrivalBLoopFeatures: GeoJsonData.JsonString,
        val arrivalDefaultFeatures: GeoJsonData.JsonString,
    ) : RailSystemArrivals()
}
