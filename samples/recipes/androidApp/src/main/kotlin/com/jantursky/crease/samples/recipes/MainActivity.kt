package com.jantursky.crease.samples.recipes.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.jantursky.crease.samples.design.CreaseTheme
import com.jantursky.crease.samples.recipes.RecipesApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent { CreaseTheme { RecipesApp() } }
    }
}
