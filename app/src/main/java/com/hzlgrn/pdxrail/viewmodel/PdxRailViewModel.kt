package com.hzlgrn.pdxrail.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.MapType
import com.hzlgrn.pdxrail.compose.MapIconBitmapLoader
import com.hzlgrn.pdxrail.data.repository.RailSystemRepository
import com.hzlgrn.pdxrail.data.toLatLon
import com.hzlgrn.pdxrail.data.toRailSystemArrivalItem
import com.hzlgrn.pdxrail.data.toRailSystemMapItem
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
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class PdxRailViewModel @Inject constructor(
    private val railSystemRepository: RailSystemRepository,
    private val mapIconBitmapLoader: MapIconBitmapLoader,
) : ViewModel() {

    private val _isMyLocationEnabled = MutableStateFlow(false)
    val isMyLocationEnabled = _isMyLocationEnabled.asStateFlow()
    fun setIsMyLocationEnabled(isMyLocationEnabled: Boolean) {
        _isMyLocationEnabled.value = isMyLocationEnabled
    }

    private val _isDrawerOpen = MutableStateFlow(false)
    val isDrawerOpen = _isDrawerOpen.asStateFlow()
    fun openDrawer(isOpen: Boolean) {
        _isDrawerOpen.value = isOpen
    }

    private val _isMapTypeDropdownOpen = MutableStateFlow(false)
    val isMapTypeDropdownOpen = _isMapTypeDropdownOpen.asStateFlow()
    fun openMapTypeDropdown(isOpen: Boolean) {
        _isMapTypeDropdownOpen.value = isOpen
    }

    private val _mapType = MutableStateFlow<MapType>(MapType.NORMAL)
    val mapType = _mapType.asStateFlow()
    fun commitMapType(newMapType: MapType) {
        _mapType.value = newMapType
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
                railSystemRepository.flowRailSystemMapData()
                    .collect { mapData ->
                        Timber.d("collected ${mapData.size} map items")
                        val mapItems = mapData.map { it.toRailSystemMapItem() }
                        RailSystemMapState.Display(mapItems.toImmutableList()).let { display ->
                            withContext(Dispatchers.Main) {
                                _railSystemMap.value = display
                            }
                        }
                    }
            }
        }
    }

    private val _stationText = MutableStateFlow("")
    val stationText = _stationText.asStateFlow()

    fun onClickStop(position: LatLng) {
        _stationText.value = ""
        flowArrivals(position,  false)
    }

    fun onClickMaxStop(maxStop: RailSystemMapItem.Marker.Stop.MaxStop) {
        _stationText.value = maxStop.stationText ?: ""
        flowArrivals(maxStop.position,  false)
    }

    fun onClickStreetcarStop(streetcarStop: RailSystemMapItem.Marker.Stop.StreetcarStop) {
        _stationText.value = streetcarStop.stationText ?: ""
        flowArrivals(streetcarStop.position,  true)
    }

    fun onClickCommuterStop(commuterStop: RailSystemMapItem.Marker.Stop.CommuterStop) {
        _stationText.value = commuterStop.stationText ?: ""
        flowArrivals(commuterStop.position,  true)
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

    private fun flowArrivals(
        position: LatLng,
        isStreetCar: Boolean,
    ) {
        _flowRailSystemArrivalsJob = viewModelScope.launch {
            _railSystemArrivals.value = RailSystemArrivals.Loading
            withContext(Dispatchers.IO) {
                val locIds = railSystemRepository.getLocIds(position.toLatLon(), isStreetCar)
                Timber.d("getLocIds: ${position}: ${locIds}")
                combine(
                    railSystemRepository.foreverGetArrivals(locIds, isStreetCar),
                    railSystemRepository.flowArrivalItems(locIds),
                    railSystemRepository.flowArrivalMarkers(locIds),
                ) { _, itemData, markerData ->
                    RailSystemArrivals.Display(
                        details = itemData.map { it.toRailSystemArrivalItem() }.toImmutableList(),
                        mapItems = markerData.map { it.toRailSystemMapItem() }.toImmutableList(),
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

    private val _isMapLoaded = MutableStateFlow(false)
    val isMapLoaded = _isMapLoaded.asStateFlow()
    fun onMapLoaded() {
        if (!_isMapLoaded.value) {
            _isMapLoaded.value = true
        }
    }
}
