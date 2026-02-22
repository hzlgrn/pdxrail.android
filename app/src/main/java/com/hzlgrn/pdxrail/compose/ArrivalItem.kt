package com.hzlgrn.pdxrail.compose

import android.text.format.DateUtils
import androidx.compose.foundation.background
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.hzlgrn.pdxrail.R
import com.hzlgrn.pdxrail.viewmodel.railsystem.RailSystemArrivalItem

@Composable
fun ArrivalItem(
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
            .clip(shape = RoundedCornerShape(dimensionResource(R.dimen.card_radius_large)))
            .then(background)
            .clickable(onClick = { onArrivalClick(item) }),
        verticalAlignment = CenterVertically,
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = dimensionResource(R.dimen.horizontal), vertical = dimensionResource(R.dimen.vertical_2x))
        ) {
            Row {
                Text(
                    item.textShortSign,
                    style = MaterialTheme.typography.headlineSmall,
                    color = if (isSelected) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    },
                    modifier = Modifier.padding(start = dimensionResource(R.dimen.horizontal)),
                )
            }
            Row {
                Column {
                    Row{
                        Icon(
                            painter = painterResource(id = item.drawableArrivalMarker),
                            tint = Color.Unspecified,
                            modifier = Modifier.rotate(item.drawableRotation),
                            contentDescription = null, // TODO: Convert rotation to cardinal text? example: "North West"
                        )
                    }
                }
                Column {
                    Row {
                        Text(
                            text = stringResource(R.string.arrival_scheduled_at, DateUtils.formatDateTime(LocalContext.current, item.scheduled, DateUtils.FORMAT_SHOW_TIME)),
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }

                    val isLate = item.estimated > item.scheduled
                    val isEarly = item.scheduled > item.estimated
                    if (isLate) {
                        Row {
                            Text(
                                text = stringResource(R.string.arrival_estimated_at, DateUtils.formatDateTime(LocalContext.current, item.estimated, DateUtils.FORMAT_SHOW_TIME)),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.error,
                            )
                        }
                    }
                    if (isEarly) {
                        Row {
                            Text(
                                text = stringResource(R.string.arrival_estimated_at, DateUtils.formatDateTime(LocalContext.current, item.estimated, DateUtils.FORMAT_SHOW_TIME)),
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