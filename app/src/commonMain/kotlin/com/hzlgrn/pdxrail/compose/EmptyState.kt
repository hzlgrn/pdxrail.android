package com.hzlgrn.pdxrail.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.hzlgrn.pdxrail.generated.resources.Res
import com.hzlgrn.pdxrail.generated.resources.arrival_none_incoming
import com.hzlgrn.pdxrail.generated.resources.cd_max_stop
import com.hzlgrn.pdxrail.generated.resources.cd_streetcar_stop
import com.hzlgrn.pdxrail.generated.resources.empty_state_max
import com.hzlgrn.pdxrail.generated.resources.empty_state_streetcar
import com.hzlgrn.pdxrail.generated.resources.marker_max_stop
import com.hzlgrn.pdxrail.generated.resources.marker_streetcar_stop
import com.hzlgrn.pdxrail.theme.LocalAppDimensions
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun ArrivalEmptyMaxViewCard() {
    val dimens = LocalAppDimensions.current
    Box(modifier = Modifier.fillMaxWidth()) {
        Card(
            modifier = Modifier.clickable { }.fillMaxWidth(),
            shape = RoundedCornerShape(dimens.cornerRadiusSmall),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = dimens.paddingMedium, vertical = dimens.itemSpacing),
            ) {
                Icon(
                    painter = painterResource(Res.drawable.marker_max_stop),
                    contentDescription = stringResource(Res.string.cd_max_stop),
                    tint = Color.Unspecified,
                    modifier = Modifier.height(dimens.iconSize).width(dimens.iconSize),
                )
                Text(
                    style = MaterialTheme.typography.bodyMedium,
                    text = stringResource(Res.string.empty_state_max),
                )
            }
        }
    }
}

@Composable
fun ArrivalEmptyStreetcarViewCard() {
    val dimens = LocalAppDimensions.current
    Box(modifier = Modifier.fillMaxWidth()) {
        Card(
            modifier = Modifier.clickable { }.fillMaxWidth(),
            shape = RoundedCornerShape(dimens.cornerRadiusSmall),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = dimens.paddingMedium, vertical = dimens.itemSpacing),
            ) {
                Icon(
                    painter = painterResource(Res.drawable.marker_streetcar_stop),
                    contentDescription = stringResource(Res.string.cd_streetcar_stop),
                    tint = Color.Unspecified,
                    modifier = Modifier.height(dimens.iconSize).width(dimens.iconSize),
                )
                Text(
                    style = MaterialTheme.typography.bodyMedium,
                    text = stringResource(Res.string.empty_state_streetcar),
                )
            }
        }
    }
}

@Composable
fun ArrivalEmptyCard() {
    val dimens = LocalAppDimensions.current
    Box(modifier = Modifier.fillMaxWidth()) {
        Card(
            modifier = Modifier.clickable { }.fillMaxWidth(),
            shape = RoundedCornerShape(dimens.cornerRadiusSmall),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = dimens.paddingMedium, vertical = dimens.itemSpacing),
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    modifier = Modifier.height(dimens.iconSize).width(dimens.iconSize),
                )
                Text(
                    style = MaterialTheme.typography.bodyMedium,
                    text = stringResource(Res.string.arrival_none_incoming),
                )
            }
        }
    }
}
