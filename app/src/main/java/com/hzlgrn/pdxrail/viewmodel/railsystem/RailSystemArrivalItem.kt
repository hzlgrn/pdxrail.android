package com.hzlgrn.pdxrail.viewmodel.railsystem

import com.google.android.gms.maps.model.LatLng

class RailSystemArrivalItem(
    val textShortSign: String,
    val scheduled: Long,
    val estimated: Long,
    val drawableArrivalMarker: Int,
    val drawableRotation: Float,
    val latlng: LatLng
)