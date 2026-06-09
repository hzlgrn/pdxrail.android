package com.hzlgrn.pdxrail.viewmodel

import com.hzlgrn.pdxrail.data.geo.LatLon

data class MapViewport(
    val center: LatLon,
    val zoom: Double,
    val bounds: MapBounds? = null,
)
