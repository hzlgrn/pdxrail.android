package com.hzlgrn.pdxrail.compose.pdxrail

import android.content.res.Configuration
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.gestures.AnchoredDraggableDefaults
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.gestures.animateTo
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hzlgrn.pdxrail.R
import com.hzlgrn.pdxrail.compose.ArrivalEmptyMaxViewCard
import com.hzlgrn.pdxrail.compose.ArrivalEmptyStreetcarViewCard
import com.hzlgrn.pdxrail.compose.ArrivalItem
import com.hzlgrn.pdxrail.compose.HeaderItem
import com.hzlgrn.pdxrail.compose.HorizontalDividerItem
import com.hzlgrn.pdxrail.theme.PdxRailTheme
import com.hzlgrn.pdxrail.viewmodel.PdxRailViewModel
import com.hzlgrn.pdxrail.viewmodel.railsystem.RailSystemArrivalItem
import com.hzlgrn.pdxrail.viewmodel.railsystem.RailSystemArrivals
import kotlin.math.roundToInt

@Composable
fun PdxRailDrawer(
    pdxRailViewModel: PdxRailViewModel,
    railSystemArrivals: RailSystemArrivals,
    onArrivalClick: (RailSystemArrivalItem) -> Unit,
    onReviewClick: () -> Unit,
    modifier: Modifier = Modifier,
    drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed),
    scrimContent: @Composable () -> Unit,
) {
    val isPortrait = LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT
    if (isPortrait) {
        PdxRailDrawerPortrait(
            pdxRailViewModel = pdxRailViewModel,
            railSystemArrivals = railSystemArrivals,
            onArrivalClick = onArrivalClick,
            onReviewClick = onReviewClick,
            modifier = modifier,
            drawerState = drawerState,
            scrimContent = scrimContent,
        )
    } else {
        PdxRailDrawerLandscape(
            pdxRailViewModel = pdxRailViewModel,
            railSystemArrivals = railSystemArrivals,
            onArrivalClick = onArrivalClick,
            onReviewClick = onReviewClick,
            modifier = modifier,
            drawerState = drawerState,
            scrimContent = scrimContent,
        )
    }
}

@Composable
private fun PdxRailDrawerLandscape(
    pdxRailViewModel: PdxRailViewModel,
    railSystemArrivals: RailSystemArrivals,
    onArrivalClick: (RailSystemArrivalItem) -> Unit,
    onReviewClick: () -> Unit,
    modifier: Modifier = Modifier,
    drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed),
    scrimContent: @Composable () -> Unit,
) {
    val density = LocalDensity.current
    val drawerWidth = (LocalConfiguration.current.screenWidthDp / 2f).dp
    val drawerWidthPx = with(density) { drawerWidth.toPx() }

    val anchoredDraggableState = remember(drawerWidthPx) {
        AnchoredDraggableState(
            initialValue = DrawerValue.Closed,
            anchors = DraggableAnchors {
                DrawerValue.Closed at -drawerWidthPx
                DrawerValue.Open at 0f
            },
        )
    }

    val flingBehavior = AnchoredDraggableDefaults.flingBehavior(
        state = anchoredDraggableState,
        positionalThreshold = { distance -> distance * 0.4f },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMediumLow,
        ),
    )

    LaunchedEffect(drawerState.targetValue) {
        when (drawerState.targetValue) {
            DrawerValue.Open -> anchoredDraggableState.animateTo(DrawerValue.Open)
            DrawerValue.Closed -> anchoredDraggableState.animateTo(DrawerValue.Closed)
        }
    }

    LaunchedEffect(anchoredDraggableState.currentValue) {
        when (anchoredDraggableState.currentValue) {
            DrawerValue.Open -> if (drawerState.targetValue != DrawerValue.Open) drawerState.open()
            DrawerValue.Closed -> if (drawerState.targetValue != DrawerValue.Closed) drawerState.close()
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        val currentOffset = anchoredDraggableState.offset.takeUnless { it.isNaN() } ?: -drawerWidthPx
        val drawerVisibleWidth = with(density) {
            (currentOffset + drawerWidthPx).coerceAtLeast(0f).toDp()
        }

        Box(Modifier.fillMaxSize().padding(start = drawerVisibleWidth)) { scrimContent() }

        if (currentOffset > -drawerWidthPx) {
            Box(
                modifier = Modifier
                    .width(drawerWidth)
                    .fillMaxHeight()
                    .offset { IntOffset(currentOffset.roundToInt(), 0) }
                    .anchoredDraggable(
                        state = anchoredDraggableState,
                        orientation = Orientation.Horizontal,
                        flingBehavior = flingBehavior,
                    )
            ) {
                ModalDrawerSheet(
                    modifier = Modifier.fillMaxWidth(),
                    drawerContainerColor = MaterialTheme.colorScheme.background,
                    drawerContentColor = MaterialTheme.colorScheme.onBackground,
                ) {
                    val stationText by pdxRailViewModel.stationText.collectAsStateWithLifecycle()
                    PdxRailDrawerContent(
                        stationText = stationText,
                        railSystemArrivals = railSystemArrivals,
                        onArrivalClick = onArrivalClick,
                        onReviewClick = onReviewClick,
                    )
                }
            }
        }
    }
}

