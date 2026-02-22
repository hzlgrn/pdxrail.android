package com.hzlgrn.pdxrail.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.ui.platform.ComposeView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import com.google.android.gms.maps.model.LatLng
import com.hzlgrn.pdxrail.BuildConfig
import com.hzlgrn.pdxrail.Domain
import com.hzlgrn.pdxrail.compose.pdxrail.PdxRailActivityAction
import com.hzlgrn.pdxrail.compose.pdxrail.PdxRailActivityScreen
import com.hzlgrn.pdxrail.dialog.HelpDialog
import com.hzlgrn.pdxrail.viewmodel.PdxRailViewModel
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class PdxRailActivity : AppCompatActivity() {
    @Inject
    lateinit var applicationPreferences: SharedPreferences
    private val pdxRailViewModel: PdxRailViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        ViewCompat.setOnApplyWindowInsetsListener(window.decorView) { _, insets -> insets }
        setContentView(
            ComposeView(this).apply {
                consumeWindowInsets = false
                setContent {
                    PdxRailActivityScreen(
                        pdxRailActivityAction = PdxRailActivityAction(
                            showHelpDialog = { showHelpDialog(true) },
                            onReviewClick = { onReviewClick() }
                        ),
                        pdxRailViewModel = pdxRailViewModel,
                    )
                }
            },
        )
    }

    override fun onStart() {
        super.onStart()
        showHelpDialog()
    }


    fun showHelpDialog(noMatterWhat: Boolean = false, shouldCheckPermissionAfter: Boolean = false) {
        val hasDialogShown = applicationPreferences
                .getBoolean(Domain.App.PREFERENCE.DIALOG_HELP_PERMISSION.type, false)
        if (!hasDialogShown || noMatterWhat) {
            if (!hasDialogShown) {
                applicationPreferences.edit {
                    putBoolean(Domain.App.PREFERENCE.DIALOG_HELP_PERMISSION.type, true)
                    apply()
                }
            }
            with (HelpDialog()) {
                onDismissListener = {
                    supportFragmentManager.beginTransaction().remove(this).commit()
                    if (shouldCheckPermissionAfter) requestAccessFineLocation()
                }
                show(supportFragmentManager, Domain.App.PREFERENCE.DIALOG_HELP_PERMISSION.type)
            }
        } else if (shouldCheckPermissionAfter) {
            requestAccessFineLocation()
        }
    }

    fun onReviewClick() {
        val uri = "market://details?id=${packageName}".toUri()
        val goToMarket = Intent(Intent.ACTION_VIEW, uri)
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY
                or Intent.FLAG_ACTIVITY_MULTIPLE_TASK
                or Intent.FLAG_ACTIVITY_NEW_DOCUMENT)
        try {
            startActivity(goToMarket)
        } catch (err: ActivityNotFoundException) {
            val fallbackUri = "http://play.google.com/store/apps/details?id=${packageName}".toUri()
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

    private fun requestAccessFineLocation() {
        val shouldRequest = !onRequestForFineLocationGranted()
                && !ActivityCompat
            .shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)

        if (shouldRequest) {
            ActivityCompat.requestPermissions(
                this,
                Array(1) { Manifest.permission.ACCESS_FINE_LOCATION },
                PERMISSION_REQUEST_ACCESS_FINE_LOCATION
            )
        }
    }

    /***
     * Handling of deep links, this is legacy code copied over and not yet re-implemented.
     */
    private fun handleActionViewIntent(data: String): Boolean {
        return when {
            data.contains(BuildConfig.HOME_HOST) -> {
                true
            }

            //--- https://developer.android.com/guide/components/intents-common
            data.contains(Domain.Intent.GEO) -> {
                handleActionViewGeo(data)
                true
            }
            else -> false
        }
    }
    private fun handleActionViewGeo(data: String) {
        try {
            val args = if (data.contains("?")) data.substringAfter("?") else ""
            val strLatLng = data.removePrefix(Domain.Intent.GEO).removeSuffix("?$args").split(",")
            val lat = strLatLng[1].toDouble()
            val lon = strLatLng[0].toDouble()
            val geoPosition = LatLng(lat, lon)
            // TODO: moveMapTo(geoPosition)
            Timber.d("lat($lat), lon($lon), args($args)")
        } catch (err: Throwable) {
            Timber.e(err)
        }
    }

    companion object {
        private const val PERMISSION_REQUEST_ACCESS_FINE_LOCATION = 31664
    }
}