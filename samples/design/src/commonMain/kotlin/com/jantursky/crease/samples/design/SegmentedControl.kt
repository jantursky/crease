package com.jantursky.crease.samples.design

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun <T> SegmentedControl(
    options: List<T>,
    selected: T,
    onSelect: (T) -> Unit,
    modifier: Modifier = Modifier,
    label: (T) -> String = { it.toString() },
) {
    val colors = CreaseTheme.colors
    Row(
        modifier =
            modifier
                .height(IntrinsicSize.Min)
                .clip(CreaseShapes.pill)
                .background(colors.control)
                .padding(2.dp),
    ) {
        options.forEach { option ->
            val isSelected = option == selected
            Box(
                contentAlignment = Alignment.Center,
                modifier =
                    Modifier
                        .weight(1f)
                        .clip(CreaseShapes.pill)
                        .then(
                            if (isSelected) {
                                Modifier
                                    .background(colors.card)
                                    .border(1.dp, colors.hairline, CreaseShapes.pill)
                            } else {
                                Modifier
                            },
                        ).selectable(isSelected, role = Role.Tab, onClick = { onSelect(option) })
                        .padding(horizontal = 14.dp, vertical = 7.dp),
            ) {
                CreaseText(
                    text = label(option),
                    style = CreaseTheme.type.label,
                    color = if (isSelected) colors.ink else colors.slate,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                )
            }
        }
    }
}
