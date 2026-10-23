package com.jantursky.crease.samples.photos

import androidx.compose.ui.window.ComposeUIViewController
import com.jantursky.crease.samples.design.CreaseTheme

@Suppress("FunctionName", "unused")
fun MainViewController() = ComposeUIViewController { CreaseTheme { PhotosApp() } }
