package com.hzlgrn.pdxrail.viewmodel.railsystem

import kotlinx.collections.immutable.ImmutableList

/***
 * RailSystemArrivals is the data collected from the rail system's API when a stop is focused.
 */
sealed class RailSystemArrivals {
    data object Idle : RailSystemArrivals()
    data object Loading : RailSystemArrivals()
    data class Display(val arrivals: ImmutableList<RailSystemMapItem.Marker.Arrival>): RailSystemArrivals()
}