package com.jantursky.crease.samples.web

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.jantursky.crease.samples.design.CreaseShapes
import com.jantursky.crease.samples.design.CreaseText
import com.jantursky.crease.samples.design.CreaseTheme
import com.jantursky.crease.samples.design.DeviceSimulator
import com.jantursky.crease.samples.design.StatusLabel
import com.jantursky.crease.samples.design.rememberSimulatorState
import com.jantursky.crease.samples.photos.PhotosApp
import com.jantursky.crease.samples.recipes.RecipesApp

private const val DOCS_URL = "https://crease.vercel.app"
private const val GITHUB_URL = "https://github.com/jantursky/crease"

internal enum class Demo(
    val label: String,
) {
    Recipes("Recipes"),
    Photos("Photos"),
}

@Composable
fun DemoSite() {
    val start = remember { StartParams.fromUrl() }
    var demo by remember { mutableStateOf(start.demo) }
    val simulator = rememberSimulatorState().also { start.applyTo(it) }
    Column(Modifier.fillMaxSize().background(CreaseTheme.colors.canvas)) {
        TopNav(demo, onSelect = { demo = it })
        DeviceSimulator(Modifier.weight(1f).fillMaxWidth(), simulator) {
            when (demo) {
                Demo.Recipes -> RecipesApp()
                Demo.Photos -> PhotosApp()
            }
        }
    }
}

@Composable
private fun TopNav(
    selected: Demo,
    onSelect: (Demo) -> Unit,
) {
    val colors = CreaseTheme.colors
    val uri = LocalUriHandler.current
    Column {
        Row(
            Modifier.fillMaxWidth().height(44.dp).padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            CreaseText("Crease", style = CreaseTheme.type.label.copy(fontSize = CreaseTheme.type.body.fontSize))
            StatusLabel("Demo")
            Demo.entries.forEach { demo ->
                NavLink(demo.label, active = demo == selected) { onSelect(demo) }
            }
            Box(Modifier.weight(1f))
            NavLink("Docs", active = false, color = colors.link) { uri.openUri(DOCS_URL) }
            NavLink("GitHub", active = false, color = colors.link) { uri.openUri(GITHUB_URL) }
        }
        Box(Modifier.fillMaxWidth().height(1.dp).background(colors.hairline))
    }
}

@Composable
private fun NavLink(
    text: String,
    active: Boolean,
    color: androidx.compose.ui.graphics.Color = CreaseTheme.colors.ink,
    onClick: () -> Unit,
) {
    val colors = CreaseTheme.colors
    CreaseText(
        text = text,
        style = CreaseTheme.type.bodySmall,
        color =
            if (active) {
                colors.ink
            } else if (color == colors.ink) {
                colors.slate
            } else {
                color
            },
        modifier = Modifier.clip(CreaseShapes.link).clickable(role = Role.Button, onClick = onClick).padding(4.dp),
    )
}
