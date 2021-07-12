package com.hzlgrn.pdxrail.data.repository

import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.hzlgrn.pdxrail.Domain
import com.hzlgrn.pdxrail.R
import com.hzlgrn.pdxrail.data.repository.viewmodel.ArrivalItemViewModel
import com.hzlgrn.pdxrail.data.room.dao.TriMetDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RailSystemArrivalRepository(private val dao: TriMetDao) {

    fun arrivalItemsViewModel(locIds: List<Long>): Flow<List<ArrivalItemViewModel>> {
        return dao.arrivalItemsFor(locIds).map { listArrivalItem ->
            listArrivalItem.map {
                val arrivalPosition = LatLng(it.blockPosition.firstOrNull()?.lat ?: 0.0,it.blockPosition.firstOrNull()?.lng ?: 0.0)
                ArrivalItemViewModel(
                        textShortSign = (it.shortSign ?: "").removePrefix(Domain.RailSystem.PREFIX_PORTLAND_STREETCAR),
                        scheduled = it.scheduled,
                        estimated = it.estimated,
                        drawableArrivalMarker = drawableFromShortSign(it.shortSign),
                        drawableRotation = it.blockPosition.firstOrNull()?.heading?.toFloat() ?: 0f,
                        latlng = arrivalPosition
                )
            }
        }
    }

    fun arrivalMarkersViewModel(locIds: List<Long>): Flow<List<MarkerOptions>> {
        return dao.arrivalMarkersFor(locIds).map { listArrivals ->
            listArrivals.filter { it.blockPosition.isNotEmpty() }.map {
                val blockPosition = it.blockPosition.firstOrNull()!!
                MarkerOptions()
                        .flat(true)
                        .icon(BitmapDescriptorFactory.fromResource(drawableFromShortSign(it.shortSign)))
                        .anchor(0.5f,0.5f)
                        .position(LatLng(blockPosition.lat,blockPosition.lng))
                        .rotation(blockPosition.heading.toFloat())
                        .visible(blockPosition.lat != 0.0 && blockPosition.lng != 0.0)
            }
        }
    }

    private fun drawableFromShortSign(shortSign: String?): Int {
        return when {
            shortSign == null -> R.drawable.marker_max_arrival

            shortSign.contains("blue",true) ->
                R.drawable.marker_max_arrival_blue

            shortSign.contains("green",true) ->
                R.drawable.marker_max_arrival_green

            shortSign.contains("orange",true) ->
                R.drawable.marker_max_arrival_orange

            shortSign.contains("red",true) ->
                R.drawable.marker_max_arrival_red

            shortSign.contains("yellow",true) ->
                R.drawable.marker_max_arrival_yellow

            shortSign.contains("ns line",true) ->
                R.drawable.marker_streetcar_ns_line

            shortSign.contains("a loop",true)
                    || shortSign.contains("loop a", true) ->
                R.drawable.marker_streetcar_a_loop

            shortSign.contains("b loop",true)
                    || shortSign.contains("loop b", true) ->
                R.drawable.marker_streetcar_b_loop

            else -> R.drawable.marker_max_arrival
        }
    }

}