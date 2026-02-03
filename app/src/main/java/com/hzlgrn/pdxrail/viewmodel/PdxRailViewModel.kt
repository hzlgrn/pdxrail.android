package com.hzlgrn.pdxrail.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.hzlgrn.pdxrail.data.repository.RailSystemRepository
import com.hzlgrn.pdxrail.data.repository.viewmodel.RailSystemMapItem
import com.hzlgrn.pdxrail.data.room.entity.ArrivalEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class PdxRailViewModel @Inject constructor(
    private val railSystemRepository: RailSystemRepository,
): ViewModel() {

    private val _drawerShouldBeOpened = MutableStateFlow(false)
    val drawerShouldBeOpened = _drawerShouldBeOpened.asStateFlow()

    fun openDrawer() {
        _drawerShouldBeOpened.value = true
    }

    fun resetOpenDrawerAction() {
        _drawerShouldBeOpened.value = false
    }

    /***
     * The RailSystemMap contains static map features and when they are made available by the
     * Database.
     */
    sealed class RailSystemMapState {
        object Idle: RailSystemMapState()
        object Loading: RailSystemMapState()
        data class Display(val mapItems: ImmutableList<RailSystemMapItem>): RailSystemMapState()
    }
    private val _railSystemMap = MutableStateFlow<RailSystemMapState>(RailSystemMapState.Idle)
    val railSystemMap = _railSystemMap.asStateFlow()
    private var _flowMapJob: Job? = null
        set(job) {
            field?.cancel()
            if (field == null) {
                _railSystemMap.value = RailSystemMapState.Idle
            }
            field = job
        }
    @OptIn(FlowPreview::class)
    fun flowRailSystemMap() {
        _flowMapJob = viewModelScope.launch {
            _railSystemMap.value = RailSystemMapState.Loading
            withContext(Dispatchers.IO) {
                railSystemRepository.flowRailSystemMapItems().debounce(timeoutMillis = 3000).collect { mapItems ->
                    Timber.d("collected ${mapItems.size} map items")
                    RailSystemMapState.Display(mapItems.toImmutableList()).let { display ->
                        withContext(Dispatchers.Main) {
                            _railSystemMap.value = display
                        }
                    }
                }
            }
        }
    }

    /***
     * RailSystemArrivals is the data collected from the rail system's API when a stop is focused.
     */
    sealed class RailSystemArrivals {
        data object Idle : RailSystemArrivals()
        data object Loading : RailSystemArrivals()
        data class Display(val arrivals: List<ArrivalEntity>): RailSystemArrivals()
    }
    private val _railSystemArrivals = MutableStateFlow<RailSystemArrivals>(RailSystemArrivals.Idle)
    val railSystemArrivals = _railSystemArrivals.asStateFlow()
    private var _flowRailSystemArrivalsJob: Job? = null
        set(job) {
            field?.cancel()
            if (field == null) {
                _railSystemArrivals.value = RailSystemArrivals.Idle
            }
            field = job
        }
    fun onClickMaxStop(position: LatLng, markerId: RailSystemMapItem.Marker.MarkerId? = null) {
        flowArrivals(position, markerId, false)
    }
    fun onClickStreetcarStop(position: LatLng, markerId: RailSystemMapItem.Marker.MarkerId? = null) {
        flowArrivals(position, markerId, true)
    }
    private fun flowArrivals(position: LatLng, markerId: RailSystemMapItem.Marker.MarkerId?, isStreetCar: Boolean) {
        _flowRailSystemArrivalsJob = viewModelScope.launch {
            _railSystemArrivals.value = RailSystemArrivals.Loading
            withContext(Dispatchers.IO) {
                val locIds = railSystemRepository.getLocIds(position, isStreetCar)
                railSystemRepository.flowArrivals(locIds.toLongArray(), isStreetCar).collect { arrivalEntities ->
                    _railSystemArrivals.value = RailSystemArrivals.Display(arrivalEntities)
                }
            }
        }
    }
}