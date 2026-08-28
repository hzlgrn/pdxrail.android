package com.hzlgrn.pdxrail.compose.pdxrail

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.AnchoredDraggableDefaults
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.gestures.animateTo
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
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
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.hzlgrn.pdxrail.compose.ArrivalEmptyCard
import com.hzlgrn.pdxrail.compose.ArrivalEmptyMaxViewCard
import com.hzlgrn.pdxrail.compose.ArrivalEmptyStreetcarViewCard
import com.hzlgrn.pdxrail.compose.ArrivalErrorViewCard
import com.hzlgrn.pdxrail.compose.ArrivalItem
import com.hzlgrn.pdxrail.compose.HeaderItem
import com.hzlgrn.pdxrail.compose.HorizontalDividerItem
import com.hzlgrn.pdxrail.generated.resources.Res
import com.hzlgrn.pdxrail.generated.resources.arrival_at
import com.hzlgrn.pdxrail.generated.resources.arrivals_header
import com.hzlgrn.pdxrail.generated.resources.menu_licenses
import com.hzlgrn.pdxrail.theme.LocalAppDimensions
import com.hzlgrn.pdxrail.viewmodel.PdxRailViewModel
import com.hzlgrn.pdxrail.viewmodel.railsystem.RailSystemArrivalItem
import com.hzlgrn.pdxrail.viewmodel.railsystem.RailSystemArrivals
import org.jetbrains.compose.resources.stringResource
import kotlin.math.roundToInt

@Composable
fun PdxRailDrawer(
    pdxRailViewModel: PdxRailViewModel,
    railSystemArrivals: RailSystemArrivals,
    onArrivalClick: (RailSystemArrivalItem) -> Unit,
    onReviewClick: () -> Unit,
    onLicensesClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed),
    scrimContent: @Composable () -> Unit,
) {
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val isPortrait = maxHeight > maxWidth
        if (isPortrait) {
            PdxRailDrawerPortrait(
                pdxRailViewModel = pdxRailViewModel,
                railSystemArrivals = railSystemArrivals,
                onArrivalClick = onArrivalClick,
                onReviewClick = onReviewClick,
                onLicensesClick = onLicensesClick,
                drawerState = drawerState,
                scrimContent = scrimContent,
            )
        } else {
            PdxRailDrawerLandscape(
                pdxRailViewModel = pdxRailViewModel,
                railSystemArrivals = railSystemArrivals,
                onArrivalClick = onArrivalClick,
                onReviewClick = onReviewClick,
                onLicensesClick = onLicensesClick,
                drawerState = drawerState,
                scrimContent = scrimContent,
            )
        }
    }
}

@Composable
private fun PdxRailDrawerLandscape(
    pdxRailViewModel: PdxRailViewModel,
    railSystemArrivals: RailSystemArrivals,
    onArrivalClick: (RailSystemArrivalItem) -> Unit,
    onReviewClick: () -> Unit,
    onLicensesClick: () -> Unit = {},
    drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed),
    scrimContent: @Composable () -> Unit,
) {
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val density = LocalDensity.current
        val drawerWidth = maxWidth / 2
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

        val currentOffset = anchoredDraggableState.offset.takeUnless { it.isNaN() } ?: -drawerWidthPx
        val drawerVisibleWidth = with(density) {
            (currentOffset + drawerWidthPx).coerceAtLeast(0f).toDp()
        }

        Box(Modifier.fillMaxSize().padding(start = drawerVisibleWidth)) {
            scrimContent()
        }

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
                    windowInsets = WindowInsets(left = 0, top = 0, right = 0, bottom = 0)
                ) {
                    val stationText by pdxRailViewModel.stationText.collectAsState()
                    PdxRailDrawerContent(
                        stationText = stationText,
                        railSystemArrivals = railSystemArrivals,
                        onArrivalClick = onArrivalClick,
                        onReviewClick = onReviewClick,
                        onLicensesClick = onLicensesClick,
                    )
                }
                HorizontalDividerItem(modifier = Modifier.align(Alignment.TopCenter))
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
    onLicensesClick: () -> Unit = {},
    drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed),
    scrimContent: @Composable () -> Unit,
) {
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val density = LocalDensity.current
        val drawerHeight = maxHeight / 2
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
                    windowInsets = WindowInsets(
                        left = 0.dp,
                        top = LocalAppDimensions.current.paddingXLarge,
                        right = 0.dp,
                        bottom = 0.dp
                    ),
                ) {
                    val stationText by pdxRailViewModel.stationText.collectAsState()
                    HorizontalDividerItem(modifier = Modifier.alpha(0.3f))
                    PdxRailDrawerContent(
                        stationText = stationText,
                        railSystemArrivals = railSystemArrivals,
                        onArrivalClick = onArrivalClick,
                        onReviewClick = onReviewClick,
                        onLicensesClick = onLicensesClick,
                    )
                }
                HorizontalDividerItem(modifier = Modifier.align(Alignment.TopCenter))
            }
        }
    }
}

@Composable
fun PdxRailDrawerContent(
    stationText: String,
    onArrivalClick: (RailSystemArrivalItem) -> Unit,
    onReviewClick: () -> Unit,
    onLicensesClick: () -> Unit = {},
    railSystemArrivals: RailSystemArrivals = RailSystemArrivals.Idle,
) {
    val dimens = LocalAppDimensions.current
    LazyColumn(
        modifier = Modifier,
        verticalArrangement = Arrangement.spacedBy(dimens.itemSpacing),
        contentPadding = PaddingValues(horizontal = dimens.paddingLarge, vertical = dimens.paddingMedium),
    ) {
        item {
            val headerText = if (stationText.isNotBlank()) stringResource(Res.string.arrival_at, stationText) else stringResource(Res.string.arrivals_header)
            HeaderItem(headerText)
        }
        when (railSystemArrivals) {
            is RailSystemArrivals.Idle, is RailSystemArrivals.Loading -> {
                item { ArrivalEmptyMaxViewCard() }
                item { ArrivalEmptyStreetcarViewCard() }
            }
            is RailSystemArrivals.Error -> {
                item { ArrivalErrorViewCard() }
            }
            is RailSystemArrivals.Display -> {
                if (railSystemArrivals.details.isEmpty()) {
                    item { ArrivalEmptyCard() }
                } else {
                    railSystemArrivals.details.forEach { arrivalItem ->
                        item {
                            ArrivalItem(
                                item = arrivalItem,
                                onArrivalClick = { onArrivalClick(arrivalItem) },
                            )
                        }
                    }
                }
            }
        }
        item {
            PdxRailReviewCard(
                onReviewClick = onReviewClick,
                modifier = Modifier.padding(horizontal = dimens.paddingXLarge),
            )
        }
        item {
            Text(
                text = "© OpenStreetMap contributors",
                style = MaterialTheme.typography.labelSmall,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
            Text(
                text = stringResource(Res.string.menu_licenses),
                style = MaterialTheme.typography.labelSmall,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.fillMaxWidth().clickable { onLicensesClick() },
            )
        }
        item {
            Spacer(modifier = Modifier.height(dimens.itemSpacing))
        }
    }
}

@Composable
fun DrawerPreview() {
    Surface {
        Column {
            PdxRailDrawerContent(stationText = "Pioneer Courthouse", onArrivalClick = {}, onReviewClick = {})
        }
    }
}
