package com.hzlgrn.pdxrail.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class AppDimensions(
    val paddingXSmall: Dp,
    val paddingSmall: Dp,
    val paddingMedium: Dp,
    val paddingLarge: Dp,
    val paddingXLarge: Dp,
    val itemSpacing: Dp,
    val cornerRadiusSmall: Dp,
    val cornerRadiusMedium: Dp,
    val cornerRadiusLarge: Dp,
    val iconSize: Dp,
    val iconSizeSmall: Dp,
    val mapLineWidth: Dp,
    val mapLineOutlineWidth: Dp,
    val mapLocationDotRadius: Dp,
    val mapLocationDotStroke: Dp,
)

private fun scaledDimensions(scale: Float) = AppDimensions(
    paddingXSmall        = (2 * scale).dp,
    paddingSmall         = (4 * scale).dp,
    paddingMedium        = (8 * scale).dp,
    paddingLarge         = (12 * scale).dp,
    paddingXLarge        = (16 * scale).dp,
    itemSpacing          = (6 * scale).dp,
    cornerRadiusSmall    = (4 * scale).dp,
    cornerRadiusMedium   = (8 * scale).dp,
    cornerRadiusLarge    = (27 * scale).dp,
    iconSize             = (36 * scale).dp,
    iconSizeSmall        = (20 * scale).dp,
    mapLineWidth         = (4 * scale).dp,
    mapLineOutlineWidth  = (6 * scale).dp,
    mapLocationDotRadius = (8 * scale).dp,
    mapLocationDotStroke = (2 * scale).dp,
)

// ldpi / mdpi — small/budget screens
val compactDimensions = scaledDimensions(1.00f)

// hdpi / xhdpi — mid-range screens
val mediumDimensions = scaledDimensions(1.15f)

// xxhdpi / xxxhdpi — flagship screens
val expandedDimensions = scaledDimensions(1.35f)

val LocalAppDimensions = staticCompositionLocalOf { compactDimensions }
