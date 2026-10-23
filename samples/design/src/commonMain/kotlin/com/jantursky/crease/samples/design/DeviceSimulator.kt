package com.jantursky.crease.samples.design

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.jantursky.crease.FoldOrientation
import com.jantursky.crease.FoldState
import com.jantursky.crease.ProvideFoldState
import kotlin.math.min

private val Bezel = 10.dp
private const val CUTOUT_DIAMETER_DP = 14

/**
 * Shows [content] inside a simulated foldable and feeds it a matching [FoldState], so the layouts
 * can be tried on desktop and in the browser. The frame shrinks to fit the space it gets.
 */
@Composable
fun DeviceSimulator(
    modifier: Modifier = Modifier,
    state: SimulatorState = rememberSimulatorState(),
    content: @Composable () -> Unit,
) {
    val colors = CreaseTheme.colors
    Column(modifier.background(colors.band)) {
        BoxWithConstraints(
            modifier = Modifier.weight(1f).fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
            contentAlignment = Alignment.Center,
        ) {
            val screen = screenSize(state.posture)
            val outerWidth = screen.width + Bezel * 2
            val outerHeight = screen.height + Bezel * 2
            val scale = min(1f, min(maxWidth / outerWidth, maxHeight / outerHeight))
            val density = LocalDensity.current
            // Scaling the density keeps window pixels, fold bounds and layout dp consistent.
            CompositionLocalProvider(LocalDensity provides Density(density.density * scale, density.fontScale)) {
                DeviceFrame(state, content)
            }
        }
        SimulatorControls(
            state = state,
            modifier =
                Modifier
                    .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                    .fillMaxWidth()
                    .clip(CreaseShapes.floating)
                    .background(colors.card)
                    .padding(16.dp),
        )
    }
}

@Composable
private fun DeviceFrame(
    state: SimulatorState,
    content: @Composable () -> Unit,
) {
    val colors = CreaseTheme.colors
    val density = LocalDensity.current
    val screen = screenSize(state.posture)
    val bezelColor = if (colors.isDark) colors.control else colors.ink
    var origin by remember { mutableStateOf(Offset.Zero) }

    val windowSize = with(density) { Size(screen.width.toPx(), screen.height.toPx()) }
    val diameter = CUTOUT_DIAMETER_DP * density.density
    val local =
        simulatedFoldState(
            posture = state.posture,
            angle = state.angle,
            windowSize = windowSize,
            foldWidth = state.foldWidthDp * density.density,
            cutout = if (state.cutout) cutoutRect(state.posture, windowSize, diameter) else null,
        )
    val inWindow = remember(local, origin) { local.translatedBy(origin) }

    Box(
        Modifier
            .size(screen.width + Bezel * 2, screen.height + Bezel * 2)
            .clip(CreaseShapes.card)
            .background(bezelColor)
            .padding(Bezel),
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .clip(CreaseShapes.floating)
                .background(colors.canvas)
                .onGloballyPositioned { origin = it.positionInWindow() },
        ) {
            ProvideFoldState(inWindow, content)
            Canvas(Modifier.fillMaxSize()) { drawHardware(local, bezelColor, colors.hairline, diameter) }
            if (state.overlay) {
                Canvas(Modifier.fillMaxSize()) { drawOverlay(local, colors.fold, colors.link) }
            }
        }
    }
}

/** What the device itself covers: the gap between the panels and the camera housing. */
private fun DrawScope.drawHardware(
    state: FoldState,
    bezelColor: Color,
    seam: Color,
    cutoutDiameter: Float,
) {
    state.folds.filter { it.isSeparating }.forEach { fold ->
        val bounds = fold.bounds
        if ((bounds.width > 0f && fold.orientation == FoldOrientation.Vertical) ||
            (bounds.height > 0f && fold.orientation == FoldOrientation.Horizontal)
        ) {
            drawRect(bezelColor, bounds.topLeft, bounds.size)
        } else if (fold.orientation == FoldOrientation.Vertical) {
            drawLine(seam, Offset(bounds.left, 0f), Offset(bounds.left, size.height), 1f)
        } else {
            drawLine(seam, Offset(0f, bounds.top), Offset(size.width, bounds.top), 1f)
        }
    }
    state.cutouts.forEach { drawCircle(Color.Black, cutoutDiameter / 2, it.center) }
}

/** The fold and cutout bounds as the library reports them. */
private fun DrawScope.drawOverlay(
    state: FoldState,
    fill: Color,
    edge: Color,
) {
    val stroke = Stroke(1.dp.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 4f)))
    val bounds = state.folds.map { it.bounds } + state.cutouts
    bounds.forEach { rect ->
        drawRect(fill, rect.topLeft, rect.size)
        drawRect(edge, rect.topLeft, rect.size, style = stroke)
    }
}
