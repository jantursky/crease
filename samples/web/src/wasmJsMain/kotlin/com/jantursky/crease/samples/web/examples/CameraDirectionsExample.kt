package com.jantursky.crease.samples.web.examples

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jantursky.crease.avoidFold
import com.jantursky.crease.cover.CameraDescriptor
import com.jantursky.crease.cover.CameraDirections
import com.jantursky.crease.cover.CameraPosition
import com.jantursky.crease.cover.ProvideCameraDirections
import com.jantursky.crease.cover.rememberCameraDirections
import com.jantursky.crease.samples.design.CreaseText
import com.jantursky.crease.samples.design.CreaseTheme
import com.jantursky.crease.samples.design.SegmentedControl
import com.jantursky.crease.samples.design.StatusLabel

private val Wide = CameraDescriptor("wide", "Wide camera", "builtInWideAngleCamera", CameraPosition.Back)
private val Selfie = CameraDescriptor("selfie", "Selfie camera", "builtInWideAngleCamera", CameraPosition.Front)

/** Where the app runs, and so which cameras face the viewer. */
private enum class Where(
    val label: String,
    val directions: CameraDirections?,
) {
    Unknown("Unknown", null),
    Inner("Inner display", CameraDirections(forwardFacing = listOf(Selfie), backwardFacing = listOf(Wide))),

    // On the iPhone Duo the camera that faces away from the inner display faces the viewer on the outer one.
    Outer("Outer display", CameraDirections(forwardFacing = listOf(Wide), backwardFacing = listOf(Selfie))),
}

/**
 * Which camera to use for a selfie. A browser cannot tell, so the answer is chosen here with
 * [ProvideCameraDirections]. On the iPhone Duo with iOS 27.1, [rememberCameraDirections] answers for real.
 */
@Composable
fun CameraDirectionsExample() {
    var selected by remember { mutableStateOf(Where.Outer) }
    Column(
        Modifier.fillMaxSize().avoidFold().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        SegmentedControl(
            options = Where.entries,
            selected = selected,
            onSelect = { selected = it },
            modifier = Modifier.fillMaxWidth(),
            label = { it.label },
        )
        ProvideCameraDirections(selected.directions) { SelfiePicker() }
    }
}

@Composable
private fun SelfiePicker() {
    val directions = rememberCameraDirections()
    val facing = directions?.forwardFacing?.firstOrNull()
    StatusLabel(facing?.name ?: "No answer")
    CreaseText(
        text =
            when {
                directions == null -> "The platform cannot tell. Fall back to the front camera."
                facing == null -> "No camera faces the viewer yet. Wait for the system to report."
                else -> "Use ${facing.name} (${facing.position.name}) to face the viewer."
            },
        style = CreaseTheme.type.bodySmall,
        color = CreaseTheme.colors.slate,
    )
}
