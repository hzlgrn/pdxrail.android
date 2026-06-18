package com.hzlgrn.pdxrail

import androidx.compose.runtime.Composable
import com.hzlgrn.pdxrail.viewmodel.PdxRailViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
actual fun rememberMainViewModel(): PdxRailViewModel = koinViewModel()