@Composable
private fun PdxRailDrawerPortrait(
    pdxRailViewModel: PdxRailViewModel,
    railSystemArrivals: RailSystemArrivals,
    onArrivalClick: (RailSystemArrivalItem) -> Unit,
    onReviewClick: () -> Unit,
    modifier: Modifier = Modifier,
    drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed),
    scrimContent: @Composable () -> Unit,
) {
    val density = LocalDensity.current
    val drawerHeight = (LocalConfiguration.current.screenHeightDp / 2f).dp
    val drawerHeightPx = with(density) { drawerHeight.toPx() }

    val anchoredDraggableState = remember(drawerHeightPx) {
        AnchoredDraggableState(
            initialValue = DrawerValue.Closed,
            anchors = DraggableAnchors {
                DrawerValue.Closed at drawerHeightPx
                DrawerValue.Open at 0f
            },
        )
    }

    val flingBehavior = AnchoredDraggableDefaults.flingBehavior(
        state = anchoredDraggableState,
        positionalThreshold = { distance -> distance * 0.4f },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMediumLow,
        ),
    )

    LaunchedEffect(drawerState.targetValue) {
        when (drawerState.targetValue) {
            DrawerValue.Open -> anchoredDraggableState.animateTo(DrawerValue.Open)
            DrawerValue.Closed -> anchoredDraggableState.animateTo(DrawerValue.Closed)
        }
    }

    LaunchedEffect(anchoredDraggableState.currentValue) {
        when (anchoredDraggableState.currentValue) {
            DrawerValue.Open -> if (drawerState.targetValue != DrawerValue.Open) drawerState.open()
            DrawerValue.Closed -> if (drawerState.targetValue != DrawerValue.Closed) drawerState.close()
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        val currentOffset = anchoredDraggableState.offset.takeUnless { it.isNaN() } ?: drawerHeightPx
        val drawerVisibleHeight = with(density) {
            (drawerHeightPx - currentOffset).coerceAtLeast(0f).toDp()
        }

        Box(Modifier.fillMaxSize().padding(bottom = drawerVisibleHeight)) { scrimContent() }

        if (currentOffset < drawerHeightPx) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(drawerHeight)
                    .align(Alignment.BottomStart)
                    .offset { IntOffset(0, currentOffset.roundToInt()) }
                    .anchoredDraggable(
                        state = anchoredDraggableState,
                        orientation = Orientation.Vertical,
                        flingBehavior = flingBehavior,
                    )
            ) {
                ModalDrawerSheet(
                    modifier = Modifier.fillMaxSize(),
                    drawerContainerColor = MaterialTheme.colorScheme.background,
                    drawerContentColor = MaterialTheme.colorScheme.onBackground,
                ) {
                    val stationText by pdxRailViewModel.stationText.collectAsStateWithLifecycle()
                    PdxRailDrawerContent(
                        stationText = stationText,
                        railSystemArrivals = railSystemArrivals,
                        onArrivalClick = onArrivalClick,
                        onReviewClick = onReviewClick,
                    )
                }
            }
        }
    }
}

@Composable
fun PdxRailDrawerContent(
    stationText: String,
    onArrivalClick: (RailSystemArrivalItem) -> Unit,
    onReviewClick: () -> Unit,
    railSystemArrivals: RailSystemArrivals = RailSystemArrivals.Idle
    ) {
    HorizontalDividerItem(modifier = Modifier.padding(bottom = dimensionResource(R.dimen.vertical)))
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.vertical_2x)),
        contentPadding = PaddingValues(horizontal = dimensionResource(R.dimen.horizontal_2x))
    ) {
        when (railSystemArrivals) {
            is RailSystemArrivals.Idle, is RailSystemArrivals.Loading-> {
                item {
                    ArrivalEmptyMaxViewCard()
                }
                item {
                    ArrivalEmptyStreetcarViewCard()
                }
            }
            else -> {
                item {
                    HeaderItem(stationText.takeIf { it.isNotBlank() }?.let { stringResource(R.string.arrival_at, stationText) } ?: stringResource(R.string.arrivals_header))
                }
            }
        }
        when (railSystemArrivals) {
            is RailSystemArrivals.Display -> {
                railSystemArrivals.details.forEach { railSystemArrivalItem ->
                    item {
                        ArrivalItem(
                            item = railSystemArrivalItem,
                            onArrivalClick = { onArrivalClick(railSystemArrivalItem) }
                        )
                    }
                }
            }
            else -> { /* nothing */ }
        }
        item {
            PdxRailReviewCard(
                onReviewClick = onReviewClick,
                modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.horizontal_4x)),
            )
        }
    }
}

@Composable
@Preview
fun DrawerPreview() {
    PdxRailTheme {
        Surface {
            Column {
                PdxRailDrawerContent(stationText = "station text", {}, {})
            }
        }
    }
}

@Composable
@Preview
fun DrawerPreviewDark() {
    PdxRailTheme(isDarkTheme = true) {
        Surface {
            Column {
                PdxRailDrawerContent(stationText = "station text", {}, {})
            }
        }
    }
}
