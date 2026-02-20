package com.hzlgrn.pdxrail.compose

import android.text.format.DateUtils
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.exponentialDecay
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.gestures.animateTo
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment.Companion.CenterStart
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.hzlgrn.pdxrail.theme.PdxRailTheme
import com.hzlgrn.pdxrail.viewmodel.railsystem.RailSystemArrivalItem
import com.hzlgrn.pdxrail.viewmodel.railsystem.RailSystemArrivals
import kotlin.math.roundToInt

@Composable
fun PdxRailDrawer(
    railSystemArrivals: RailSystemArrivals,
    onArrivalClick: (String) -> Unit,
    onReviewClick: () -> Unit,
    modifier: Modifier = Modifier,
    drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed),
    content: @Composable () -> Unit,
) {
    val density = LocalDensity.current
    val drawerWidth = (LocalConfiguration.current.screenWidthDp / 2f).dp
    val drawerWidthPx = with(density) { drawerWidth.toPx() }

    val anchoredDraggableState = remember {
        AnchoredDraggableState(
            initialValue = DrawerValue.Closed,
            positionalThreshold = { distance -> distance * 0.4f },
            velocityThreshold = { with(density) { 400.dp.toPx() } },
            snapAnimationSpec = spring(
                dampingRatio = Spring.DampingRatioNoBouncy,
                stiffness = Spring.StiffnessMediumLow,
            ),
            decayAnimationSpec = exponentialDecay(),
        )
    }

    LaunchedEffect(drawerWidthPx) {
        anchoredDraggableState.updateAnchors(DraggableAnchors {
            DrawerValue.Closed at -drawerWidthPx
            DrawerValue.Open at 0f
        })
    }

    // Sync: external DrawerState.open()/close() calls → internal drag animation
    LaunchedEffect(drawerState.targetValue) {
        when (drawerState.targetValue) {
            DrawerValue.Open -> anchoredDraggableState.animateTo(DrawerValue.Open)
            DrawerValue.Closed -> anchoredDraggableState.animateTo(DrawerValue.Closed)
        }
    }

    // Sync: drag settled → external DrawerState so isOpen/isClosed stay accurate
    LaunchedEffect(anchoredDraggableState.currentValue) {
        when (anchoredDraggableState.currentValue) {
            DrawerValue.Open -> if (drawerState.targetValue != DrawerValue.Open) drawerState.open()
            DrawerValue.Closed -> if (drawerState.targetValue != DrawerValue.Closed) drawerState.close()
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        // Content is always fully interactive — no scrim, no input blocking
        Box(Modifier.fillMaxSize()) { content() }

        val currentOffset = anchoredDraggableState.offset.takeUnless { it.isNaN() } ?: -drawerWidthPx
        if (currentOffset > -drawerWidthPx) {
            Box(
                modifier = Modifier
                    .width(drawerWidth)
                    .fillMaxHeight()
                    .offset { IntOffset(currentOffset.roundToInt(), 0) }
                    .anchoredDraggable(
                        state = anchoredDraggableState,
                        orientation = Orientation.Horizontal,
                    )
            ) {
                ModalDrawerSheet(
                    modifier = Modifier.fillMaxWidth(),
                    drawerContainerColor = MaterialTheme.colorScheme.background,
                    drawerContentColor = MaterialTheme.colorScheme.onBackground,
                ) {
                    PdxRailDrawerContent(
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
    onArrivalClick: (String) -> Unit,
    onReviewClick: () -> Unit,
    railSystemArrivals: RailSystemArrivals = RailSystemArrivals.Idle
    ) {
    LazyColumn() {
        item {
            HeaderItem("Arrivals") // TODO: "Arrivals to ${shortSign}"
        }
        when (railSystemArrivals) {
            is RailSystemArrivals.Display -> {
                railSystemArrivals.details.forEach { arrivalDisplay ->
                    item {
                        ArrivalItem(arrivalDisplay)
                    }
                }
            }
            else -> { /* nothing */ }
        }
    }
}

@Composable
private fun HeaderItem(text: String) {
    Box(
        modifier = Modifier
            .padding(horizontal = 12.dp, vertical = 6.dp),
        contentAlignment = CenterStart,
    ) {
        Text(
            text,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun ArrivalItem(
    item: RailSystemArrivalItem,
    onArrivalClick: (RailSystemArrivalItem) -> Unit = {},
    isSelected: Boolean = false,
) {
    val background = if (isSelected) {
        Modifier.background(MaterialTheme.colorScheme.surfaceContainerHigh)
    } else {
        Modifier.background(MaterialTheme.colorScheme.surfaceContainer)
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(12.dp))
            .then(background)
            .clickable(onClick = { onArrivalClick(item) }),
        verticalAlignment = CenterVertically,
    ) {
        Column(
            modifier = Modifier.padding(6.dp)
        ) {
            Row() {
                Text(
                    item.textShortSign,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isSelected) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    },
                    modifier = Modifier.padding(start = 6.dp),
                )
            }
            Row() {
                Icon(
                    painter = painterResource(id = item.drawableArrivalMarker),
                    tint = Color.Unspecified,
                    modifier = Modifier.rotate(item.drawableRotation),
                    contentDescription = null, // TODO: Convert rotation to cardinal text. example: "North West"
                )
                Column() {
                    Row() {
                        Text(
                            DateUtils.formatDateTime(LocalContext.current, item.scheduled, DateUtils.FORMAT_SHOW_TIME),
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }

                    val isLate = item.estimated > item.scheduled
                    val isEarly = item.scheduled > item.estimated
                    if (isLate) {
                        Row() {
                            Text(
                                DateUtils.formatDateTime(LocalContext.current, item.estimated, DateUtils.FORMAT_SHOW_TIME),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.error,
                            )
                        }
                    }
                    if (isEarly) {
                        Row() {
                            Text(
                                DateUtils.formatDateTime(LocalContext.current, item.estimated, DateUtils.FORMAT_SHOW_TIME),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DividerItem(modifier: Modifier = Modifier) {
    HorizontalDivider(
        modifier = modifier,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
    )
}

@Composable
@Preview
fun DrawerPreview() {
    PdxRailTheme {
        Surface {
            Column {
                PdxRailDrawerContent({}, {})
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
                PdxRailDrawerContent({}, {})
            }
        }
    }
}

/*
@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun WidgetDiscoverability() {
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .height(56.dp)
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .clip(CircleShape)
            .clickable(onClick = {
                addWidgetToHomeScreen(context)
            }),
        verticalAlignment = CenterVertically,
    ) {
        Text(
            stringResource(id = R.string.add_widget_to_home_page),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(start = 12.dp),
        )
    }
}
 */

/*
@RequiresApi(Build.VERSION_CODES.O)
private fun addWidgetToHomeScreen(context: Context) {
    val appWidgetManager = AppWidgetManager.getInstance(context)
    val myProvider = ComponentName(context, WidgetReceiver::class.java)
    if (widgetAddingIsSupported(context)) {
        appWidgetManager.requestPinAppWidget(myProvider, null, null)
    }
}

@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.O)
private fun widgetAddingIsSupported(context: Context): Boolean {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O &&
            AppWidgetManager.getInstance(context).isRequestPinAppWidgetSupported
}
 */
