package com.hzlgrn.pdxrail.activity.common

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.OnMapReadyCallback
import com.hzlgrn.pdxrail.Domain
import com.hzlgrn.pdxrail.dialog.HelpDialog
import timber.log.Timber

@SuppressLint("Registered")
abstract class GoogleMapViewActivity : ApplicationActivity(), GoogleMap.OnInfoWindowClickListener {

    protected abstract val pMapView: MapView

    protected var pGoogleMap: GoogleMap? = null
        set(googleMap) {
            field = googleMap
            field?.let { onMapReady() }
        }

    @SuppressLint("PotentialBehaviorOverride")
    protected open fun onMapReady() {
        pGoogleMap?.setOnInfoWindowClickListener(this)
    }

    private var mSavedInstanceState: Bundle? = null
    private val onMapReadyCallback by lazy {
        OnMapReadyCallback { googleMap -> googleMap.let {
            pGoogleMap = it
            showHelpDialog(noMatterWhat = false,shouldCheckPermissionAfter = true)
        } }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mSavedInstanceState = savedInstanceState
    }

    override fun setContentView(view: View?) {
        super.setContentView(view)
        pMapView.onCreate(mSavedInstanceState)
        pMapView.onResume()
        try {
            MapsInitializer.initialize(applicationContext)
        } finally { Timber.d("MapsInitializer.initialize") }
        pMapView.getMapAsync(onMapReadyCallback)
    }

    override fun onResume() {
        super.onResume()
        pMapView.onResume()
    }
    override fun onPause() {
        super.onPause()
        pMapView.onPause()
    }
    override fun onDestroy() {
        super.onDestroy()
        pMapView.onDestroy()
    }
    override fun onLowMemory() {
        super.onLowMemory()
        pMapView.onLowMemory()
    }
    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<String>,
            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_REQUEST_ACCESS_FINE_LOCATION -> {
                grantResults.firstOrNull { it == PackageManager.PERMISSION_GRANTED }?.let {
                    onRequestForFineLocationGranted()
                }
            }
        }
    }

    fun showHelpDialog(noMatterWhat: Boolean = false, shouldCheckPermissionAfter: Boolean = false) {
        val dialogHelpPermission by lazy(false) { HelpDialog() }
        val hasDialogShown = applicationPreferences.getBoolean(Domain.App.PREFERENCE.DIALOG_HELP_PERMISSION.type, false)
        if (!hasDialogShown || noMatterWhat) {
            if (!hasDialogShown) {
                applicationPreferences.edit().apply {
                    putBoolean(Domain.App.PREFERENCE.DIALOG_HELP_PERMISSION.type, true)
                    apply()
                }
            }
            dialogHelpPermission.onDismissListener = {
                supportFragmentManager.beginTransaction().apply {
                    remove(dialogHelpPermission)
                    commit()
                }
                if (shouldCheckPermissionAfter) requestAccessFineLocation()
            }
            dialogHelpPermission.show(supportFragmentManager, Domain.App.PREFERENCE.DIALOG_HELP_PERMISSION.type)
        } else if (shouldCheckPermissionAfter) {
            requestAccessFineLocation()
        }
    }

    @SuppressLint("MissingPermission")
    private fun onRequestForFineLocationGranted(): Boolean {
        return (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED).also { granted ->
            if (granted) pGoogleMap?.isMyLocationEnabled = true
        }
    }
    private fun requestAccessFineLocation() {
        if (!onRequestForFineLocationGranted()
                && !ActivityCompat.shouldShowRequestPermissionRationale(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION)) {
            ActivityCompat.requestPermissions(
                    this,
                    Array(1) { Manifest.permission.ACCESS_FINE_LOCATION },
                    PERMISSION_REQUEST_ACCESS_FINE_LOCATION
            )
        }
    }

    companion object {
        private const val PERMISSION_REQUEST_ACCESS_FINE_LOCATION = 31664
    }
}