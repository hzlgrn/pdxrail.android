package com.hzlgrn.pdxrail.data.repository

import com.google.android.gms.maps.model.LatLng
import com.hzlgrn.pdxrail.data.repository.viewmodel.RailSystemMapViewModel
import com.hzlgrn.pdxrail.data.room.dao.RailSystemDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn

class RailSystemRepository(private val dao: RailSystemDao) {

    fun mapViewModel(): Flow<RailSystemMapViewModel> {
        return combine(dao.railStops(), dao.railLines()) { stops, lines ->
            val stopsViewModel = stops.map {
                RailSystemMapViewModel.RailStopMapViewModel(
                        uniqueid = it.uniqueid,
                        station = it.station,
                        line = it.line,
                        type = it.type,
                        position = LatLng(it.latitude, it.longitude)
                )
            }
            val linesViewModel = lines.map { entity ->
                val polyline = ArrayList<LatLng>()
                val splits = entity.polylineString.split(" ")
                splits.forEach {
                    val split = it.split(",")
                    if (split.count() == 2) {
                        polyline.add(LatLng(split[0].toDouble(), split[1].toDouble()))
                    }
                }
                RailSystemMapViewModel.RailLineMapViewModel(
                        line = entity.line,
                        passage = entity.passage,
                        type = entity.type,
                        polyline = polyline
                )
            }
            RailSystemMapViewModel(stopsViewModel, linesViewModel)
        }.flowOn(Dispatchers.IO)
    }

}