package com.jantursky.crease.samples.design

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.jantursky.crease.Posture

@Stable
class SimulatorState {
    var posture by mutableStateOf(Posture.Book)
    var angle by mutableFloatStateOf(110f)
    var foldWidthDp by mutableIntStateOf(0)
    var cutout by mutableStateOf(false)
    var overlay by mutableStateOf(false)

    /** The angle slider only means something while the device is half open. */
    val isHalfOpen: Boolean get() = posture == Posture.Book || posture == Posture.Tabletop

    companion object {
        const val MIN_ANGLE = 20f
        const val MAX_ANGLE = 170f
    }
}

@Composable
fun rememberSimulatorState(): SimulatorState = remember { SimulatorState() }
