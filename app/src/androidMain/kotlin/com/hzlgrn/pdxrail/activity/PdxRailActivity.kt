package com.hzlgrn.pdxrail.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.hzlgrn.pdxrail.App
import com.hzlgrn.pdxrail.BuildConfig
import com.hzlgrn.pdxrail.viewmodel.PdxRailViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class PdxRailActivity : ComponentActivity() {

    private val pdxRailViewModel: PdxRailViewModel by viewModel()

    private val requestLocationPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) pdxRailViewModel.setIsMyLocationEnabled(true)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            App(
                onReviewClick = { onReviewClick() },
                onRequestLocationPermission = { requestAccessFineLocation(isInsisting = true) },
                onLicensesClick = { startActivity(Intent(Intent.ACTION_VIEW, "https://pdxrail.hzlgrn.com/licenses".toUri())) },
            )
        }
    }

    override fun onStart() {
        super.onStart()
        pdxRailViewModel.initHelpDialog()
        requestAccessFineLocation()
    }

    private fun onReviewClick() {
        val uri = "market://details?id=${BuildConfig.STORE_ID}".toUri()
        val goToMarket = Intent(Intent.ACTION_VIEW, uri)
        goToMarket.addFlags(
            Intent.FLAG_ACTIVITY_NO_HISTORY
                or Intent.FLAG_ACTIVITY_MULTIPLE_TASK
                or Intent.FLAG_ACTIVITY_NEW_DOCUMENT
        )
        try {
            startActivity(goToMarket)
        } catch (err: ActivityNotFoundException) {
            val fallbackUri = "http://play.google.com/store/apps/details?id=${BuildConfig.STORE_ID}".toUri()
            startActivity(Intent(Intent.ACTION_VIEW, fallbackUri))
        }
    }

    @SuppressLint("MissingPermission")
    private fun onRequestForFineLocationGranted(): Boolean {
        val granted = PackageManager.PERMISSION_GRANTED ==
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        if (granted) {
            pdxRailViewModel.setIsMyLocationEnabled(true)
        }
        return granted
    }

    private fun requestAccessFineLocation(isInsisting: Boolean = false) {
        if (onRequestForFineLocationGranted()) return
        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        val hasAsked = prefs.getBoolean(PREF_LOCATION_ASKED, false)
        val shouldShowRationale = ActivityCompat.shouldShowRequestPermissionRationale(
            this, Manifest.permission.ACCESS_FINE_LOCATION
        )
        when {
            !hasAsked || shouldShowRationale -> {
                prefs.edit().putBoolean(PREF_LOCATION_ASKED, true).apply()
                requestLocationPermission.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
            isInsisting -> {
                // Permanently denied so send user to app settings
                startActivity(
                    Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.fromParts("package", packageName, null)
                    }
                )
            }
        }
    }

    companion object {
        private const val PREFS_NAME = "pdxrail"
        private const val PREF_LOCATION_ASKED = "location_permission_asked"
    }
}
