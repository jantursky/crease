package com.jantursky.crease.samples.web

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import kotlinx.browser.document
import org.w3c.dom.Element
import org.w3c.dom.asList

/** Mounts the playground and every example into the elements index.html sets aside for them. */
@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    watchPageTheme()
    document.getElementById("playground")?.let { element ->
        ComposeViewport(element) { SiteTheme { DemoSite() } }
    }
    document.querySelectorAll("[data-example]").asList().filterIsInstance<Element>().forEach { element ->
        val example = SiteExample.byId(element.getAttribute("data-example")) ?: return@forEach
        ComposeViewport(element) { SiteTheme { ExampleFrame(example) } }
    }
}
