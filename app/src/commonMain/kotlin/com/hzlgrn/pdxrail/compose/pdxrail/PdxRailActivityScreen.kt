@file:OptIn(ExperimentalMaterial3Api::class)

package com.hzlgrn.pdxrail.compose.pdxrail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.hzlgrn.pdxrail.compose.HorizontalDividerItem
import com.hzlgrn.pdxrail.generated.resources.Res
import com.hzlgrn.pdxrail.generated.resources.menu_help
import com.hzlgrn.pdxrail.generated.resources.menu_pdxrail
import com.hzlgrn.pdxrail.theme.PdxRailTheme
import com.hzlgrn.pdxrail.viewmodel.PdxRailViewModel
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun PdxRailActivityScreen(
    onReviewClick: () -> Unit,
    onRequestLocationPermission: () -> Unit = {},
    onLicensesClick: () -> Unit = {},
    pdxRailViewModel: PdxRailViewModel,
) {
    val coroutineScope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val isDrawerOpen by pdxRailViewModel.isDrawerOpen.collectAsState()
    val railSystemArrivals by pdxRailViewModel.railSystemArrivals.collectAsState()
    val isHelpDialogVisible by pdxRailViewModel.isHelpDialogVisible.collectAsState()
    val isMyLocationEnabled by pdxRailViewModel.isMyLocationEnabled.collectAsState()

    if (isDrawerOpen) {
        LaunchedEffect(Unit) {
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
                    pdxRailViewModel = pdxRailViewModel,
                    onHelpClick = { pdxRailViewModel.showHelpDialog() },
                    onNavigationIconClick = {
                        coroutineScope.launch {
                            if (drawerState.isOpen) drawerState.close()
                            else if (drawerState.isClosed) drawerState.open()
                        }
                    },
                )
            },
        ) { innerPadding ->
            PdxRailDrawer(
                pdxRailViewModel = pdxRailViewModel,
                railSystemArrivals = railSystemArrivals,
                drawerState = drawerState,
                onArrivalClick = { },
                onReviewClick = onReviewClick,
                onLicensesClick = onLicensesClick,
                modifier = Modifier.padding(innerPadding),
            ) {
                Column {
                    Row { HorizontalDividerItem() }
                    Row { PdxRailMap(pdxRailViewModel) }
                }
            }
        }

        if (isHelpDialogVisible) {
            HelpDialog(
                onDismiss = { pdxRailViewModel.dismissHelpDialog() },
                isMyLocationEnabled = isMyLocationEnabled,
                onRequestLocationPermission = onRequestLocationPermission,
            )
        }
    }
}

@Composable
fun PdxRailActivityTopBar(
    pdxRailViewModel: PdxRailViewModel,
    onHelpClick: () -> Unit,
    onNavigationIconClick: () -> Unit,
) {
    TopAppBar(
        title = { Text("PDX Rail") },
        actions = {
            IconButton(onClick = onHelpClick) {
                Icon(painterResource(Res.drawable.menu_help), stringResource(Res.string.menu_help))
            }
        },
        navigationIcon = {
            IconButton(onClick = onNavigationIconClick) {
                Icon(
                    painter = painterResource(Res.drawable.menu_pdxrail),
                    contentDescription = "PDX Rail",
                    tint = androidx.compose.ui.graphics.Color.Unspecified,
                )
            }
        }
    )
}
