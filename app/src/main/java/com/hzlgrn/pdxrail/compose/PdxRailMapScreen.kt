package com.hzlgrn.pdxrail.compose

import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.Dash
import com.google.android.gms.maps.model.Gap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberUpdatedMarkerState
import com.hzlgrn.pdxrail.R
import com.hzlgrn.pdxrail.data.repository.viewmodel.RailSystemMapItem

@Composable
fun RailSystemMapItem.Marker.DisplayGoogleMapMarker() {
    when (this) {
        is RailSystemMapItem.Marker.Stop.MaxStop ->
            Marker(
                state = rememberUpdatedMarkerState(position = this.position),
                anchor = Offset(0.5f, 0.5f),
                icon = BitmapDescriptorFactory.fromResource(R.drawable.marker_max_stop),
                flat = true,
                infoWindowAnchor = Offset(0.5f, 0.5f),
                title = this.stationText,
                tag = this.uniqueId.uniqueIdString
            )
        is RailSystemMapItem.Marker.Stop.StreetcarStop ->
            Marker(
                state = rememberUpdatedMarkerState(position = this.position),
                anchor = Offset(0.5f, 0.5f),
                icon = BitmapDescriptorFactory.fromResource(R.drawable.marker_streetcar_stop),
                flat = true,
                infoWindowAnchor = Offset(0.5f, 0.5f),
                title = this.stationText,
                tag = this.uniqueId.uniqueIdString
            )
        // Undefined render the same as Max but with no title or Id?
        // Looks to be only WES stops. Investigate later.
        is RailSystemMapItem.Marker.Undefined ->
            Marker(
                state = rememberUpdatedMarkerState(position = this.position),
                anchor = Offset(0.5f, 0.5f),
                icon = BitmapDescriptorFactory.fromResource(R.drawable.marker_max_stop),
                flat = true,
                infoWindowAnchor = Offset(0.5f, 0.5f),
            )

    }

}

