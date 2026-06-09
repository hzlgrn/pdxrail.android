package com.hzlgrn.pdxrail.compose.pdxrail

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.hzlgrn.pdxrail.generated.resources.Res
import com.hzlgrn.pdxrail.generated.resources.cd_max_stop
import com.hzlgrn.pdxrail.generated.resources.cd_streetcar_stop
import com.hzlgrn.pdxrail.generated.resources.cd_vehicle_arrival
import com.hzlgrn.pdxrail.generated.resources.dialog_help_action
import com.hzlgrn.pdxrail.generated.resources.help_arrival
import com.hzlgrn.pdxrail.generated.resources.help_max_stop
import com.hzlgrn.pdxrail.generated.resources.help_streetcar_stop
import com.hzlgrn.pdxrail.generated.resources.help_title
import com.hzlgrn.pdxrail.generated.resources.marker_max_arrival
import com.hzlgrn.pdxrail.generated.resources.marker_max_stop
import com.hzlgrn.pdxrail.generated.resources.marker_streetcar_stop
import com.hzlgrn.pdxrail.generated.resources.permission_location_reason
import com.hzlgrn.pdxrail.theme.LocalAppDimensions
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun HelpDialog(
    onDismiss: () -> Unit,
    isMyLocationEnabled: Boolean = false,
    onRequestLocationPermission: () -> Unit = {},
) {
    val dimens = LocalAppDimensions.current
    AlertDialog(
        onDismissRequest = onDismiss,
        title = null,
        text = {
            Column {
                Text(
                    modifier = Modifier.padding(bottom = dimens.itemSpacing),
                    text = stringResource(Res.string.help_title),
                    style = MaterialTheme.typography.bodyMedium,
                )
                Row(modifier = Modifier.padding(horizontal = dimens.paddingMedium, vertical = dimens.itemSpacing)) {
                    Icon(
                        painter = painterResource(Res.drawable.marker_max_stop),
                        contentDescription = stringResource(Res.string.cd_max_stop),
                        tint = Color.Unspecified,
                    )
                    Text(
                        style = MaterialTheme.typography.bodySmall,
                        text = stringResource(Res.string.help_max_stop),
                        modifier = Modifier.padding(start = dimens.paddingMedium),
                    )
                }
                Row(modifier = Modifier.padding(horizontal = dimens.paddingMedium, vertical = dimens.itemSpacing)) {
                    Icon(
                        painter = painterResource(Res.drawable.marker_streetcar_stop),
                        contentDescription = stringResource(Res.string.cd_streetcar_stop),
                        tint = Color.Unspecified,
                    )
                    Text(
                        style = MaterialTheme.typography.bodySmall,
                        text = stringResource(Res.string.help_streetcar_stop),
                        modifier = Modifier.padding(start = dimens.paddingMedium),
                    )
                }
                Row(modifier = Modifier.padding(horizontal = dimens.paddingMedium, vertical = dimens.itemSpacing)) {
                    Icon(
                        painter = painterResource(Res.drawable.marker_max_arrival),
                        contentDescription = stringResource(Res.string.cd_vehicle_arrival),
                        tint = Color.Unspecified,
                    )
                    Text(
                        style = MaterialTheme.typography.labelSmall,
                        text = stringResource(Res.string.help_arrival),
                        modifier = Modifier.padding(start = dimens.paddingMedium),
                    )
                }
                Row(
                    modifier = Modifier
                        .clickable(enabled = !isMyLocationEnabled) { onRequestLocationPermission() }
                        .padding(horizontal = dimens.paddingMedium, vertical = dimens.itemSpacing),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(Modifier.size(dimens.iconSize)) {
                        val strokePx = dimens.paddingXSmall
                        Canvas(modifier = Modifier.size(dimens.iconSizeSmall).align(Alignment.Center)) {
                            val outerRadius = size.minDimension / 2
                            drawCircle(color = Color.White, radius = outerRadius)
                            drawCircle(color = Color(0xFF2196F3), radius = outerRadius - strokePx.toPx())
                        }
                    }
                    Text(
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.weight(1f).padding(start = dimens.paddingMedium),
                        text = stringResource(Res.string.permission_location_reason),
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text(stringResource(Res.string.dialog_help_action))
            }
        },
    )
}
