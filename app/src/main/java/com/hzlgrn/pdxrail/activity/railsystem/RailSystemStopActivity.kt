package com.hzlgrn.pdxrail.activity.railsystem

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.hzlgrn.pdxrail.App
import com.hzlgrn.pdxrail.Domain
import com.hzlgrn.pdxrail.R
import com.hzlgrn.pdxrail.adapter.ArrivalsRecyclerViewAdapter
import com.hzlgrn.pdxrail.data.repository.ArrivalRepository
import com.hzlgrn.pdxrail.data.repository.viewmodel.UniqueIdModel
import com.hzlgrn.pdxrail.databinding.DrawerArrivalsBinding
import com.hzlgrn.pdxrail.task.TaskWsV1Stops
import com.hzlgrn.pdxrail.task.TaskWsV2Arrivals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

@SuppressLint("Registered")
abstract class RailSystemStopActivity: RailSystemActivity() {

    @Inject
    lateinit var arrivalRepository: ArrivalRepository

    protected abstract val pDrawerBinding: DrawerArrivalsBinding

    private val arrivalsAdapter: ArrivalsRecyclerViewAdapter by lazy {
        ArrivalsRecyclerViewAdapter(coroutineContext) { uniqueId ->
            arrivalRepository.collectArrivalItemViewModel(uniqueId)
        }
    }

    protected open var pFocusStopUniqueId: String? = null

    private var mFocusStopPosition: LatLng? = null

    private val mArrivalMarkers = ArrayList<Marker>()

    private var observeArrivalsJob: Job? = null
        set(job) {
            if (field?.isActive == true) field?.cancel()
            field = job
        }
    private var observeArrivalItems: Job? = null
        set(job) {
            if (field?.isActive == true) field?.cancel()
            field = job
        }
    private var observeArrivalMarkers: Job? = null
        set(job) {
            if (field?.isActive == true) field?.cancel()
            field = job
        }
    private var updateArrivalDataJob: Job? = null
        set(job) {
            if (field?.isActive == true) field?.cancel()
            field = job
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.applicationComponent.inject(this)
    }

    override fun onStart() {
        super.onStart()
        clickedMarker = clickedMarker
        pDrawerBinding.drawerStartListviewArrivals.adapter = arrivalsAdapter
        arrivalsAdapter.onClick = onArrivalItemClicked
    }

    override fun onStop() {
        super.onStop()
        updateArrivalDataJob = null
        observeArrivalsJob = null
        observeArrivalItems = null
        observeArrivalMarkers = null
        pDrawerBinding.drawerStartListviewArrivals.adapter = null
        arrivalsAdapter.onClick = { _ -> }
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
                pDrawerBinding.textLatLon.text = resources.getString(
                    R.string.lat_lon,
                    "%.4f".format(position.latitude),
                    "%.4f".format(position.longitude))
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
            Timber.d("onMarkerClick")
            this.clickedMarker = clickedMarker
            false
        }
        pGoogleMap?.setOnMapClickListener {
            Timber.d("onMapClick")
            updateArrivalDataJob = null
            observeArrivalItems = null
            observeArrivalMarkers = null
            for (marker in mArrivalMarkers) marker.remove()
            mArrivalMarkers.clear()
            pFocusStopUniqueId = null
            clickedMarker = null
            arrivalsAdapter.setData(emptyList())
        }
    }

    private fun fetchArrivalsFor(position: LatLng, isStreetcar: Boolean = false) {
        Timber.d("fetchArrivalsFor($position, $isStreetcar")
        updateArrivalDataJob = null
        observeArrivalItems = null
        observeArrivalMarkers = null
        if(mFocusStopPosition != position) {
            mFocusStopPosition = position
        }
        onArrivalMarkersViewModel()
        onArrivalListViewModel()
        observeArrivalsJob = launch(Dispatchers.IO) {
            TaskWsV1Stops().flowLocid(position, isStreetcar).collect { locid ->
                onLocationIdUpdated(locid.toLongArray(), isStreetcar)
            }
        }
    }

    private fun onLocationIdUpdated(locid: LongArray, isStreetcar: Boolean) {
        Timber.d("onLocationIdUpdated($locid, $isStreetcar)")
        updateArrivalDataJob = TaskWsV2Arrivals().launchJob(locid, isStreetcar)
        observeArrivalMarkers = launch {
            arrivalRepository.arrivalMarkersViewModel(locid.toList()).collect {
                onArrivalMarkersViewModel(it)
            }
        }
        observeArrivalItems = launch {
            arrivalRepository.arrivalItemsViewModel(locid.toList()).collect {
                onArrivalListViewModel(it)
            }
        }
    }

    private fun onArrivalMarkersViewModel(models: List<MarkerOptions> = emptyList()) {
        Timber.d("onArrivalMarkersViewModel(${models.size})")
        for (marker in mArrivalMarkers) marker.remove()
        mArrivalMarkers.clear()
        Timber.d(if (models.isEmpty()) "clear arrival markers" else "placing ${models.size} arrival markers")
        for (model in models) {
            try {
                pGoogleMap?.addMarker(model)?.also { mArrivalMarkers.add(it) }
            } catch (err: Throwable) {
                Timber.e(err)
            }
        }
    }

    private fun onArrivalListViewModel(models: List<UniqueIdModel> = emptyList()) {
        Timber.d("onArrivalListViewModel(${models.size})")
        arrivalsAdapter.setData(models)
    }

    private val onArrivalItemClicked by lazy {
        fun(latlng: LatLng) {
            moveMapTo(latlng)
        }
    }

    protected fun moveMapTo(position: LatLng) {
        var didAnimateCamera = false
        if (position.latitude > Domain.RailSystem.REGION_RECT_SE.latitude
            && position.longitude < Domain.RailSystem.REGION_RECT_SE.longitude
            && position.latitude < Domain.RailSystem.REGION_RECT_NW.latitude
            && position.longitude > Domain.RailSystem.REGION_RECT_NW.longitude) {
            pGoogleMap?.animateCamera(CameraUpdateFactory.newLatLng(position))
            didAnimateCamera = true
        }
        if (!didAnimateCamera) mFocusStopPosition?.let {
            pGoogleMap?.animateCamera(CameraUpdateFactory.newLatLng(it))
        }
    }

}