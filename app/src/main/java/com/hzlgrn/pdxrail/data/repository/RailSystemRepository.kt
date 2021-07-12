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
                        it.uniqueid,
                        it.station,
                        it.line,
                        it.type,
                        LatLng(it.latitude, it.longitude)
                )
            }
            val linesViewModel = lines.map { entity ->
                val polylineBuilder = ArrayList<LatLng>()
                val splits = entity.polylineString.split(" ")
                splits.forEach {
                    val split = it.split(",")
                    if (split.count() == 2) {
                        polylineBuilder.add(LatLng(split[0].toDouble(), split[1].toDouble()))
                    }
                }
                RailSystemMapViewModel.RailLineMapViewModel(
                        entity.line,
                        entity.passage,
                        entity.type,
                        polylineBuilder
                )
            }
            RailSystemMapViewModel(stopsViewModel, linesViewModel)
        }.flowOn(Dispatchers.IO)
    }

}