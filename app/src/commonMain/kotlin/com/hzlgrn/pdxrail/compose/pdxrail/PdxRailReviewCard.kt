package com.hzlgrn.pdxrail.compose.pdxrail

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.hzlgrn.pdxrail.generated.resources.Res
import com.hzlgrn.pdxrail.generated.resources.action_review
import com.hzlgrn.pdxrail.generated.resources.gratitude
import com.hzlgrn.pdxrail.generated.resources.version
import com.hzlgrn.pdxrail.platformVersionName
import com.hzlgrn.pdxrail.theme.LocalAppDimensions
import org.jetbrains.compose.resources.stringResource

@Composable
fun PdxRailReviewCard(modifier: Modifier = Modifier, onReviewClick: () -> Unit) {
    val dimens = LocalAppDimensions.current
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        modifier = modifier,
        shape = RoundedCornerShape(dimens.cornerRadiusMedium),
    ) {
        Box(modifier = Modifier.padding(dimens.paddingMedium)) {
            Column {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        modifier = Modifier.fillMaxWidth().padding(bottom = dimens.paddingSmall),
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        text = stringResource(Res.string.gratitude),
                    )
                }
                Row {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = dimens.paddingSmall)
                            .align(alignment = Alignment.CenterVertically)
                    ) {
                        Text(
                            style = MaterialTheme.typography.labelSmall,
                            text = stringResource(Res.string.version, platformVersionName),
                            textAlign = TextAlign.End,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = dimens.paddingSmall)
                            .align(alignment = Alignment.CenterVertically)
                    ) {
                        Button(
                            content = {
                                Text(
                                    style = MaterialTheme.typography.bodyMedium,
                                    text = stringResource(Res.string.action_review),
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
