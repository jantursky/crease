package com.jantursky.crease.samples.photos

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import kotlin.math.PI
import kotlin.math.sin

/** Pictures drawn with code, so the samples need no image files and no licences. */
enum class Scene { Dusk, Lake, City, Dunes }

private const val WIDTH = 900
private const val HEIGHT = 600

fun renderScene(scene: Scene): ImageBitmap {
    val image = ImageBitmap(WIDTH, HEIGHT)
    CanvasDrawScope().draw(Density(1f), LayoutDirection.Ltr, Canvas(image), Size(WIDTH.toFloat(), HEIGHT.toFloat())) {
        when (scene) {
            Scene.Dusk -> dusk()
            Scene.Lake -> lake()
            Scene.City -> city()
            Scene.Dunes -> dunes()
        }
    }
    return image
}

private fun DrawScope.sky(vararg stops: Pair<Float, Color>) {
    drawRect(Brush.verticalGradient(colorStops = arrayOf(*stops)))
}

/** A wavy ridge line filled down to the bottom edge. */
private fun DrawScope.ridge(
    color: Color,
    baseline: Float,
    amplitude: Float,
    waves: Float,
    phase: Float,
) {
    val path = Path().apply { moveTo(0f, size.height) }
    var x = 0f
    while (x <= size.width + STEP) {
        val t = x / size.width * 2f * PI.toFloat()
        val wobble = sin(t * waves + phase) + sin(t * waves * 2.3f + phase * 1.7f) / 3f
        path.lineTo(x, size.height * (baseline + amplitude * wobble))
        x += STEP
    }
    path.lineTo(size.width, size.height)
    path.close()
    drawPath(path, color)
}

private const val STEP = 6f

private fun DrawScope.dusk() {
    sky(
        0f to Color(0xFF2B1B4D),
        0.45f to Color(0xFFC2436A),
        0.72f to Color(0xFFF59F4A),
        1f to Color(0xFFFFD9A0),
    )
    drawCircle(Color(0xFFFFF1C9), size.height * 0.1f, Offset(size.width * 0.5f, size.height * 0.62f))
    ridge(Color(0xFF6B2D5C), 0.7f, 0.05f, 1.5f, 0.4f)
    ridge(Color(0xFF4A1F4D), 0.8f, 0.04f, 2.2f, 2.1f)
    ridge(Color(0xFF2A1238), 0.9f, 0.03f, 3f, 4f)
}

private fun DrawScope.lake() {
    val w = size.width
    val h = size.height
    sky(0f to Color(0xFF7FB7E6), 1f to Color(0xFFDFF0FA))
    val peaks = listOf(0.2f to 0.32f, 0.5f to 0.18f, 0.8f to 0.36f)
    peaks.forEach { (x, top) ->
        val mountain =
            Path().apply {
                moveTo(w * (x - 0.34f), h * 0.62f)
                lineTo(w * x, h * top)
                lineTo(w * (x + 0.34f), h * 0.62f)
                close()
            }
        drawPath(mountain, Color(0xFF51627A))
        val snow =
            Path().apply {
                moveTo(w * (x - 0.07f), h * (top + 0.13f))
                lineTo(w * x, h * top)
                lineTo(w * (x + 0.07f), h * (top + 0.13f))
                lineTo(w * (x + 0.03f), h * (top + 0.1f))
                lineTo(w * x, h * (top + 0.14f))
                lineTo(w * (x - 0.03f), h * (top + 0.1f))
                close()
            }
        drawPath(snow, Color(0xFFF4F8FC))
    }
    drawRect(
        Brush.verticalGradient(listOf(Color(0xFF3A6E9A), Color(0xFF1C3B5A)), startY = h * 0.62f, endY = h),
        topLeft = Offset(0f, h * 0.62f),
        size = Size(w, h * 0.38f),
    )
    ridge(Color(0xFF1F3A2E), 0.88f, 0.03f, 2.5f, 1f)
}

private fun DrawScope.city() {
    val w = size.width
    val h = size.height
    sky(0f to Color(0xFF0B1030), 0.6f to Color(0xFF3A2A63), 1f to Color(0xFFD9776A))
    drawCircle(Color(0xFFF5F0DC), h * 0.07f, Offset(w * 0.78f, h * 0.2f))
    var seed = 7

    fun next(): Float {
        seed = (seed * 1103515245 + 12345) and 0x7fffffff
        return (seed % 1000) / 1000f
    }
    val columns = 16
    val bw = w / columns
    for (i in 0 until columns) {
        val top = h * (0.4f + next() * 0.3f)
        drawRect(Color(0xFF0A0A1A), Offset(i * bw, top), Size(bw - 3f, h - top))
        var y = top + 14f
        while (y < h - 20f) {
            var x = i * bw + 8f
            while (x < (i + 1) * bw - 14f) {
                if (next() > 0.45f) drawRect(Color(0xFFFFD27A), Offset(x, y), Size(6f, 9f))
                x += 16f
            }
            y += 22f
        }
    }
}

private fun DrawScope.dunes() {
    sky(0f to Color(0xFFF4D8B0), 1f to Color(0xFFFBEEDD))
    drawCircle(Color(0xFFFFF7E6), size.height * 0.08f, Offset(size.width * 0.3f, size.height * 0.25f))
    ridge(Color(0xFFE0A96D), 0.55f, 0.05f, 1f, 0.5f)
    ridge(Color(0xFFC98A4F), 0.68f, 0.05f, 1.3f, 2f)
    ridge(Color(0xFFA96A36), 0.8f, 0.045f, 1.6f, 3.4f)
    ridge(Color(0xFF7D4A26), 0.92f, 0.03f, 2f, 5f)
}
