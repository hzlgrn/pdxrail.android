package com.hzlgrn.pdxrail.data.repository.viewmodel

import com.google.android.gms.maps.model.LatLng

class ArrivalItemViewModel(
        val textShortSign: String,
        val scheduled: Long,
        val estimated: Long,
        val drawableArrivalMarker: Int,
        val drawableRotation: Float,
        val latlng: LatLng
)