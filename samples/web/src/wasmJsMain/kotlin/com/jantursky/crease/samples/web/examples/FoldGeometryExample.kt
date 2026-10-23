package com.jantursky.crease.samples.web.examples

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jantursky.crease.avoidFold
import com.jantursky.crease.rememberFoldState
import com.jantursky.crease.samples.design.CreaseText
import com.jantursky.crease.samples.design.CreaseTheme
import kotlin.math.roundToInt

/** Everything Crease reports about the fold, as plain values. */
@Composable
fun FoldGeometryExample() {
    val state = rememberFoldState()
    val fold = state.fold
    Column(
        Modifier.fillMaxSize().avoidFold().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        CreaseText("FoldState", style = CreaseTheme.type.title)
        Value("posture", state.posture.name)
        Value("hinge", state.hinge?.let { "${it.status.name}, ${it.angle?.roundToInt()}°" } ?: "none")
        Value("folds", state.folds.size.toString())
        if (fold != null) {
            val bounds = fold.bounds
            Value("orientation", fold.orientation.name)
            Value("bounds", "${bounds.width.roundToInt()} × ${bounds.height.roundToInt()} px")
            Value("separating", fold.isSeparating.toString())
            Value("occluding", fold.isOccluding.toString())
        }
        Value("cutouts", state.cutouts.size.toString())
    }
}

@Composable
private fun Value(
    name: String,
    value: String,
) {
    Row {
        CreaseText(
            name,
            style = CreaseTheme.type.bodySmall,
            color = CreaseTheme.colors.slate,
            modifier = Modifier.width(96.dp),
        )
        CreaseText(value, style = CreaseTheme.type.bodySmall)
    }
}
