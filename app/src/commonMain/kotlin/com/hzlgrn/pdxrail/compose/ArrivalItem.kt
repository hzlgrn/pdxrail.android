package com.hzlgrn.pdxrail.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import com.hzlgrn.pdxrail.compose.pdxrail.arrivalMarkerPainter
import com.hzlgrn.pdxrail.compose.pdxrail.arrivalRowBackground
import com.hzlgrn.pdxrail.generated.resources.Res
import com.hzlgrn.pdxrail.generated.resources.arrival_estimated_at
import com.hzlgrn.pdxrail.generated.resources.arrival_scheduled_at
import com.hzlgrn.pdxrail.theme.LocalAppDimensions
import com.hzlgrn.pdxrail.viewmodel.railsystem.RailSystemArrivalItem
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.stringResource

@Composable
fun ArrivalItem(
    item: RailSystemArrivalItem,
    onArrivalClick: (RailSystemArrivalItem) -> Unit = {},
    isSelected: Boolean = false,
) {
    val bg = item.lineBackground.arrivalRowBackground(isSelected)
    val dimens = LocalAppDimensions.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape = RoundedCornerShape(dimens.cornerRadiusLarge))
            .then(bg)
            .clickable(onClick = { onArrivalClick(item) }),
        verticalAlignment = CenterVertically,
    ) {
        Column(
            modifier = Modifier.padding(horizontal = dimens.paddingMedium, vertical = dimens.paddingLarge)
        ) {
            Row {
                Text(
                    item.textShortSign,
                    style = MaterialTheme.typography.titleSmall,
                    color = if (isSelected) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(start = dimens.paddingMedium),
                )
            }
            Row {
                Column {
                    Row {
                        val painter = item.markerIcon.arrivalMarkerPainter()
                        if (painter != null) {
                            Icon(
                                painter = painter,
                                tint = Color.Unspecified,
                                modifier = Modifier
                                    .rotate(item.drawableRotation)
                                    .alpha(if (item.isMaxStop) 1f else 0f),
                                contentDescription = null,
                            )
                        }
                    }
                }
                Column {
                    Row {
                        Text(
                            text = stringResource(Res.string.arrival_scheduled_at, formatTime(item.scheduled)),
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                    val isLate = item.estimated > item.scheduled
                    val isEarly = item.scheduled > item.estimated
                    if (isLate) {
                        Row {
                            Text(
                                text = stringResource(Res.string.arrival_estimated_at, formatTime(item.estimated)),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.error,
                            )
                        }
                    }
                    if (isEarly) {
                        Row {
                            Text(
                                text = stringResource(Res.string.arrival_estimated_at, formatTime(item.estimated)),
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

private fun formatTime(epochMillis: Long): String {
    if (epochMillis == 0L) return "--"
    val instant = Instant.fromEpochMilliseconds(epochMillis)
    val dt = instant.toLocalDateTime(TimeZone.currentSystemDefault())
    val hour = dt.hour
    val minute = dt.minute.toString().padStart(2, '0')
    val period = if (hour < 12) "AM" else "PM"
    val h = when {
        hour == 0 -> 12
        hour <= 12 -> hour
        else -> hour - 12
    }
    return "$h:$minute $period"
}
