package com.hzlgrn.pdxrail.compose

import android.text.format.DateUtils
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterStart
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hzlgrn.pdxrail.theme.PdxRailTheme
import com.hzlgrn.pdxrail.viewmodel.railsystem.RailSystemArrivalItem
import com.hzlgrn.pdxrail.viewmodel.railsystem.RailSystemArrivals

@Composable
fun PdxRailDrawer(
    railSystemArrivals: RailSystemArrivals,
    onArrivalClick: (String) -> Unit,
    onReviewClick: () -> Unit,
    modifier: Modifier = Modifier,
    drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed),
    content: @Composable () -> Unit,
) {
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerState = drawerState,
                drawerContainerColor = MaterialTheme.colorScheme.background,
                drawerContentColor = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.width(240.dp)
                // Consider 1/3 screen width to reserve viewable space? Or refactor to
                // a bottom sheet. Is there enough screen for the map and an arrivals list?
            ) {
                PdxRailDrawerContent(
                    railSystemArrivals = railSystemArrivals,
                    onArrivalClick = onArrivalClick,
                    onReviewClick = onReviewClick,
                )
            }
        },
        content = content,
        gesturesEnabled = false,
        modifier = modifier,
    )
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