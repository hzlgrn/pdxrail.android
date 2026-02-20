package com.hzlgrn.pdxrail.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.hzlgrn.pdxrail.compose.MapIconBitmapLoader
import com.hzlgrn.pdxrail.data.repository.PdxRailSystemRepository
import com.hzlgrn.pdxrail.viewmodel.bitmap.MapIconBitmap
import com.hzlgrn.pdxrail.viewmodel.railsystem.RailSystemArrivals
import com.hzlgrn.pdxrail.viewmodel.railsystem.RailSystemMapItem
import com.hzlgrn.pdxrail.viewmodel.railsystem.RailSystemMapState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class PdxRailViewModel @Inject constructor(
    private val railSystemRepository: PdxRailSystemRepository,
    private val mapIconBitmapLoader: MapIconBitmapLoader,
): ViewModel() {
    private val _isMyLocationEnabled = MutableStateFlow(false)
    val isMyLocationEnabled = _isMyLocationEnabled.asStateFlow()
    fun setIsMyLocationEnabled(isMyLocationEnabled: Boolean) {
        _isMyLocationEnabled.value = isMyLocationEnabled
    }

    private val _drawerShouldBeOpened = MutableStateFlow(false)
    val shouldDrawerBeOpen = _drawerShouldBeOpened.asStateFlow()
    fun shouldDrawerBeOpen(shouldDrawerBeOpen: Boolean) {
        _drawerShouldBeOpened.value = shouldDrawerBeOpen
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
    fun onClickMaxStop(position: LatLng, markerId: RailSystemMapItem.Marker.MarkerId? = null) {
        flowArrivals(position, markerId, false)
    }
    fun onClickStreetcarStop(position: LatLng, markerId: RailSystemMapItem.Marker.MarkerId? = null) {
        flowArrivals(position, markerId, true)
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
    private fun flowArrivals(position: LatLng, markerId: RailSystemMapItem.Marker.MarkerId?, isStreetCar: Boolean) {
        _flowRailSystemArrivalsJob = viewModelScope.launch {
            _railSystemArrivals.value = RailSystemArrivals.Loading
            withContext(Dispatchers.IO) {
                val locIds = railSystemRepository.getLocIds(position, isStreetCar)
                combine(
                    railSystemRepository.flowArrivalItems(locIds),
                    railSystemRepository.flowArrivalMarkers(locIds.toLongArray(), isStreetCar),
                ) { arrivalItems, arrivalMarkers ->
                    RailSystemArrivals.Display(
                        details = arrivalItems.toImmutableList(),
                        mapItems = arrivalMarkers.toImmutableList()
                    )
                }.collect { display ->
                    withContext(Dispatchers.Main) {
                        _railSystemArrivals.value = display
                    }
                }
            }
        }
    }

    private val _mapDrawerIcon = MutableStateFlow<MapIconBitmap>(MapIconBitmap.Idle)
    val mapDrawerIcon = _mapDrawerIcon.asStateFlow()
    fun loadMapIcon() {
        if (_mapDrawerIcon.value == MapIconBitmap.Idle) {
            viewModelScope.launch {
                _mapDrawerIcon.value = MapIconBitmap.Loading
                withContext(Dispatchers.IO) {
                    val mapIconBitmap = mapIconBitmapLoader.load()
                    val display = MapIconBitmap.Display(mapIconBitmap = mapIconBitmap)
                    withContext(Dispatchers.Main) {
                        _mapDrawerIcon.value = display
                    }
                }
            }
        }
    }
}