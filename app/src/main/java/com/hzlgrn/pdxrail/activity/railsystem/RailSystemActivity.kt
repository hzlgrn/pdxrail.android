package com.hzlgrn.pdxrail.activity.railsystem

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.core.content.res.ResourcesCompat
import com.google.android.gms.maps.model.*
import com.hzlgrn.pdxrail.R
import com.hzlgrn.pdxrail.activity.common.MapTypeMenuActivity
import com.hzlgrn.pdxrail.data.repository.RailSystemRepository
import com.hzlgrn.pdxrail.data.repository.viewmodel.RailSystemMapViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*
import javax.inject.Inject

@SuppressLint("Registered")
abstract class RailSystemActivity : MapTypeMenuActivity() {

    @Inject
    lateinit var railSystemRepository: RailSystemRepository

    override fun onInfoWindowClick(marker: Marker?) {}

    fun selectStop(uniqueId: String) {
        Timber.d("fetchArrivalsFor($uniqueId)")
        val marker = (stopMarkerMax.firstOrNull {
            val tag = it.tag
            tag != null && tag.toString() == uniqueId
        } ?: stopMarkerStreetcar.firstOrNull {
            val tag = it.tag
            tag != null && tag.toString() == uniqueId
        })
        marker?.showInfoWindow()
    }

    fun getStopMarkerFor(uidStop: String?): Marker? {
        var stopMarker: Marker? = null

        for (marker in stopMarkerMax) {
            val tag = marker.tag
            if (tag != null && tag.toString() == uidStop) stopMarker = marker
            if (stopMarker != null) break
        }
        for (marker in stopMarkerStreetcar) {
            if (stopMarker != null) break
            val tag = marker.tag
            if (tag != null && tag.toString() == uidStop) stopMarker = marker
        }

        return stopMarker
    }

    protected fun isMaxStopMarker(marker: Marker) = stopMarkerMax.contains(marker)
    protected fun isStreetCarStopMarker(marker: Marker) = stopMarkerStreetcar.contains(marker)

    private val stopMarkerMax = mutableListOf<Marker>()
    private val stopMarkerStreetcar = mutableListOf<Marker>()
    private val polylines = mutableListOf<Polyline>()

    private val colorMaxBlueLine by lazy(false) { ResourcesCompat.getColor(resources, R.color.max_blue_line,theme) }
    private val colorMaxGreenLine by lazy(false) { ResourcesCompat.getColor(resources,R.color.max_green_line,theme) }
    private val colorMaxOrangeLine by lazy(false) { ResourcesCompat.getColor(resources,R.color.max_orange_line,theme) }
    private val colorMaxRedLine by lazy(false) { ResourcesCompat.getColor(resources,R.color.max_red_line,theme) }
    private val colorMaxYellowLine by lazy(false) { ResourcesCompat.getColor(resources,R.color.max_yellow_line,theme) }
    private val colorWesCommuterRail by lazy(false) { ResourcesCompat.getColor(resources,R.color.wes_commuter_rail,theme) }
    private val colorStreetcarALoop by lazy(false) { ResourcesCompat.getColor(resources,R.color.portland_streetcar_a_loop,theme) }
    private val colorStreetcarBLoop by lazy(false) { ResourcesCompat.getColor(resources,R.color.portland_streetcar_b_loop,theme) }
    private val colorStreetcarNorthSouth by lazy(false) { ResourcesCompat.getColor(resources,R.color.portland_streetcar_north_south_line,theme) }

    private val maxLineDp by lazy { resources.getDimension(R.dimen.max_line_width) }
    private val streetcarLineDp by lazy { resources.getDimension(R.dimen.streetcar_line_width) }

