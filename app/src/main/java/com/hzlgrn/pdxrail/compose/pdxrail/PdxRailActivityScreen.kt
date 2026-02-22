@file:OptIn(ExperimentalMaterial3Api::class)

package com.hzlgrn.pdxrail.compose.pdxrail

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidViewBinding
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.maps.android.compose.MapType
import com.hzlgrn.pdxrail.R
import com.hzlgrn.pdxrail.compose.HorizontalDividerItem
import com.hzlgrn.pdxrail.databinding.ActivityPdxRailBinding
import com.hzlgrn.pdxrail.theme.PdxRailTheme
import com.hzlgrn.pdxrail.viewmodel.PdxRailViewModel
import kotlinx.coroutines.launch

@Composable
fun PdxRailActivityScreen(
    pdxRailActivityAction: PdxRailActivityAction,
    pdxRailViewModel: PdxRailViewModel,
) {
    val coroutineScope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val isDrawerOpen by pdxRailViewModel.isDrawerOpen.collectAsStateWithLifecycle()
    val isMapTypeDropdownOpen by pdxRailViewModel.isMapTypeDropdownOpen.collectAsStateWithLifecycle()
    val railSystemArrivals by pdxRailViewModel.railSystemArrivals.collectAsStateWithLifecycle()

    if (isDrawerOpen) {
        // Open drawer and reset state in VM.
        LaunchedEffect(Unit) {
            // wrap in try-finally to handle interruption while opening drawer
            try {
                drawerState.open()
            } finally {
                pdxRailViewModel.openDrawer(false)
            }
        }
    }

    PdxRailTheme {
        Scaffold(
            topBar = {
                PdxRailActivityTopBar(
                    pdxRailActivityAction = pdxRailActivityAction,
                    pdxRailViewModel = pdxRailViewModel,
                    isMapTypeDropdownOpen = isMapTypeDropdownOpen,
                ) {
                    coroutineScope.launch {
                        if (drawerState.isOpen) {
                            drawerState.close()
                        } else if(drawerState.isClosed) {
                            drawerState.open()
                        }
                    }
                }
            },
        ) { innerPadding ->
            PdxRailDrawer(
                pdxRailViewModel = pdxRailViewModel,
                railSystemArrivals = railSystemArrivals,
                drawerState = drawerState,
                onArrivalClick = { _ -> /* TODO: Do nothing or move map? */ },
                onReviewClick = pdxRailActivityAction.onReviewClick,
                modifier = Modifier.padding(innerPadding),
            ) {
                Column {
                    Row { HorizontalDividerItem() }
                    Row { AndroidViewBinding(ActivityPdxRailBinding::inflate) }
                }
            }
        }
    }
}

@Composable
fun PdxRailActivityTopBar(
    pdxRailActivityAction: PdxRailActivityAction,
    pdxRailViewModel: PdxRailViewModel,
    isMapTypeDropdownOpen: Boolean,
    onNavigationIconClick: () -> Unit,
) {
    TopAppBar(
        title = { Text(stringResource(R.string.app_name)) },
        actions = {
            IconButton(onClick = { pdxRailActivityAction.showHelpDialog() }) {
                Icon(painterResource(R.drawable.menu_help), stringResource(R.string.menu_help))
            }

            IconButton(onClick = { pdxRailViewModel.openMapTypeDropdown(!isMapTypeDropdownOpen) }) {
                Icon(painterResource(R.drawable.menu_base_layer), stringResource(R.string.menu_google_map))
            }
            DropdownMenu(
                expanded = isMapTypeDropdownOpen,
                onDismissRequest = { pdxRailViewModel.openMapTypeDropdown(false) }
            ) {
                DropdownMenuItem(
                    onClick = { pdxRailViewModel.commitMapType(MapType.HYBRID) },
                    text = { Text(text = stringResource(R.string.google_map_type_hybrid)) },
                )
                DropdownMenuItem(
                    onClick = { pdxRailViewModel.commitMapType(MapType.SATELLITE) },
                    text = { Text(text = stringResource(R.string.google_map_type_satellite)) },
                )
                DropdownMenuItem(
                    onClick = { pdxRailViewModel.commitMapType(MapType.TERRAIN) },
                    text = { Text(text = stringResource(R.string.google_map_type_terrain)) },
                )
                DropdownMenuItem(
                    onClick = { pdxRailViewModel.commitMapType(MapType.NORMAL) },
                    text = { Text(text = stringResource(R.string.google_map_type_normal)) },
                )
            }
        },
        navigationIcon = {
            Icon(
                imageVector = Icons.Default.Menu,
                contentDescription = stringResource(R.string.app_name),
                modifier = Modifier.clickable { onNavigationIconClick() }
            )
        }
    )
}

data class PdxRailActivityAction(
    val showHelpDialog: () -> Unit,
    val onReviewClick: () -> Unit,
)