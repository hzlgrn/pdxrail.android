package com.hzlgrn.pdxrail

import androidx.compose.runtime.Composable
import com.hzlgrn.pdxrail.compose.pdxrail.PdxRailActivityScreen
import com.hzlgrn.pdxrail.viewmodel.PdxRailViewModel

@Composable
expect fun rememberMainViewModel(): PdxRailViewModel

@Composable
fun App(
    onReviewClick: () -> Unit = {},
    onRequestLocationPermission: () -> Unit = {},
    onLicensesClick: () -> Unit = {},
) {
    val viewModel: PdxRailViewModel = rememberMainViewModel()
    PdxRailActivityScreen(
        onReviewClick = onReviewClick,
        onRequestLocationPermission = onRequestLocationPermission,
        onLicensesClick = onLicensesClick,
        pdxRailViewModel = viewModel,
    )
}
