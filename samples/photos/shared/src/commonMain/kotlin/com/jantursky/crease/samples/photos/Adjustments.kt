package com.jantursky.crease.samples.photos

import androidx.compose.ui.graphics.ColorMatrix
import kotlin.math.pow

/** Edit sliders, each from -1 to 1 with 0 meaning untouched. */
data class Adjustments(
    val exposure: Float = 0f,
    val contrast: Float = 0f,
    val saturation: Float = 0f,
    val warmth: Float = 0f,
) {
    val isDefault: Boolean get() = this == Adjustments()

    fun toColorMatrix(): ColorMatrix = ColorMatrix(colorMatrixValues())

    /** A 4x5 row-major color matrix: exposure, then saturation, contrast and warmth. */
    internal fun colorMatrixValues(): FloatArray {
        val gain = 2f.pow(exposure)
        val exposureStep = scale(gain, gain, gain)

        val s = 1f + saturation
        val saturationStep =
            floatArrayOf(
                LUMA_R * (1 - s) + s,
                LUMA_G * (1 - s),
                LUMA_B * (1 - s),
                0f,
                0f,
                LUMA_R * (1 - s),
                LUMA_G * (1 - s) + s,
                LUMA_B * (1 - s),
                0f,
                0f,
                LUMA_R * (1 - s),
                LUMA_G * (1 - s),
                LUMA_B * (1 - s) + s,
                0f,
                0f,
                0f,
                0f,
                0f,
                1f,
                0f,
            )

        val c = 1f + contrast
        val contrastStep = scale(c, c, c, offset = MID_GREY * (1 - c))

        val warmthStep =
            floatArrayOf(
                1f,
                0f,
                0f,
                0f,
                WARMTH_SHIFT * warmth,
                0f,
                1f,
                0f,
                0f,
                0f,
                0f,
                0f,
                1f,
                0f,
                -WARMTH_SHIFT * warmth,
                0f,
                0f,
                0f,
                1f,
                0f,
            )

        return exposureStep.then(saturationStep).then(contrastStep).then(warmthStep)
    }

    private companion object {
        const val LUMA_R = 0.213f
        const val LUMA_G = 0.715f
        const val LUMA_B = 0.072f
        const val MID_GREY = 128f
        const val WARMTH_SHIFT = 28f
    }
}

private fun scale(
    r: Float,
    g: Float,
    b: Float,
    offset: Float = 0f,
) = floatArrayOf(
    r,
    0f,
    0f,
    0f,
    offset,
    0f,
    g,
    0f,
    0f,
    offset,
    0f,
    0f,
    b,
    0f,
    offset,
    0f,
    0f,
    0f,
    1f,
    0f,
)

/** Applies this matrix first and [next] second. Both are 4x5 row-major with the offset in column 5. */
internal fun FloatArray.then(next: FloatArray): FloatArray {
    val out = FloatArray(20)
    for (row in 0 until 4) {
        for (col in 0 until 5) {
            var sum = if (col == 4) next[row * 5 + 4] else 0f
            for (k in 0 until 4) sum += next[row * 5 + k] * this[k * 5 + col]
            out[row * 5 + col] = sum
        }
    }
    return out
}
