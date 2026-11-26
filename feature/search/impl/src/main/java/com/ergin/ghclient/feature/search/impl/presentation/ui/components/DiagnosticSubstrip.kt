package com.ergin.ghclient.feature.search.impl.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ergin.ghclient.ui.theme.GHClientTheme

@Composable
fun DiagnosticSubstrip(
    modifier: Modifier = Modifier,
    selectorTitle: String = "СЕЛЕКТОР: РЕПОЗИТОРИИ (ТАКТ: 0x88)",
    indexTag: String = "ИНДЕКС: #092-К"
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceContainerLowest)
            .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "● ", color = MaterialTheme.colorScheme.primary, fontSize = 10.sp)
            Text(
                text = selectorTitle,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Text(
            text = indexTag,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.secondary
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DiagnosticSubstripPreview() {
    GHClientTheme {
        DiagnosticSubstrip()
    }
}
