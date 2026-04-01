package com.hzlgrn.pdxrail.compose.pdxrail

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hzlgrn.pdxrail.BuildConfig
import com.hzlgrn.pdxrail.R
import com.hzlgrn.pdxrail.theme.PdxRailTheme

@Composable
fun PdxRailReviewCard(modifier: Modifier = Modifier, onReviewClick: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        modifier = modifier,
        shape = RoundedCornerShape(dimensionResource(R.dimen.card_radius_medium)),
    ) {
        Box(modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.horizontal_2x), vertical = dimensionResource(R.dimen.vertical_2x))) {
            Column {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        modifier = Modifier.fillMaxWidth().padding(bottom = dimensionResource(R.dimen.vertical)),
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        text = stringResource(R.string.gratitude),
                    )
                }
                Row {
                    Column(modifier = Modifier
                        .weight(1f)
                        .padding(end = dimensionResource(R.dimen.horizontal_small))
                        .align(alignment = Alignment.CenterVertically)
                    ) {
                        Row {
                            Text(
                                style = MaterialTheme.typography.labelSmall,
                                text = stringResource(R.string.version, BuildConfig.VERSION_NAME),
                                textAlign = TextAlign.End,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                    Column(modifier = Modifier
                        .weight(1f)
                        .padding(start = dimensionResource(R.dimen.horizontal_small))
                        .align(alignment = Alignment.CenterVertically)
                    ) {
                        Button(
                            content = {
                                Text(
                                    style = MaterialTheme.typography.bodyMedium,
                                    text = stringResource(R.string.action_play_store),
                                )
                            },
                            onClick = onReviewClick,
                            modifier = Modifier.align(alignment = Alignment.Start),
                        )
                    }
                }
            }
        }
    }
}

@Composable
@Preview
fun PreviewPdxRailReviewCard() {
    PdxRailTheme {
        PdxRailReviewCard(modifier = Modifier.width(300.dp), onReviewClick = {})
    }
}