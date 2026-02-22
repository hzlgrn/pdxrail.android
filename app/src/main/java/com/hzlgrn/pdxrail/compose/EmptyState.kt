package com.hzlgrn.pdxrail.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.hzlgrn.pdxrail.R
import com.hzlgrn.pdxrail.theme.PdxRailTheme

@Composable
fun ArrivalEmptyMaxViewCard() {
    val textMax = stringResource(id = R.string.content_description_max_stop_verbose)
        Box(modifier = Modifier.fillMaxWidth()) {
            Card(
                modifier = Modifier
                    .clickable { /* just ripples */ }
                    .fillMaxWidth(),
                shape = RoundedCornerShape(dimensionResource(androidx.cardview.R.dimen.cardview_default_radius))
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(
                            horizontal = dimensionResource(R.dimen.horizontal),
                            vertical = dimensionResource(R.dimen.vertical)
                        ),
                ) {
                    Image(
                        painter = painterResource(
                            id = R.drawable.marker_max_stop
                        ),
                        contentDescription = "Max stop",
                        modifier = Modifier
                            .height(dimensionResource(R.dimen.ic_size_large))
                            .width(dimensionResource(R.dimen.ic_size_large))
                    )
                    Text(
                        style = MaterialTheme.typography.bodyMedium,
                        text = textMax,
                    )
                }
            }
        }
}

@Composable
fun ArrivalEmptyStreetcarViewCard() {
    val textStreetcar = stringResource(id = R.string.content_description_streetcar_stop_verbose)
    Box(modifier = Modifier.fillMaxWidth()) {
        Card(
            modifier = Modifier
                .clickable { /* just ripples */ }
                .fillMaxWidth(),
            shape = RoundedCornerShape(dimensionResource(R.dimen.card_radius_small)),
        ) {
            Row (
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(
                        horizontal = dimensionResource(R.dimen.horizontal),
                        vertical = dimensionResource(R.dimen.vertical)
                    ),
            ) {
                Image(
                    painter = painterResource(
                        id = R.drawable.marker_streetcar_stop
                    ),
                    contentDescription = "Streetcar stop",
                    modifier = Modifier
                        .height(dimensionResource(R.dimen.ic_size_large))
                        .width(dimensionResource(R.dimen.ic_size_large))
                )
                Text(
                    style = MaterialTheme.typography.bodyMedium,
                    text = textStreetcar,
                )
            }

        }
    }
}

@Preview
@Composable
fun PreviewEmptyStates() {
    PdxRailTheme {
        Column {
            ArrivalEmptyMaxViewCard()
            ArrivalEmptyStreetcarViewCard()
        }
    }
}