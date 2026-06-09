package com.hzlgrn.pdxrail.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val colorPrimary = Color(0xFF004a98)
private val colorSecondary = Color(0xFFFFFF66)

@Composable
fun HorizontalDividerItem(modifier: Modifier = Modifier) {
    Spacer(
        modifier = modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(colorPrimary, colorSecondary)
                )
            )
    )
}

@Composable
fun VerticalDividerItem(modifier: Modifier = Modifier) {
    Spacer(
        modifier = modifier
            .fillMaxHeight()
            .width(1.dp)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(colorPrimary, colorSecondary)
                )
            )
    )
}
