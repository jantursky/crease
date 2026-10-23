package com.jantursky.crease.samples.recipes

import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.jantursky.crease.samples.design.CreaseTheme
import com.jantursky.crease.samples.design.DeviceSimulator

fun main() =
    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "Crease recipes",
            state = rememberWindowState(size = DpSize(1180.dp, 940.dp)),
        ) {
            CreaseTheme {
                DeviceSimulator { RecipesApp() }
            }
        }
    }
