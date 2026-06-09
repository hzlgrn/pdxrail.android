package com.hzlgrn.pdxrail.data

import com.hzlgrn.pdxrail.data.geo.LatLon
import org.maplibre.spatialk.geojson.Position

fun LatLon.toPosition() = Position(longitude = lon, latitude = lat)