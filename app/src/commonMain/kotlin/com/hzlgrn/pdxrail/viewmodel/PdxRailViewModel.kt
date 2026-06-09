package com.hzlgrn.pdxrail.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hzlgrn.pdxrail.compose.pdxrail.toGeoJsonString
import com.hzlgrn.pdxrail.data.geo.LatLon
import com.hzlgrn.pdxrail.data.help.PdxRailSystemHelper
import com.hzlgrn.pdxrail.data.repository.RailSystemRepository
import com.hzlgrn.pdxrail.data.toRailSystemArrivalItem
import com.hzlgrn.pdxrail.data.toRailSystemMapItem
import com.hzlgrn.pdxrail.viewmodel.railsystem.RailSystemArrivals
import com.hzlgrn.pdxrail.viewmodel.railsystem.RailSystemMapItem
import com.hzlgrn.pdxrail.viewmodel.railsystem.RailSystemMapState
import com.hzlgrn.pdxrail.viewmodel.railsystem.toGeoJsonDataJsonString
import com.russhwolf.settings.Settings
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.maplibre.compose.sources.GeoJsonData

class PdxRailViewModel(
    private val railSystemRepository: RailSystemRepository,
    private val settings: Settings,
) : ViewModel() {

    private val _isMyLocationEnabled = MutableStateFlow(false)
    val isMyLocationEnabled = _isMyLocationEnabled.asStateFlow()
    fun setIsMyLocationEnabled(enabled: Boolean) { _isMyLocationEnabled.value = enabled }

    private val _isDrawerOpen = MutableStateFlow(false)
    val isDrawerOpen = _isDrawerOpen.asStateFlow()
    fun openDrawer(isOpen: Boolean) { _isDrawerOpen.value = isOpen }

    private val _isHelpDialogVisible = MutableStateFlow(false)
    val isHelpDialogVisible = _isHelpDialogVisible.asStateFlow()

    fun initHelpDialog() {
        if (!settings.getBoolean(PdxRailSystemHelper.SETTING_HAS_SHOWN_HELP_DIALOG, false)) {
            settings.putBoolean(PdxRailSystemHelper.SETTING_HAS_SHOWN_HELP_DIALOG, true)
            _isHelpDialogVisible.value = true
        }
    }

    fun showHelpDialog() { _isHelpDialogVisible.value = true }
    fun dismissHelpDialog() { _isHelpDialogVisible.value = false }

    private val _railSystemMap = MutableStateFlow<RailSystemMapState>(RailSystemMapState.Idle)
    val railSystemMap = _railSystemMap.asStateFlow()
    private var _flowRailSystemMapJob: Job? = null
        set(job) {
            field?.cancel()
            if (field == null) _railSystemMap.value = RailSystemMapState.Idle
            field = job
        }

    fun flowRailSystemMap() = startFlowingMapData(railSystemRepository.flowRailSystemMapData())

    private fun startFlowingMapData(flow: Flow<List<com.hzlgrn.pdxrail.data.model.RailSystemMapData>>) {
        _flowRailSystemMapJob = viewModelScope.launch {
            withContext(Dispatchers.Default) {
                flow.collect { mapData ->
                        with (mapData.map { it.toRailSystemMapItem() }) {
                            val maxStops = filterIsInstance<RailSystemMapItem.Marker.Stop.MaxStop>().toImmutableList()
                            val streetcarStops = filterIsInstance<RailSystemMapItem.Marker.Stop.StreetcarStop>().toImmutableList()
                            val commuterStops = filterIsInstance<RailSystemMapItem.Marker.Stop.CommuterStop>().toImmutableList()
                            val lineItems = filterIsInstance<RailSystemMapItem.Line>()

                            val maxStopFeatures = (maxStops.map { stop ->
                                val id = stop.uniqueId.uniqueIdString
                                """{"type":"Feature","geometry":{"type":"Point","coordinates":[${stop.position.lon},${stop.position.lat}]},"properties":{"id":"$id","type":"max"}}"""
                            } + commuterStops.map { stop ->
                                val id = stop.uniqueId.uniqueIdString
                                """{"type":"Feature","geometry":{"type":"Point","coordinates":[${stop.position.lon},${stop.position.lat}]},"properties":{"id":"$id","type":"commuter"}}"""
                            }).joinToString(",")

                            val maxStopJson = """{"type":"FeatureCollection","features":[$maxStopFeatures]}"""

                            val streetcarFeatures = streetcarStops.joinToString(",") { stop ->
                                val id = stop.uniqueId.uniqueIdString
                                """{"type":"Feature","geometry":{"type":"Point","coordinates":[${stop.position.lon},${stop.position.lat}]},"properties":{"id":"$id","type":"streetcar"}}"""
                            }
                            val streetcarStopJson = """{"type":"FeatureCollection","features":[$streetcarFeatures]}"""

                            RailSystemMapState.Display(
                                maxStopData = maxStops,
                                streetcarStopData = streetcarStops,
                                commuterStopData = commuterStops,

                                // Group all segments by line type, one FeatureCollection per type.
                                // This keeps the MapLibre layer count low regardless of segment count.
                                maxStops = GeoJsonData.JsonString(maxStopJson),
                                streetcarStops = GeoJsonData.JsonString(streetcarStopJson),

                                blueFC = lineItems.filterIsInstance<RailSystemMapItem.Line.MaxBlue>().toGeoJsonDataJsonString(),
                                greenFC = lineItems.filterIsInstance<RailSystemMapItem.Line.MaxGreen>().toGeoJsonDataJsonString(),
                                orangeFC = lineItems.filterIsInstance<RailSystemMapItem.Line.MaxOrange>().toGeoJsonDataJsonString(),
                                redFC = lineItems.filterIsInstance<RailSystemMapItem.Line.MaxRed>().toGeoJsonDataJsonString(),
                                yellowFC = lineItems.filterIsInstance<RailSystemMapItem.Line.MaxYellow>().toGeoJsonDataJsonString(),
                                wesFC = lineItems.filterIsInstance<RailSystemMapItem.Line.WES>().toGeoJsonDataJsonString(),
                                blueGreenFC = lineItems.filterIsInstance<RailSystemMapItem.Line.MaxBlueGreen>().toGeoJsonDataJsonString(),
                                blueRedFC = lineItems.filterIsInstance<RailSystemMapItem.Line.MaxBlueRed>().toGeoJsonDataJsonString(),
                                greenOrangeFC = lineItems.filterIsInstance<RailSystemMapItem.Line.MaxGreenOrange>().toGeoJsonDataJsonString(),
                                greenYellowFC = lineItems.filterIsInstance<RailSystemMapItem.Line.MaxGreenYellow>().toGeoJsonDataJsonString(),
                                bgrFC = lineItems.filterIsInstance<RailSystemMapItem.Line.MaxBlueGreenRed>().toGeoJsonDataJsonString(),
                                bgryFC = lineItems.filterIsInstance<RailSystemMapItem.Line.MaxBlueGreenRedYellow>().toGeoJsonDataJsonString(),
                                scAFC = lineItems.filterIsInstance<RailSystemMapItem.Line.StreetcarALoop>().toGeoJsonDataJsonString(),
                                scBFC = lineItems.filterIsInstance<RailSystemMapItem.Line.StreetcarBLoop>().toGeoJsonDataJsonString(),
                                scNsFC = lineItems.filterIsInstance<RailSystemMapItem.Line.StreetcarNorthSouth>().toGeoJsonDataJsonString(),
                                scAbFC = lineItems.filterIsInstance<RailSystemMapItem.Line.StreetcarAB>().toGeoJsonDataJsonString(),
                                scNsaFC = lineItems.filterIsInstance<RailSystemMapItem.Line.StreetcarNSA>().toGeoJsonDataJsonString(),
                                scNsbFC = lineItems.filterIsInstance<RailSystemMapItem.Line.StreetcarNSB>().toGeoJsonDataJsonString(),
                                scNsabFC = lineItems.filterIsInstance<RailSystemMapItem.Line.StreetcarNSAB>().toGeoJsonDataJsonString(),
                                scMaxAbOrFC = lineItems.filterIsInstance<RailSystemMapItem.Line.StreetcarMaxABOrange>().toGeoJsonDataJsonString(),
                                basicFC = lineItems.filterIsInstance<RailSystemMapItem.Line.Basic>().toGeoJsonDataJsonString(),
                            ).also { display ->
                                withContext(Dispatchers.Main) {
                                    _railSystemMap.value = display
                                }
                            }
                        }
                    }
            }
        }
    }

    private val _stationText = MutableStateFlow("")
    val stationText = _stationText.asStateFlow()

    private val _selectedStopPosition = MutableStateFlow<LatLon?>(null)
    val selectedStopPosition = _selectedStopPosition.asStateFlow()

    fun clearSelectedStop() {
        _selectedStopPosition.value = null
        _stationText.value = ""
        _isDrawerOpen.value = false
    }

    fun onClickStop(position: LatLon) {
        _stationText.value = ""
        _selectedStopPosition.value = position
        flowArrivals(position, false)
    }

    fun onClickMaxStop(maxStop: RailSystemMapItem.Marker.Stop.MaxStop) {
        _stationText.value = maxStop.stationText ?: ""
        _selectedStopPosition.value = maxStop.position
        flowArrivals(maxStop.position, false)
    }

    fun onClickStreetcarStop(streetcarStop: RailSystemMapItem.Marker.Stop.StreetcarStop) {
        _stationText.value = streetcarStop.stationText ?: ""
        _selectedStopPosition.value = streetcarStop.position
        flowArrivals(streetcarStop.position, true)
    }

    fun onClickCommuterStop(commuterStop: RailSystemMapItem.Marker.Stop.CommuterStop) {
        _stationText.value = commuterStop.stationText ?: ""
        _selectedStopPosition.value = commuterStop.position
        flowArrivals(commuterStop.position, true)
    }

    private val _railSystemArrivals = MutableStateFlow<RailSystemArrivals>(RailSystemArrivals.Idle)
    val railSystemArrivals = _railSystemArrivals.asStateFlow()
    private var _flowRailSystemArrivalsJob: Job? = null
        set(job) {
            field?.cancel()
            if (field == null) _railSystemArrivals.value = RailSystemArrivals.Idle
            field = job
        }

    private fun flowArrivals(position: LatLon, isStreetCar: Boolean) {
        _flowRailSystemArrivalsJob = viewModelScope.launch {
            _railSystemArrivals.value = RailSystemArrivals.Loading
            withContext(Dispatchers.Default) {
                val locIds = railSystemRepository.getLocIds(position, isStreetCar)
                combine(
                    railSystemRepository.foreverGetArrivals(locIds, isStreetCar),
                    railSystemRepository.flowArrivalItems(locIds),
                    railSystemRepository.flowArrivalMarkers(locIds),
                ) { _, itemData, markerData ->

                    val mapItems = markerData.map { it.toRailSystemMapItem() }
                    RailSystemArrivals.Display(
                        details = itemData.map { it.toRailSystemArrivalItem() }.toImmutableList(),
                        arrivalBlueFeatures = GeoJsonData.JsonString(mapItems.filterIsInstance<RailSystemMapItem.Marker.Arrival.MaxBlue>().toGeoJsonString()),
                        arrivalGreenFeatures = GeoJsonData.JsonString(mapItems.filterIsInstance<RailSystemMapItem.Marker.Arrival.MaxGreen>().toGeoJsonString()),
                        arrivalOrangeFeatures = GeoJsonData.JsonString(mapItems.filterIsInstance<RailSystemMapItem.Marker.Arrival.MaxOrange>().toGeoJsonString()),
                        arrivalRedFeatures = GeoJsonData.JsonString(mapItems.filterIsInstance<RailSystemMapItem.Marker.Arrival.MaxRed>().toGeoJsonString()),
                        arrivalYellowFeatures = GeoJsonData.JsonString(mapItems.filterIsInstance<RailSystemMapItem.Marker.Arrival.MaxYellow>().toGeoJsonString()),
                        arrivalNSFeatures = GeoJsonData.JsonString(mapItems.filterIsInstance<RailSystemMapItem.Marker.Arrival.NSLine>().toGeoJsonString()),
                        arrivalALoopFeatures = GeoJsonData.JsonString(mapItems.filterIsInstance<RailSystemMapItem.Marker.Arrival.ALoop>().toGeoJsonString()),
                        arrivalBLoopFeatures = GeoJsonData.JsonString(mapItems.filterIsInstance<RailSystemMapItem.Marker.Arrival.BLoop>().toGeoJsonString()),
                        arrivalDefaultFeatures = GeoJsonData.JsonString(mapItems.filterIsInstance<RailSystemMapItem.Marker.Arrival.Default>().toGeoJsonString()),
                    )
                }.collect { display ->
                    withContext(Dispatchers.Main) {
                        _railSystemArrivals.value = display
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
            flowRailSystemMap()
        }
    }

    private val _mapViewport = MutableStateFlow<MapViewport?>(null)
    val mapViewport = _mapViewport.asStateFlow()
    fun updateMapViewport(viewport: MapViewport) {
        _mapViewport.value = viewport
        viewport.bounds?.let { bounds ->
            startFlowingMapData(
                railSystemRepository.flowRailSystemMapDataByRegion(
                    north = bounds.north,
                    south = bounds.south,
                    east = bounds.east,
                    west = bounds.west,
                )
            )
        }
    }
}
