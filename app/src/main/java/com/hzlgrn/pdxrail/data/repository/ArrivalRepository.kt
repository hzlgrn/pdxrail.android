package com.hzlgrn.pdxrail.data.repository

import android.content.res.Resources
import android.graphics.Color
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.hzlgrn.pdxrail.Domain
import com.hzlgrn.pdxrail.R
import com.hzlgrn.pdxrail.data.repository.viewmodel.ArrivalItemViewModel
import com.hzlgrn.pdxrail.data.repository.viewmodel.UniqueIdModel
import com.hzlgrn.pdxrail.data.room.dao.ArrivalDao
import com.hzlgrn.pdxrail.data.room.model.ArrivalItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.*

class ArrivalRepository(
    private val applicationResources: Resources,
    private val dao: ArrivalDao
) {

    fun arrivalItemsViewModel(listLocId: List<Long>): Flow<List<UniqueIdModel>> {
        return dao.arrivalItemsFor(listLocId).map { listPkArrival ->
            listPkArrival.map { UniqueIdModel(it.id) }
        }
    }
    fun collectArrivalItemViewModel(uniqueId: String): Flow<ArrivalItemViewModel> {
        return dao.collectArrivalItemFor(uniqueId).map {
            toArrivalItemViewModel(it)
        }
    }
    fun getArrivalItemViewModel(uniqueId: String): ArrivalItemViewModel {
        return toArrivalItemViewModel(dao.getArrivalItemFor(uniqueId))
    }

    private fun toArrivalItemViewModel(item: ArrivalItem?): ArrivalItemViewModel {
        return if (item == null) {
            ArrivalItemViewModel(
                textShortSign = " ",
                textScheduled = " ",
                textEstimated = " ",
                colorTextEstimated = Color.YELLOW,
                drawableArrivalMarker = R.drawable.marker_max_arrival,
                drawableRotation = 180f
            )
        } else {
            val arrivalLat = item.blockPosition?.lat ?: 0.0
            val arrivalLon = item.blockPosition?.lng ?: 0.0
            val arrivalPosition = LatLng(arrivalLat,arrivalLon)
            val textShortSign = item.shortSign.orEmpty().removePrefix(Domain.RailSystem.PREFIX_PORTLAND_STREETCAR)
            val textEstimate = if (item.estimated == 0L
                || (item.scheduled > 0L
                        && item.estimated > 0L
                        && item.scheduled in (item.estimated - Domain.RailSystem.RANGE_ON_TIME_MS) .. (item.estimated + Domain.RailSystem.RANGE_ON_TIME_MS))) {
                // NO TEXT ESTIMATE
                " "
            } else {
                applicationResources
                    .getString(R.string.estimated_at)
                    .format(
                        SimpleDateFormat("h:mm:ss", Locale.US)
                            .format(Date(item.estimated)))
            }
            val textScheduled = if (item.scheduled == 0L) {
                applicationResources.getString(R.string.no_arrival)
            } else {
                SimpleDateFormat("h:mma", Locale.US)
                    .format(Date(item.scheduled)).let { formattedString ->
                        applicationResources.getString(R.string.arriving_at).format(formattedString)
                    }
            }
            val isLate = item.estimated > item.scheduled
            val textEstimateColor = applicationResources.getColor(if (isLate)
                R.color.max_red_line
            else
                R.color.max_green_line, applicationResources.newTheme())

            ArrivalItemViewModel(
                textShortSign = textShortSign,
                textScheduled = textScheduled,
                textEstimated = textEstimate,
                colorTextEstimated = textEstimateColor,
                drawableArrivalMarker = drawableFromShortSign(item.shortSign),
                drawableRotation = item.blockPosition?.heading?.toFloat() ?: 0f,
                latlng = arrivalPosition
            )
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