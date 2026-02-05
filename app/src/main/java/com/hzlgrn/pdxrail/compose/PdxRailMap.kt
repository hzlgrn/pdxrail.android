package com.hzlgrn.pdxrail.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.dimensionResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState
import com.hzlgrn.pdxrail.Domain
import com.hzlgrn.pdxrail.R
import com.hzlgrn.pdxrail.viewmodel.PdxRailViewModel
import com.hzlgrn.pdxrail.viewmodel.bitmap.MapIconBitmap
import com.hzlgrn.pdxrail.viewmodel.railsystem.RailSystemArrivals
import com.hzlgrn.pdxrail.viewmodel.railsystem.RailSystemMapItem
import com.hzlgrn.pdxrail.viewmodel.railsystem.RailSystemMapState

@Composable
fun PdxRailMap(pdxRailViewModel: PdxRailViewModel) {
    val railSystemMap by pdxRailViewModel.railSystemMap.collectAsStateWithLifecycle()
    val railSystemArrivals by pdxRailViewModel.railSystemArrivals.collectAsStateWithLifecycle()
    val mapDrawerIcon by pdxRailViewModel.mapDrawerIcon.collectAsStateWithLifecycle()
    Box(modifier = Modifier.fillMaxSize()) {
        when (railSystemMap) {
            is RailSystemMapState.Idle -> {

            }
            is RailSystemMapState.Loading -> {

            }
            is RailSystemMapState.Display -> {
                val mapItems = (railSystemMap as RailSystemMapState.Display).mapItems
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
                                mapItem.DisplayGoogleMapMarker(pdxRailViewModel)
                            is RailSystemMapItem.Line ->
                                mapItem.DisplayGoogleMapLine()
                        }
                    }
                    (railSystemArrivals as? RailSystemArrivals.Display)?.let {
                        it.arrivals.forEach { arrivalMarker ->
                            arrivalMarker.DisplayGoogleMapMarker(pdxRailViewModel)
                        }
                    }
                }
                when (mapDrawerIcon) {
                    is MapIconBitmap.Display -> {
                        Image(
                            bitmap = (mapDrawerIcon as MapIconBitmap.Display).mapIconBitmap.asImageBitmap(),
                            contentDescription = "your mom",
                            modifier = Modifier.align(Alignment.BottomStart).padding(
                                bottom = dimensionResource(R.dimen.map_icon_below),
                                start = dimensionResource(R.dimen.map_icon_start)
                            ),
                        )
                    }
                    else -> {}
                }
            }
        }
    }
}