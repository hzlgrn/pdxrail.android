package com.hzlgrn.pdxrail.data.repository

import com.hzlgrn.pdxrail.data.geo.LatLon
import com.hzlgrn.pdxrail.data.model.ArrivalItemData
import com.hzlgrn.pdxrail.data.model.ArrivalMarkerData
import com.hzlgrn.pdxrail.data.model.RailSystemMapData
import kotlinx.coroutines.flow.Flow

interface RailSystemRepository {
    fun flowRailSystemMapData(): Flow<List<RailSystemMapData>>
    fun flowRailSystemMapDataByRegion(north: Double, south: Double, east: Double, west: Double): Flow<List<RailSystemMapData>>
    suspend fun getLocIds(latLon: LatLon, isStreetCar: Boolean): List<Long>
    fun flowArrivalMarkers(locIds: List<Long>): Flow<List<ArrivalMarkerData>>
    fun flowArrivalItems(locIds: List<Long>): Flow<List<ArrivalItemData>>

    /**
     * Continuously polls arrivals for [locIds] and writes them to the local database.
     *
     * Emits true when the data is current (freshly fetched, or nothing to fetch)
     * and false when the most recent fetch failed. The flow never completes on its
     * own; collect it within a cancellable scope.
     */
    fun foreverGetArrivals(locIds: List<Long>, isStreetCar: Boolean): Flow<Boolean>
}
