package com.hzlgrn.pdxrail.di.repository

import com.google.android.gms.maps.model.LatLng
import com.hzlgrn.pdxrail.viewmodel.railsystem.RailSystemArrivalItem
import com.hzlgrn.pdxrail.viewmodel.railsystem.RailSystemMapItem
import kotlinx.coroutines.flow.Flow

interface RailSystemRepository {
    fun flowRailSystemMapItems(): Flow<List<RailSystemMapItem>>
    fun getLocIds(latLng: LatLng, isStreetCar: Boolean): List<Long>
    fun flowArrivalMarkers(locIds: LongArray, isStreetcar: Boolean): Flow<List<RailSystemMapItem.Marker.Arrival>>
    fun flowArrivalItems(listLocId: List<Long>): Flow<List<RailSystemArrivalItem>>
}