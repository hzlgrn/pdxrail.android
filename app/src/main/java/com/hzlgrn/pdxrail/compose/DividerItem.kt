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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.hzlgrn.pdxrail.R

@Composable
fun HorizontalDividerItem(modifier: Modifier = Modifier) {
    Spacer(
        modifier = modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        colorResource(R.color.colorPrimary),
                        colorResource(R.color.colorSecondary),
                    )
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
                    colors = listOf(
                        colorResource(R.color.colorPrimary),
                        colorResource(R.color.colorSecondary),
                    )
                )
            )
    )
}