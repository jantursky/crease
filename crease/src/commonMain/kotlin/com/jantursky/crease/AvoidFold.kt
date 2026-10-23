package com.jantursky.crease

import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.node.CompositionLocalConsumerModifierNode
import androidx.compose.ui.node.GlobalPositionAwareModifierNode
import androidx.compose.ui.node.LayoutModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.currentValueOf
import androidx.compose.ui.node.invalidateMeasurement
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.constrainHeight
import androidx.compose.ui.unit.constrainWidth
import androidx.compose.ui.unit.toSize
import com.jantursky.crease.platform.FoldStateSubscription
import com.jantursky.crease.platform.subscribeToPlatformFoldState
import kotlin.math.roundToInt

/**
 * Keeps the element's content on one side of a separating fold. When a fold crosses the element, the
 * element keeps its footprint and lays its content out on the larger side of the fold; otherwise
 * nothing changes.
 */
public fun Modifier.avoidFold(): Modifier = this then AvoidFoldElement

private data object AvoidFoldElement : ModifierNodeElement<AvoidFoldNode>() {
    override fun create() = AvoidFoldNode()

    override fun update(node: AvoidFoldNode) = Unit

    override fun InspectorInfo.inspectableProperties() {
        name = "avoidFold"
    }
}

/**
 * Lays the content out on the larger side of the fold. Along the axis the fold splits, the element keeps
 * the length it had on the previous pass, which is where the fold was found and which therefore stays
 * put; along the other axis it measures as usual.
 */
internal class AvoidFoldNode :
    Modifier.Node(),
    LayoutModifierNode,
    GlobalPositionAwareModifierNode,
    CompositionLocalConsumerModifierNode {
    private var subscription: FoldStateSubscription? = null
    private var windowBounds: Rect? = null

    override fun onAttach() {
        subscription = subscribeToPlatformFoldState(this)
    }

    override fun onDetach() {
        subscription?.release?.invoke()
        subscription = null
        windowBounds = null
    }

    private fun separatingFold(): Fold? =
        (currentValueOf(LocalFoldStateOverride) ?: subscription?.state?.value)?.separatingFold

    override fun MeasureScope.measure(
        measurable: Measurable,
        constraints: Constraints,
    ): MeasureResult {
        val bounds = windowBounds
        val fold = separatingFold()
        if (bounds != null && fold != null) {
            val vertical = fold.orientation == FoldOrientation.Vertical
            val width =
                if (vertical) {
                    constraints.constrainWidth(
                        bounds.width.roundToInt(),
                    )
                } else {
                    bounds.width.roundToInt()
                }
            val height =
                if (vertical) {
                    bounds.height.roundToInt()
                } else {
                    constraints.constrainHeight(
                        bounds.height.roundToInt(),
                    )
                }
            val container = Rect(0f, 0f, width.toFloat(), height.toFloat())
            val side = splitAtFold(container, fold.toLocal(bounds.topLeft))?.larger
            if (side != null) {
                val start = (if (vertical) side.left else side.top).roundToInt()
                val length = (if (vertical) side.right else side.bottom).roundToInt() - start
                val placeable =
                    if (vertical) {
                        measurable.measure(
                            constraints.copy(
                                minWidth = minOf(constraints.minWidth, length),
                                maxWidth = minOf(constraints.maxWidth, length),
                            ),
                        )
                    } else {
                        measurable.measure(
                            constraints.copy(
                                minHeight = minOf(constraints.minHeight, length),
                                maxHeight = minOf(constraints.maxHeight, length),
                            ),
                        )
                    }
                return if (vertical) {
                    layout(width, placeable.height) { placeable.place(start, 0) }
                } else {
                    layout(placeable.width, height) { placeable.place(0, start) }
                }
            }
        }
        val placeable = measurable.measure(constraints)
        return layout(placeable.width, placeable.height) { placeable.place(0, 0) }
    }

    override fun onGloballyPositioned(coordinates: LayoutCoordinates) {
        val bounds = Rect(coordinates.positionInWindow(), coordinates.size.toSize())
        if (bounds == windowBounds) return
        windowBounds = bounds
        // Without a fold the position does not matter, so scrolling past it costs no extra layout.
        if (separatingFold() != null) invalidateMeasurement()
    }
}
