package com.hzlgrn.pdxrail.viewmodel.railsystem

import com.google.android.gms.maps.model.LatLng
import com.hzlgrn.pdxrail.R

class RailSystemArrivalItem(
    val textShortSign: String,
    val scheduled: Long,
    val estimated: Long,
    val drawableArrivalMarker: Int,
    val drawableRotation: Float,
    val backgroundColorArrival: Int,
    val latlng: LatLng
) {
    val isMaxStop get() = when (drawableArrivalMarker) {
        R.drawable.marker_max_arrival_blue,
        R.drawable.marker_max_arrival_green,
        R.drawable.marker_max_arrival_orange,
        R.drawable.marker_max_arrival_red,
        R.drawable.marker_max_arrival_yellow,
        -> true
        else -> false
    }
}