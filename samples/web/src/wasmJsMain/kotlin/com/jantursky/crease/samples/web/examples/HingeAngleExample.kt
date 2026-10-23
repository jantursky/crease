package com.jantursky.crease.samples.web.examples

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jantursky.crease.avoidFold
import com.jantursky.crease.rememberFoldState
import com.jantursky.crease.rememberHinge
import com.jantursky.crease.samples.design.CreaseText
import com.jantursky.crease.samples.design.CreaseTheme
import com.jantursky.crease.samples.design.StatusLabel
import kotlin.math.roundToInt

/** The raw readings: the hinge angle, its status and the posture Crease derives from them. */
@Composable
fun HingeAngleExample() {
    val hinge = rememberHinge()
    val posture = rememberFoldState().posture
    Column(
        Modifier.fillMaxSize().avoidFold().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        StatusLabel(posture.name)
        CreaseText(
            text = hinge?.angle?.let { "${it.roundToInt()}°" } ?: "No hinge",
            style = CreaseTheme.type.numeric.copy(fontSize = 56.sp, lineHeight = 60.sp),
        )
        CreaseText(
            text = hinge?.status?.name ?: "This device does not fold",
            style = CreaseTheme.type.bodySmall,
            color = CreaseTheme.colors.slate,
        )
    }
}