    private val lPattern1o2 = listOf(Dash(60f),Gap(60f))
    private val lPattern2o2 = listOf(Gap(60f),Dash(60f))
    private val lPattern1o3 = listOf(Dash(60f),Gap(120f))
    private val lPattern2o3 = listOf(Gap(60f),Dash(60f),Gap(60f))
    private val lPattern3o3 = listOf(Gap(120f),Dash(60f))
    private val lPattern1o4 = listOf(Dash(60f),Gap(180f))
    private val lPattern2o4 = listOf(Gap(60f),Dash(60f),Gap(120f))
    private val lPattern3o4 = listOf(Gap(120f),Dash(60f),Gap(60f))
    private val lPattern4o4 = listOf(Gap(180f),Dash(60f))

    private var collectRailStopMapViewModel: Job? = null
        set(job) {
            if (field?.isCancelled == false) field?.cancel()
            field = job
        }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        configMap(newConfig)
    }

    override fun onMapReady() {
        super.onMapReady()
        configMap(resources.configuration)
        collectRailStopMapViewModel = launch {
            railSystemRepository.mapViewModel().collect {
                onUpdateRailSystemMapViewModel(it)
            }
        }
    }

    private fun configMap(newConfig: Configuration) {
        val mapMode = when (newConfig.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> R.raw.google_map_dark
            else -> R.raw.google_map_silver
        }
        pGoogleMap?.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, mapMode))
    }

    private fun onUpdateRailSystemMapViewModel(viewModel: RailSystemMapViewModel) {
        renderTMRailLines(viewModel)
        renderTMRailStops(viewModel)
    }

    private fun renderTMRailLines(viewModel: RailSystemMapViewModel) {
        polylines.forEach { it.remove() }
        polylines.clear()
        for (railLineViewModel in viewModel.railLines) {
            val polyline = railLineViewModel.polyline
            when(railLineViewModel.line) {
                "B" ->
                    renderLine(maxLineDp,colorMaxBlueLine,polyline)
                "G" ->
                    renderLine(maxLineDp,colorMaxGreenLine,polyline)
                "O" ->
                    renderLine(maxLineDp,colorMaxOrangeLine,polyline)
                "R" ->
                    renderLine(maxLineDp,colorMaxRedLine,polyline)
                "Y" ->
                    renderLine(maxLineDp,colorMaxYellowLine,polyline)
                "BG" -> {
                    renderLine(maxLineDp,colorMaxBlueLine,polyline,lPattern1o2)
                    renderLine(maxLineDp,colorMaxGreenLine,polyline,lPattern2o2)
                }
                "BR" -> {
                    renderLine(maxLineDp,colorMaxBlueLine,polyline,lPattern1o2)
                    renderLine(maxLineDp,colorMaxRedLine,polyline,lPattern2o2)
                }
                "GO" -> {
                    renderLine(maxLineDp,colorMaxGreenLine,polyline,lPattern1o2)
                    renderLine(maxLineDp,colorMaxOrangeLine,polyline,lPattern2o2)
                }
                "GY" -> {
                    renderLine(maxLineDp,colorMaxGreenLine,polyline,lPattern1o2)
                    renderLine(maxLineDp,colorMaxYellowLine,polyline,lPattern2o2)
                }
                "BGR" -> {
                    renderLine(maxLineDp,colorMaxBlueLine,polyline,lPattern1o3)
                    renderLine(maxLineDp,colorMaxGreenLine,polyline,lPattern2o3)
                    renderLine(maxLineDp,colorMaxRedLine,polyline,lPattern3o3)
                }
                "BGRY" -> {
                    renderLine(maxLineDp,colorMaxBlueLine,polyline,lPattern1o4)
                    renderLine(maxLineDp,colorMaxGreenLine,polyline,lPattern2o4)
                    renderLine(maxLineDp,colorMaxRedLine,polyline,lPattern3o4)
                    renderLine(maxLineDp,colorMaxYellowLine,polyline,lPattern4o4)
                }
                "WES" ->
                    renderLine(maxLineDp,colorWesCommuterRail,polyline)
                "AL" ->
                    renderLine(streetcarLineDp, colorStreetcarALoop, polyline)
                "BL" ->
                    renderLine(streetcarLineDp, colorStreetcarBLoop, polyline)
                "NS" ->
                    renderLine(streetcarLineDp, colorStreetcarNorthSouth, polyline)
                "AL/BL" -> {
                    renderLine(streetcarLineDp,colorStreetcarALoop,polyline,lPattern1o2)
                    renderLine(streetcarLineDp,colorStreetcarBLoop,polyline,lPattern2o2)
                }
                "NS/BL" -> {
                    renderLine(streetcarLineDp,colorStreetcarNorthSouth,polyline,lPattern1o2)
                    renderLine(streetcarLineDp,colorStreetcarBLoop,polyline,lPattern2o2)
                }
                "NS/AL" -> {
                    renderLine(streetcarLineDp,colorStreetcarNorthSouth,polyline,lPattern1o2)
                    renderLine(streetcarLineDp,colorStreetcarALoop,polyline,lPattern2o2)
                }
                "O/AL/BL" -> {
                    renderLine(maxLineDp,colorMaxOrangeLine,polyline,lPattern1o3)
                    renderLine(streetcarLineDp,colorStreetcarALoop,polyline,lPattern2o3)
                    renderLine(streetcarLineDp,colorStreetcarBLoop,polyline,lPattern3o3)
                }
                "NS/AL/BL" -> {
                    renderLine(streetcarLineDp,colorStreetcarNorthSouth,polyline,lPattern1o3)
                    renderLine(streetcarLineDp,colorStreetcarALoop,polyline,lPattern2o3)
                    renderLine(streetcarLineDp,colorStreetcarBLoop,polyline,lPattern3o3)
                }
                else -> Timber.e("Line type not recognized: ${railLineViewModel.line}")
            }
        }
    }
    private fun renderLine(
                lineWidth: Float,
                color_code: Int,
                lineString: ArrayList<LatLng>,
                pattern: List<PatternItem>? = null) {
        val polylineOptions = PolylineOptions().apply {
            startCap(RoundCap())
            endCap(RoundCap())
            jointType(JointType.ROUND)
            width(lineWidth)
            color(color_code)
            addAll(lineString)
            pattern?.let { pattern(it) }
        }
        pGoogleMap?.addPolyline(polylineOptions)?.let {
            polylines.add(it)
        }
    }
    private fun renderTMRailStops(viewModel: RailSystemMapViewModel) {
        stopMarkerMax.forEach { it.remove() }
        stopMarkerMax.clear()
        stopMarkerStreetcar.forEach { it.remove() }
        stopMarkerStreetcar.clear()
        for (stopModel in viewModel.railStops) {
            when (stopModel.type) {
                "MAX", "CR" -> renderMaxStop(stopModel)
                "SC" -> renderStreetcarStop(stopModel)
                else -> {
                    Timber.e("Stop type no recognized: ${stopModel.type}")
                    renderMaxStop(stopModel)
                }
            }
        }
    }
    private fun renderMaxStop(stopModel: RailSystemMapViewModel.RailStopMapViewModel) {
        pGoogleMap?.addMarker(MarkerOptions().apply {
            anchor(0.5f, 0.5f)
            icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_max_stop))
            flat(true)
            infoWindowAnchor(0.5f, 0.5f)
            position(stopModel.position)
            if (!stopModel.station.isNullOrEmpty()) title(stopModel.station)
        })?.let { marker ->
            marker.tag = stopModel.uniqueid
            stopMarkerMax.add(marker)
        }
    }
    private fun renderStreetcarStop(stopModel: RailSystemMapViewModel.RailStopMapViewModel) {
        pGoogleMap?.addMarker(MarkerOptions().apply {
            anchor(0.5f, 0.5f)
            icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_streetcar_stop))
            flat(true)
            infoWindowAnchor(0.5f, 0.5f)
            position(stopModel.position)
            if (!stopModel.station.isNullOrEmpty()) title(stopModel.station)
        })?.let { marker ->
            marker.tag = stopModel.uniqueid
            stopMarkerStreetcar.add(marker)
        }
    }

}