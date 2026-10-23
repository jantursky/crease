package com.jantursky.crease.samples.design

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** `card` surface with the 28 radius. No border, no shadow; it reads as raised on a `band`. */
@Composable
fun CreaseCard(
    modifier: Modifier = Modifier,
    padding: Dp = 20.dp,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier =
            modifier
                .clip(CreaseShapes.card)
                .background(CreaseTheme.colors.card)
                .padding(padding),
        content = content,
    )
}
