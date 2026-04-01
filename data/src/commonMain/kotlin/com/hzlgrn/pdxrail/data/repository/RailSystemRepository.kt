package com.hzlgrn.pdxrail.data.repository

import com.hzlgrn.pdxrail.data.geo.LatLon
import com.hzlgrn.pdxrail.data.model.ArrivalItemData
import com.hzlgrn.pdxrail.data.model.ArrivalMarkerData
import com.hzlgrn.pdxrail.data.model.RailSystemMapData
import kotlinx.coroutines.flow.Flow

interface RailSystemRepository {
    fun flowRailSystemMapData(): Flow<List<RailSystemMapData>>
    suspend fun getLocIds(latLon: LatLon, isStreetCar: Boolean): List<Long>
    fun flowArrivalMarkers(locIds: List<Long>): Flow<List<ArrivalMarkerData>>
    fun flowArrivalItems(locIds: List<Long>): Flow<List<ArrivalItemData>>
    fun foreverGetArrivals(locIds: List<Long>, isStreetCar: Boolean): Flow<Boolean>
}
