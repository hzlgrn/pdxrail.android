package com.hzlgrn.pdxrail.data.repository.viewmodel

import com.google.android.gms.maps.model.LatLng

data class RailSystemMapViewModel(
        val railStops: List<RailStopMapViewModel>,
        val railLines: List<RailLineMapViewModel>
) {
    data class RailStopMapViewModel(
        val uniqueid: String,
        val station: String?,
        val line: String?,
        val type: String,
        val position: LatLng
    )
    data class RailLineMapViewModel(
        val line: String?,
        val passage: String,
        val type: String,
        val polyline: ArrayList<LatLng>
    )
}

sealed class RailSystemMapItem {
    data class Stop(
        val uniqueId: String,
        val station: String?,
        val line: String?,
        val type: String, // instead of type, split to data class, ex: MaxStop, etc
        val position: LatLng,
    ) : RailSystemMapItem()
    data class Line(
        val line: String?,
        val passage: String,
        val type: String, // instead of type, split to data class, ex: BlueLine, etc
        val polyline: ArrayList<LatLng>,
    ) : RailSystemMapItem()
}