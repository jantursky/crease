package com.jantursky.crease.samples.recipes

import androidx.compose.ui.window.ComposeUIViewController
import com.jantursky.crease.samples.design.CreaseTheme

@Suppress("FunctionName", "unused")
fun MainViewController() = ComposeUIViewController { CreaseTheme { RecipesApp() } }
