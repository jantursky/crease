package com.jantursky.crease.samples.web

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jantursky.crease.samples.design.CreaseTheme
import com.jantursky.crease.samples.design.DeviceSimulator
import com.jantursky.crease.samples.design.SegmentedControl
import com.jantursky.crease.samples.design.rememberSimulatorState
import com.jantursky.crease.samples.photos.PhotosApp
import com.jantursky.crease.samples.recipes.RecipesApp

internal enum class Demo(
    val label: String,
) {
    Recipes("Recipes"),
    Photos("Photos"),
}

/** The playground: both sample apps in a simulated foldable with every control. */
@Composable
fun DemoSite() {
    val start = remember { StartParams.fromUrl() }
    var demo by remember { mutableStateOf(start.demo) }
    val simulator = rememberSimulatorState().also { start.applyTo(it) }
    Column(
        Modifier.fillMaxSize().background(CreaseTheme.colors.band),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        SegmentedControl(
            options = Demo.entries,
            selected = demo,
            onSelect = { demo = it },
            label = { it.label },
            modifier = Modifier.padding(top = 16.dp).width(240.dp),
        )
        DeviceSimulator(Modifier.weight(1f).fillMaxWidth(), simulator) {
            when (demo) {
                Demo.Recipes -> RecipesApp()
                Demo.Photos -> PhotosApp()
            }
        }
    }
}
