package com.hzlgrn.pdxrail.activity.railsystem

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.hzlgrn.pdxrail.App
import com.hzlgrn.pdxrail.activity.BuildConfig
import com.hzlgrn.pdxrail.activity.databinding.TrimetStopDrawerBinding
import com.hzlgrn.pdxrail.adapter.ArrivalModelArrayAdapter
import com.hzlgrn.pdxrail.data.repository.RailSystemArrivalRepository
import com.hzlgrn.pdxrail.data.repository.viewmodel.ArrivalItemViewModel
import com.hzlgrn.pdxrail.task.TaskTrimetWsV1Stops
import com.hzlgrn.pdxrail.task.TaskTrimetWsV2Arrivals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*
import javax.inject.Inject

@SuppressLint("Registered")
abstract class RailSystemStopActivity: RailSystemActivity() {

    protected abstract val pDrawerBinding: TrimetStopDrawerBinding

    @Inject
    lateinit var railSystemArrivalRepository: RailSystemArrivalRepository

    protected open var pFocusStopUniqueId: String? = null

    private val mArrivalMarkers = ArrayList<Marker>()
    private var mFocusStopPosition: LatLng? = null

    private var observeArrivalsJob: Job? = null
        set(job) {
            if (field?.isCancelled == false) field?.cancel()
            field = job
        }
    private var updateArrivalDataJob: Job? = null
        set(job) {
            if (field?.isCancelled == false) field?.cancel()
            field = job
        }
    private var observeArrivalItems: Job? = null
        set(job) {
            if (field?.isCancelled == false) field?.cancel()
            field = job
        }
    private var observeArrivalMarkers: Job? = null
        set(job) {
            if (field?.isCancelled == false) field?.cancel()
            field = job
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.applicationComponent.inject(this)
    }

    override fun onStart() {
        super.onStart()
        clickedMarker = clickedMarker
    }

    override fun onStop() {
        super.onStop()
        updateArrivalDataJob = null
        observeArrivalsJob = null
        observeArrivalItems = null
        observeArrivalMarkers = null
    }

    private var clickedMarker: Marker? = null
        set(marker) {
            marker?.let {
                val isMax = isMaxStopMarker(marker)
                val isStreetCar = isStreetCarStopMarker(marker)
                val isStopMarkerClicked = isMax || isStreetCar
                if(isStopMarkerClicked) {
                    marker.tag?.let { tag ->
                        pFocusStopUniqueId = tag.toString()
                    }
                    fetchArrivalsFor(marker.position, isStreetCar)
                }
                pDrawerBinding.textDesc.text = it.title
                val position = it.position
                pDrawerBinding.textLatLon.text = "${"%.4f".format(position.latitude)}, ${"%.4f".format(position.longitude)}"
                pDrawerBinding.dividerTop.visibility = View.VISIBLE
                pDrawerBinding.cardStop.visibility = View.VISIBLE
                pDrawerBinding.cardStop.setOnClickListener {
                    pGoogleMap?.animateCamera(CameraUpdateFactory.newLatLng(position))
                }
            }.also {
                if (it == null) {
                    pDrawerBinding.dividerTop.visibility = View.GONE
                    pDrawerBinding.cardStop.visibility = View.GONE
                }
            }
            field = marker
        }

    @SuppressLint("PotentialBehaviorOverride")
    override fun onMapReady() {
        super.onMapReady()
        pGoogleMap?.setOnMarkerClickListener { clickedMarker ->
            Timber.d("I clicked a marker?")
            this.clickedMarker = clickedMarker
            false
        }
        pGoogleMap?.setOnMapClickListener {
            updateArrivalDataJob = null
            observeArrivalItems = null
            observeArrivalMarkers = null
            for (marker in mArrivalMarkers) marker.remove()
            mArrivalMarkers.clear()
            pFocusStopUniqueId = null
            clickedMarker = null
            pDrawerBinding.drawerStartListviewArrivals.adapter = ArrivalModelArrayAdapter(this, emptyList())
        }
        pDrawerBinding.drawerStartListviewArrivals.divider = null
        pDrawerBinding.drawerStartListviewArrivals.adapter = ArrivalModelArrayAdapter(this, emptyList())
    }

    private fun fetchArrivalsFor(position: LatLng, isStreetcar: Boolean = false) {
        Timber.d("position=$position, isStreetCar=$isStreetcar")
        updateArrivalDataJob = null
        observeArrivalItems = null
        observeArrivalMarkers = null
        if(mFocusStopPosition != position) {
            mFocusStopPosition = position
        }
        onArrivalMarkersViewModel()
        onArrivalListViewModel()
        observeArrivalsJob = launch(Dispatchers.IO) {
            TaskTrimetWsV1Stops().flowLocid(position, isStreetcar).collect { locid ->
                onLocationIdUpdated(locid, isStreetcar)
            }
        }
    }

    private fun onLocationIdUpdated(locid: List<Long>, isStreetcar: Boolean) {
        Timber.d("onLocationUpdated()")
        updateArrivalDataJob = TaskTrimetWsV2Arrivals().launchJob(locid, isStreetcar)
        observeArrivalMarkers = launch {
            railSystemArrivalRepository.arrivalMarkersViewModel(locid).collect {
                onArrivalMarkersViewModel(it)
            }
        }
        observeArrivalItems = launch {
            railSystemArrivalRepository.arrivalItemsViewModel(locid).collect {
                onArrivalListViewModel(it)
            }
        }
    }

    private fun onArrivalMarkersViewModel(models: List<MarkerOptions> = emptyList()) {
        Timber.d("onArrivalMapViewModel()")
        for (marker in mArrivalMarkers) marker.remove()
        mArrivalMarkers.clear()
        Timber.d(if (models.isEmpty()) "clear arrival markers" else "placing ${models.size} arrival markers")
        for (model in models) {
            try {
                pGoogleMap?.addMarker(model)?.also { mArrivalMarkers.add(it) }
            } catch( err: Exception ) {
                if (BuildConfig.DEBUG) err.printStackTrace()
            }
        }
    }

    private fun onArrivalListViewModel(models: List<ArrivalItemViewModel> = emptyList()) {
        Timber.d("onArrivalListViewModel()")
        pDrawerBinding.drawerStartListviewArrivals.adapter = ArrivalModelArrayAdapter(this, models).apply {
            onItemClickCallback = onArrivalItemClicked
        }
    }

    private val onArrivalItemClicked by lazy {
        fun(arrival: ArrivalItemViewModel) {
            var didAnimateCamera = false
            if (arrival.latlng.latitude != 0.0 && arrival.latlng.longitude != 0.0) {
                pGoogleMap?.animateCamera(CameraUpdateFactory.newLatLng(arrival.latlng))
                didAnimateCamera = true
            }
            if (!didAnimateCamera) mFocusStopPosition?.let {
                pGoogleMap?.animateCamera(
                        CameraUpdateFactory.newLatLng(arrival.latlng))
            }
        }
    }

}