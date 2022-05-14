package com.hzlgrn.pdxrail.data.repository

import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.hzlgrn.pdxrail.Domain
import com.hzlgrn.pdxrail.R
import com.hzlgrn.pdxrail.data.repository.viewmodel.ArrivalItemViewModel
import com.hzlgrn.pdxrail.data.room.dao.ArrivalDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ArrivalRepository(private val dao: ArrivalDao) {

    fun arrivalItemsViewModel(listLocId: List<Long>): Flow<List<ArrivalItemViewModel>> {
        return dao.arrivalItemsFor(listLocId).map { listArrivalItem ->
            listArrivalItem.map {
                val arrivalLat = it.blockPosition.firstOrNull()?.lat ?: 0.0
                val arrivalLon = it.blockPosition.firstOrNull()?.lng ?: 0.0
                val arrivalPosition = LatLng(arrivalLat,arrivalLon)
                val textShortSign = it.shortSign.orEmpty().removePrefix(Domain.RailSystem.PREFIX_PORTLAND_STREETCAR)
                ArrivalItemViewModel(
                    textShortSign = textShortSign,
                    scheduled = it.scheduled,
                    estimated = it.estimated,
                    drawableArrivalMarker = drawableFromShortSign(it.shortSign),
                    drawableRotation = it.blockPosition.firstOrNull()?.heading?.toFloat() ?: 0f,
                    latlng = arrivalPosition
                )
            }
        }
    }

    fun arrivalMarkersViewModel(listLocId: List<Long>): Flow<List<MarkerOptions>> {
        return dao.arrivalMarkersFor(listLocId).map { listArrivals ->
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

            Domain.RailSystem.isBlue(shortSign) -> R.drawable.marker_max_arrival_blue
            Domain.RailSystem.isGreen(shortSign) -> R.drawable.marker_max_arrival_green
            Domain.RailSystem.isOrange(shortSign) -> R.drawable.marker_max_arrival_orange
            Domain.RailSystem.isRed(shortSign) -> R.drawable.marker_max_arrival_red
            Domain.RailSystem.isYellow(shortSign) -> R.drawable.marker_max_arrival_yellow
            Domain.RailSystem.isNSLine(shortSign) -> R.drawable.marker_streetcar_ns_line
            Domain.RailSystem.isALoop(shortSign) -> R.drawable.marker_streetcar_a_loop
            Domain.RailSystem.isBLoop(shortSign) -> R.drawable.marker_streetcar_b_loop

            else -> R.drawable.marker_max_arrival
        }
    }

}