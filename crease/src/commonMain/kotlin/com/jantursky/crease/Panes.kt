package com.jantursky.crease

import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.layout.SubcomposeMeasureScope
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.constrainHeight
import androidx.compose.ui.unit.constrainWidth
import kotlin.math.roundToInt

/** A pane's content, measured for its bounds. */
internal class MeasuredPane(
    private val offset: IntOffset,
    width: Int,
    height: Int,
    private val placeables: List<Placeable>,
) {
    val right: Int = offset.x + width
    val bottom: Int = offset.y + height

    fun place(scope: Placeable.PlacementScope) = with(scope) { placeables.forEach { it.place(offset) } }
}

/**
 * Subcomposes [content] under [slotId] and measures it to fill [bounds], snapped to whole pixels. An
 * infinite edge leaves that length to the content.
 */
internal fun SubcomposeMeasureScope.measurePane(
    slotId: Any,
    bounds: Rect,
    content: @Composable () -> Unit,
): MeasuredPane {
    val left = bounds.left.roundToInt()
    val top = bounds.top.roundToInt()
    val width = if (bounds.right.isFinite()) bounds.right.roundToInt() - left else null
    val height = if (bounds.bottom.isFinite()) bounds.bottom.roundToInt() - top else null
    val constraints =
        Constraints(
            minWidth = width ?: 0,
            maxWidth = width ?: Constraints.Infinity,
            minHeight = height ?: 0,
            maxHeight = height ?: Constraints.Infinity,
        )
    val placeables = subcompose(slotId, content).map { it.measure(constraints) }
    return MeasuredPane(
        offset = IntOffset(left, top),
        width = width ?: placeables.maxOfOrNull { it.width } ?: 0,
        height = height ?: placeables.maxOfOrNull { it.height } ?: 0,
        placeables = placeables,
    )
}

/** Lays out [panes] filling [constraints] where they are bounded, and wrapping the panes where not. */
internal fun SubcomposeMeasureScope.layoutPanes(
    constraints: Constraints,
    panes: List<MeasuredPane>,
): MeasureResult {
    val width =
        if (constraints.hasBoundedWidth) {
            constraints.maxWidth
        } else {
            constraints.constrainWidth(
                panes.maxOf {
                    it.right
                },
            )
        }
    val height =
        if (constraints.hasBoundedHeight) {
            constraints.maxHeight
        } else {
            constraints.constrainHeight(
                panes.maxOf {
                    it.bottom
                },
            )
        }
    return layout(width, height) { panes.forEach { it.place(this) } }
}
