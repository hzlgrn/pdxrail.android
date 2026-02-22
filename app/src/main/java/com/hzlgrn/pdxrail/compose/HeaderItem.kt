package com.hzlgrn.pdxrail.compose

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterStart
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import com.hzlgrn.pdxrail.R

@Composable
fun HeaderItem(text: String) {
    Box(
        modifier = Modifier
            .padding(
                horizontal = dimensionResource(R.dimen.horizontal_small),
                vertical = dimensionResource(R.dimen.vertical_small)
            ),
        contentAlignment = CenterStart,
    ) {
        Text(
            text,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}