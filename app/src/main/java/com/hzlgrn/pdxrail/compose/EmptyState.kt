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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.hzlgrn.pdxrail.R

@Composable
private fun ArrivalEmptyViewCard() {
    val textMax = stringResource(id = R.string.content_description_max_stop_verbose)
    val textStreetcar = stringResource(id = R.string.content_description_streetcar_stop_verbose)
    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 4.dp, top = 3.dp, bottom = 3.dp, end = 8.dp)
        ) {
            Card(
                modifier = Modifier
                    .clickable {}
                    .fillMaxWidth(),
                shape = RoundedCornerShape(6.dp)
            ) {
                Row (verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(
                            id = R.drawable.marker_max_stop
                        ),
                        contentDescription = "Max stop",
                        modifier = Modifier
                            .height(36.dp)
                            .width(36.dp)
                    )
                    Text(
                        text = textMax,
                        modifier = Modifier.padding(top = 3.dp, bottom = 3.dp)
                    )
                }
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 4.dp, top = 3.dp, bottom = 3.dp, end = 8.dp)
        ) {
            Card(
                modifier = Modifier
                    .clickable {}
                    .fillMaxWidth(),
                shape = RoundedCornerShape(6.dp)
            ) {
                Row (verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(
                            id = R.drawable.marker_streetcar_stop
                        ),
                        contentDescription = "Streetcar stop",
                        modifier = Modifier
                            .height(36.dp)
                            .width(36.dp)
                    )
                    Text(
                        text = textStreetcar,
                        modifier = Modifier.padding(top = 3.dp, bottom = 3.dp)
                    )
                }

            }
        }
    }
}