@Composable
fun RailSystemMapItem.Line.DisplayGoogleMapLine() {
    Polyline(points = this.polyline, color = Color.White, width = widthOutLine)
    when (this) {
        is RailSystemMapItem.Line.MaxBlue ->
            Polyline(points = this.polyline, color = colorResource(R.color.max_blue_line))
        is RailSystemMapItem.Line.MaxGreen ->
            Polyline(points = this.polyline, color = colorResource(R.color.max_green_line))
        is RailSystemMapItem.Line.MaxOrange ->
            Polyline(points = this.polyline, color = colorResource(R.color.max_orange_line))
        is RailSystemMapItem.Line.MaxRed ->
            Polyline(points = this.polyline, color = colorResource(R.color.max_red_line))
        is RailSystemMapItem.Line.MaxYellow ->
            Polyline(points = this.polyline, color = colorResource(R.color.max_yellow_line))
        is RailSystemMapItem.Line.MaxBlueGreen -> {
            Polyline(points = this.polyline, color = colorResource(R.color.max_blue_line), pattern = lPattern1o2)
            Polyline(points = this.polyline, color = colorResource(R.color.max_green_line), pattern = lPattern2o2)
        }
        is RailSystemMapItem.Line.MaxBlueRed -> {
            Polyline(points = this.polyline, color = colorResource(R.color.max_blue_line), pattern = lPattern1o2)
            Polyline(points = this.polyline, color = colorResource(R.color.max_red_line), pattern = lPattern2o2)
        }
        is RailSystemMapItem.Line.MaxGreenOrange -> {
            Polyline(points = this.polyline, color = colorResource(R.color.max_green_line), pattern = lPattern1o2)
            Polyline(points = this.polyline, color = colorResource(R.color.max_orange_line), pattern = lPattern2o2)
        }
        is RailSystemMapItem.Line.MaxGreenYellow -> {
            Polyline(points = this.polyline, color = colorResource(R.color.max_green_line), pattern = lPattern1o2)
            Polyline(points = this.polyline, color = colorResource(R.color.max_yellow_line), pattern = lPattern2o2)
        }
        is RailSystemMapItem.Line.MaxBlueGreenRed -> {
            Polyline(points = this.polyline, color = colorResource(R.color.max_blue_line), pattern = lPattern1o3)
            Polyline(points = this.polyline, color = colorResource(R.color.max_green_line), pattern = lPattern2o3)
            Polyline(points = this.polyline, color = colorResource(R.color.max_red_line), pattern = lPattern3o3)
        }
        is RailSystemMapItem.Line.MaxBlueGreenRedYellow -> {
            Polyline(points = this.polyline, color = colorResource(R.color.max_blue_line), pattern = lPattern1o4)
            Polyline(points = this.polyline, color = colorResource(R.color.max_green_line), pattern = lPattern2o4)
            Polyline(points = this.polyline, color = colorResource(R.color.max_red_line), pattern = lPattern3o4)
            Polyline(points = this.polyline, color = colorResource(R.color.max_yellow_line), pattern = lPattern4o4)
        }
        is RailSystemMapItem.Line.WES ->
            Polyline(points = this.polyline, color = colorResource(R.color.wes_commuter_rail))
        is RailSystemMapItem.Line.StreetcarALoop -> {
            Polyline(
                points = this.polyline,
                color = colorResource(R.color.portland_streetcar_a_loop)
            )
        }
        is RailSystemMapItem.Line.StreetcarBLoop -> {
            Polyline(
                points = this.polyline,
                color = colorResource(R.color.portland_streetcar_b_loop)
            )
        }
        is RailSystemMapItem.Line.StreetcarNorthSouth -> {
            Polyline(
                points = this.polyline,
                color = colorResource(R.color.portland_streetcar_north_south_line)
            )
        }
        is RailSystemMapItem.Line.StreetcarAB -> {
            Polyline(points = this.polyline, color = colorResource(R.color.portland_streetcar_a_loop), pattern = lPattern1o2)
            Polyline(points = this.polyline, color = colorResource(R.color.portland_streetcar_b_loop), pattern = lPattern2o2)
        }
        is RailSystemMapItem.Line.StreetcarNSB -> {
            Polyline(points = this.polyline, color = colorResource(R.color.portland_streetcar_north_south_line), pattern = lPattern1o2)
            Polyline(points = this.polyline, color = colorResource(R.color.portland_streetcar_b_loop), pattern = lPattern2o2)
        }
        is RailSystemMapItem.Line.StreetcarNSA -> {
            Polyline(points = this.polyline, color = colorResource(R.color.portland_streetcar_north_south_line), pattern = lPattern1o2)
            Polyline(points = this.polyline, color = colorResource(R.color.portland_streetcar_a_loop), pattern = lPattern2o2)
        }
        is RailSystemMapItem.Line.StreetcarMaxABOrange -> {
            Polyline(points = this.polyline, color = colorResource(R.color.max_orange_line), pattern = lPattern1o3)
            Polyline(points = this.polyline, color = colorResource(R.color.portland_streetcar_a_loop), pattern = lPattern2o3)
            Polyline(points = this.polyline, color = colorResource(R.color.portland_streetcar_b_loop), pattern = lPattern3o3)
        }
        is RailSystemMapItem.Line.StreetcarNSAB -> {
            Polyline(points = this.polyline, color = colorResource(R.color.portland_streetcar_north_south_line), pattern = lPattern1o3)
            Polyline(points = this.polyline, color = colorResource(R.color.portland_streetcar_a_loop), pattern = lPattern2o3)
            Polyline(points = this.polyline, color = colorResource(R.color.portland_streetcar_b_loop), pattern = lPattern3o3)
        }
        is RailSystemMapItem.Line.Basic ->
            Polyline(points = this.polyline)
    }
}

private const val widthOutLine = 16.0f
private val lPattern1o2 = listOf(Dash(60f),Gap(60f))
private val lPattern2o2 = listOf(Gap(60f),Dash(60f))

private val lPattern1o3 = listOf(Dash(60f),Gap(120f))
private val lPattern2o3 = listOf(Gap(60f),Dash(60f),Gap(60f))
private val lPattern3o3 = listOf(Gap(120f),Dash(60f))

private val lPattern1o4 = listOf(Dash(60f),Gap(180f))
private val lPattern2o4 = listOf(Gap(60f),Dash(60f),Gap(120f))
private val lPattern3o4 = listOf(Gap(120f),Dash(60f),Gap(60f))
private val lPattern4o4 = listOf(Gap(180f),Dash(60f))