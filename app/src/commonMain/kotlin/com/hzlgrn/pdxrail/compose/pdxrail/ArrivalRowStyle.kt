package com.hzlgrn.pdxrail.compose.pdxrail

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import com.hzlgrn.pdxrail.generated.resources.Res
import com.hzlgrn.pdxrail.generated.resources.marker_max_arrival
import com.hzlgrn.pdxrail.generated.resources.marker_max_arrival_blue
import com.hzlgrn.pdxrail.generated.resources.marker_max_arrival_green
import com.hzlgrn.pdxrail.generated.resources.marker_max_arrival_orange
import com.hzlgrn.pdxrail.generated.resources.marker_max_arrival_red
import com.hzlgrn.pdxrail.generated.resources.marker_max_arrival_yellow
import com.hzlgrn.pdxrail.generated.resources.marker_streetcar_a_loop
import com.hzlgrn.pdxrail.generated.resources.marker_streetcar_b_loop
import com.hzlgrn.pdxrail.generated.resources.marker_streetcar_ns_line
import com.hzlgrn.pdxrail.viewmodel.railsystem.ArrivalLineBackground
import com.hzlgrn.pdxrail.viewmodel.railsystem.ArrivalMarkerIcon
import org.jetbrains.compose.resources.painterResource

@Composable
fun ArrivalMarkerIcon.arrivalMarkerPainter(): Painter? = when (this) {
    ArrivalMarkerIcon.DEFAULT -> painterResource(Res.drawable.marker_max_arrival)
    ArrivalMarkerIcon.MAX_BLUE -> painterResource(Res.drawable.marker_max_arrival_blue)
    ArrivalMarkerIcon.MAX_GREEN -> painterResource(Res.drawable.marker_max_arrival_green)
    ArrivalMarkerIcon.MAX_ORANGE -> painterResource(Res.drawable.marker_max_arrival_orange)
    ArrivalMarkerIcon.MAX_RED -> painterResource(Res.drawable.marker_max_arrival_red)
    ArrivalMarkerIcon.MAX_YELLOW -> painterResource(Res.drawable.marker_max_arrival_yellow)
    ArrivalMarkerIcon.STREETCAR_NS -> painterResource(Res.drawable.marker_streetcar_ns_line)
    ArrivalMarkerIcon.STREETCAR_A_LOOP -> painterResource(Res.drawable.marker_streetcar_a_loop)
    ArrivalMarkerIcon.STREETCAR_B_LOOP -> painterResource(Res.drawable.marker_streetcar_b_loop)
}

@Composable
fun ArrivalLineBackground.arrivalRowBackground(isSelected: Boolean): Modifier {
    val color = if (isSelected) {
        Color(0xFFBBDEFB)
    } else {
        if (isSystemInDarkTheme()) {
            when (this) {
                ArrivalLineBackground.DEFAULT -> Color.Transparent
                ArrivalLineBackground.MAX_BLUE -> Color(0xFF001421)
                ArrivalLineBackground.MAX_GREEN -> Color(0xFF002516)
                ArrivalLineBackground.MAX_ORANGE -> Color(0xFF1b0c05)
                ArrivalLineBackground.MAX_RED -> Color(0xFF1c0209)
                ArrivalLineBackground.MAX_YELLOW -> Color(0xFF251b00)
                ArrivalLineBackground.STREETCAR_NS -> Color(0xFF16030e)
                ArrivalLineBackground.STREETCAR_A_LOOP -> Color(0xFF001115)
                ArrivalLineBackground.STREETCAR_B_LOOP -> Color(0xFF0d1306)
            }
        } else {
            when (this) {
                ArrivalLineBackground.DEFAULT -> Color.Transparent
                ArrivalLineBackground.MAX_BLUE -> Color(0xFFE5F5FF)
                ArrivalLineBackground.MAX_GREEN -> Color(0xFFE9FFF6)
                ArrivalLineBackground.MAX_ORANGE -> Color(0xFFFBEFE9)
                ArrivalLineBackground.MAX_RED -> Color(0xFFFDE5EB)
                ArrivalLineBackground.MAX_YELLOW -> Color(0xFFFFF4D6)
                ArrivalLineBackground.STREETCAR_NS -> Color(0xFFEFF7E5)
                ArrivalLineBackground.STREETCAR_A_LOOP -> Color(0xFFFAE2F0)
                ArrivalLineBackground.STREETCAR_B_LOOP -> Color(0xFFD9F8FF)
            }
        }
    }
    return if (color == Color.Transparent) Modifier else Modifier.background(color)
}
