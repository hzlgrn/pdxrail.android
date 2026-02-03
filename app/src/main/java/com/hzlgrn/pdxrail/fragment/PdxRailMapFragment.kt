package com.hzlgrn.pdxrail.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Switch
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState
import com.hzlgrn.pdxrail.Domain
import com.hzlgrn.pdxrail.compose.DisplayGoogleMapLine
import com.hzlgrn.pdxrail.compose.DisplayGoogleMapMarker
import com.hzlgrn.pdxrail.data.repository.viewmodel.RailSystemMapItem
import com.hzlgrn.pdxrail.theme.PdxRailTheme
import com.hzlgrn.pdxrail.viewmodel.PdxRailViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PdxRailMapFragment : Fragment() {

    private val pdxRailViewModel: PdxRailViewModel by activityViewModels()

    override fun onResume() {
        super.onResume()
        pdxRailViewModel.flowRailSystemMap()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = ComposeView(inflater.context).apply {
        layoutParams = LayoutParams(MATCH_PARENT, MATCH_PARENT)
        setContent {
            PdxRailTheme {

                // PdxRailMapScreen()

                val railSystemMap by pdxRailViewModel.railSystemMap.collectAsStateWithLifecycle()
                Box(modifier = Modifier.fillMaxSize()) {
                    when (railSystemMap) {
                        is PdxRailViewModel.RailSystemMapState.Idle -> {

                        }
                        is PdxRailViewModel.RailSystemMapState.Loading -> {

                        }
                        is PdxRailViewModel.RailSystemMapState.Display -> {
                            val mapItems = (railSystemMap as PdxRailViewModel.RailSystemMapState.Display).mapItems
                            val mapCameraPositionState = rememberCameraPositionState {
                                position = CameraPosition.fromLatLngZoom(
                                    Domain.PdxRail.CAMERA_TARGET,
                                    Domain.PdxRail.CAMERA_ZOOM
                                )
                            }
                            var mapUiSettings by remember { mutableStateOf(MapUiSettings()) }
                            var mapProperties by remember {
                                mutableStateOf(MapProperties(mapType = MapType.NORMAL))
                            }
                            GoogleMap(
                                modifier = Modifier.matchParentSize(),
                                cameraPositionState = mapCameraPositionState,
                                uiSettings = mapUiSettings,
                                properties = mapProperties
                            ) {
                                mapItems.forEach { mapItem ->
                                    when (mapItem) {
                                        is RailSystemMapItem.Marker ->
                                            mapItem.DisplayGoogleMapMarker()
                                        is RailSystemMapItem.Line ->
                                            mapItem.DisplayGoogleMapLine()
                                    }
                                }
                            }
                            Switch(
                                checked = mapUiSettings.zoomControlsEnabled,
                                onCheckedChange = {
                                    mapUiSettings = mapUiSettings.copy(zoomControlsEnabled = it)
                                },
                                modifier = Modifier.align(Alignment.BottomEnd).padding(bottom = 32.dp, end = 64.dp)
                            )
                        }
                    }
                }

            }
        }
    }
}