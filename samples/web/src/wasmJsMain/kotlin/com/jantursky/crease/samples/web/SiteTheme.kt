package com.jantursky.crease.samples.web

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import com.jantursky.crease.samples.design.CreaseColors
import com.jantursky.crease.samples.design.CreaseTheme
import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.MutationObserver
import org.w3c.dom.MutationObserverInit

// The site's warm paper palette, the Compose side of the --paper-* variables in site.css.
private val PaperLight =
    CreaseColors(
        canvas = Color(0xFFFCFCF9),
        band = Color(0xFFF4F4EE),
        raised = Color(0xFFF3F3EE),
        card = Color(0xFFFFFFFF),
        hairline = Color(0xFFD7D7D6),
        control = Color(0xFFE6E6DF),
        ink = Color(0xFF181818),
        slate = Color(0xFF666666),
        steel = Color(0xFFA8A8A8),
        link = Color(0xFF3F6FBF),
        action = Color(0xFF181818),
        signal = Color(0xFF3F6FBF),
        fold = Color(0x245D8CD7),
        isDark = false,
        onAction = Color(0xFFFCFCF9),
    )

private val PaperDark =
    CreaseColors(
        canvas = Color(0xFF1C1C1A),
        band = Color(0xFF161614),
        raised = Color(0xFF232320),
        card = Color(0xFF232320),
        hairline = Color(0xFF3A3A36),
        control = Color(0xFF30302C),
        ink = Color(0xFFEDEDE6),
        slate = Color(0xFFA3A39C),
        steel = Color(0xFF7A7A74),
        link = Color(0xFF81ADEC),
        action = Color(0xFFEDEDE6),
        signal = Color(0xFF81ADEC),
        fold = Color(0x3381ADEC),
        isDark = true,
        onAction = Color(0xFF181818),
    )

/**
 * Whether the page is dark: `data-theme` on the root element when the switch has set it, otherwise the
 * system setting. Shared by every viewport on the page and updated when either changes.
 */
private val pageIsDark = mutableStateOf(readPageIsDark())

private fun readPageIsDark(): Boolean =
    when (document.documentElement?.getAttribute("data-theme")) {
        "dark" -> true
        "light" -> false
        else -> window.matchMedia("(prefers-color-scheme: dark)").matches
    }

internal fun watchPageTheme() {
    val update = { pageIsDark.value = readPageIsDark() }
    document.documentElement?.let { root ->
        MutationObserver { _, _ -> update() }
            .observe(
                root,
                MutationObserverInit(
                    attributes = true,
                    attributeFilter = arrayOf("data-theme".toJsString()).toJsArray(),
                ),
            )
    }
    window.matchMedia("(prefers-color-scheme: dark)").addEventListener("change", { update() })
}

@Composable
internal fun SiteTheme(content: @Composable () -> Unit) {
    val dark = pageIsDark.value
    CreaseTheme(darkTheme = dark, colors = if (dark) PaperDark else PaperLight, content = content)
}
