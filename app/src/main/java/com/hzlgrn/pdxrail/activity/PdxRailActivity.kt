package com.hzlgrn.pdxrail.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.viewinterop.AndroidViewBinding
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.core.view.ViewCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.MapType
import com.hzlgrn.pdxrail.BuildConfig
import com.hzlgrn.pdxrail.Domain
import com.hzlgrn.pdxrail.R
import com.hzlgrn.pdxrail.compose.PdxRailDrawer
import com.hzlgrn.pdxrail.data.room.ApplicationRoomLoader
import com.hzlgrn.pdxrail.databinding.ActivityPdxRailBinding
import com.hzlgrn.pdxrail.dialog.HelpDialog
import com.hzlgrn.pdxrail.theme.PdxRailTheme
import com.hzlgrn.pdxrail.viewmodel.PdxRailViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class PdxRailActivity : AppCompatActivity() {
    @Inject
    lateinit var applicationRoomLoader: ApplicationRoomLoader
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
                    PdxRailActivityContent()
                }
            },
        )
    }

    override fun onStart() {
        super.onStart()
        launchLoadData() // TODO: Maybe call this AFTER the map initializes?
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

    private fun launchLoadData() {
        pdxRailViewModel.viewModelScope.launch {
            withContext(Dispatchers.IO) {
                applicationRoomLoader.load()
            }
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

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun PdxRailActivityContent() {
        val coroutineScope = rememberCoroutineScope()
        val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
        val isDrawerOpen by pdxRailViewModel.isDrawerOpen.collectAsStateWithLifecycle()
        val isMapTypeDropdownOpen by pdxRailViewModel.isMapTypeDropdownOpen.collectAsStateWithLifecycle()
        val railSystemArrivals by pdxRailViewModel.railSystemArrivals.collectAsStateWithLifecycle()
        if (isDrawerOpen) {
            // Open drawer and reset state in VM.
            LaunchedEffect(Unit) {
                // wrap in try-finally to handle interruption while opening drawer
                try {
                    drawerState.open()
                } finally {
                    pdxRailViewModel.openDrawer(false)
                }
            }
        }

        PdxRailTheme {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text(getString(R.string.app_name)) },
                        actions = {
                            IconButton(onClick = { showHelpDialog(true) }) {
                                Icon(painterResource(R.drawable.menu_help), getString(R.string.menu_help))
                            }

                            IconButton(onClick = { pdxRailViewModel.openMapTypeDropdown(!isMapTypeDropdownOpen) }) {
                                Icon(painterResource(R.drawable.menu_base_layer), getString(R.string.menu_google_map))
                            }
                            DropdownMenu(
                                expanded = isMapTypeDropdownOpen,
                                onDismissRequest = { pdxRailViewModel.openMapTypeDropdown(false) }
                            ) {
                                DropdownMenuItem(onClick = { pdxRailViewModel.commitMapType(MapType.HYBRID) }) {
                                    Text(text = getString(R.string.google_map_type_hybrid))
                                }
                                DropdownMenuItem(onClick = { pdxRailViewModel.commitMapType(MapType.SATELLITE) }) {
                                    Text(text = getString(R.string.google_map_type_satellite))
                                }
                                DropdownMenuItem(onClick = { pdxRailViewModel.commitMapType(MapType.TERRAIN) }) {
                                    Text(text = getString(R.string.google_map_type_terrain))
                                }
                                DropdownMenuItem(onClick = { pdxRailViewModel.commitMapType(MapType.NORMAL) }) {
                                    Text(text = getString(R.string.google_map_type_normal))
                                }
                            }
                        },
                        navigationIcon = {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = getString(R.string.app_name),
                                modifier = Modifier.clickable {
                                    coroutineScope.launch {
                                        if (drawerState.isOpen) {
                                            drawerState.close()
                                        } else if(drawerState.isClosed) {
                                            drawerState.open()
                                        }
                                    }
                                }
                            )
                        }
                    )
                },
            ) { innerPadding ->
                PdxRailDrawer(
                    railSystemArrivals = railSystemArrivals,
                    drawerState = drawerState,
                    onArrivalClick = {},
                    onReviewClick = {},
                    modifier = Modifier.padding(innerPadding),
                ) {
                    AndroidViewBinding(ActivityPdxRailBinding::inflate)
                }
            }
        }
    }
}