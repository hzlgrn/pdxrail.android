package com.hzlgrn.pdxrail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import com.hzlgrn.pdxrail.viewmodel.PdxRailViewModel
import org.koin.compose.getKoin

@Composable
actual fun rememberMainViewModel(): PdxRailViewModel {
    val koin = getKoin()
    val viewModel = remember { koin.get<PdxRailViewModel>() }
    LaunchedEffect(Unit) {
        locationAuthorized.collect { granted ->
            viewModel.setIsMyLocationEnabled(granted)
        }
    }
    return viewModel
}
