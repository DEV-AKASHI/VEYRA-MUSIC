/*
 * Copyright (C) 2026 SFG545
 *
 * This file is part of Veyra Music.
 *
 * Veyra Music es un fork de Orchard (https://github.com/SFG5453/Orchard),
 * mantenido únicamente para consumo personal y sin afiliación con el
 * proyecto original.
 *
 * Veyra Music is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * Veyra Music is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Veyra Music. If not, see <https://www.gnu.org/licenses/>.
 */

package dev.sfg.veyra.mobile.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.graphics.ColorUtils
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import dev.sfg.veyra.mobile.artwork.highResolutionArtworkUrl
import dev.sfg.veyra.mobile.ui.theme.CanopyColors
import dev.sfg.veyra.mobile.ui.theme.LocalReducedMotion
import dev.sfg.veyra.mobile.ui.theme.VeyraCyber

/**
 * Fondo ambiental dinámico de la app con proyección de portada y velo Obsidian Slate.
 */
@Composable
fun ArtworkBackdrop(
    palette: ArtworkPalette,
    animated: Boolean,
    modifier: Modifier = Modifier,
    rich: Boolean = false,
    artworkUrl: String = "",
    isPlaying: Boolean = false,
) {
    val tint = animateColorAsState(
        if (rich) palette.accent.wash(0.18f, 0.65f, 0.28f) else palette.accent,
        tween(900),
        label = "BackdropTint",
    )
    val base = animateColorAsState(
        if (rich) palette.bottom.wash(0.10f, 0.40f, 0.14f) else palette.bottom,
        tween(900),
        label = "BackdropBase",
    )
    val crown = animateColorAsState(
        palette.top.wash(0.12f, 0.45f, 0.16f),
        tween(900),
        label = "BackdropCrown",
    )
    val core = animateColorAsState(
        palette.accent.wash(0.16f, 0.55f, 0.18f),
        tween(900),
        label = "BackdropCore",
    )

    val reduceMotion = LocalReducedMotion.current
    val isCyberpunk = LocalCyberpunkTheme.current
    val isLiquidAurora = !isCyberpunk

    val effectiveAnimated = !reduceMotion && (animated || (isPlaying && artworkUrl.isNotBlank()))
    val drift: State<Float> = if (effectiveAnimated) {
        rememberInfiniteTransition(label = "BackdropDrift").animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(tween(18_000), RepeatMode.Reverse),
            label = "BackdropDriftValue",
        )
    } else {
        remember { mutableFloatStateOf(0f) }
    }

    val ambientScale = if (effectiveAnimated && isPlaying) {
        rememberInfiniteTransition(label = "BackdropBreath").animateFloat(
            initialValue = 1.12f,
            targetValue = 1.22f,
            animationSpec = infiniteRepeatable(
                animation = tween(12_000, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse,
            ),
            label = "BackdropBreathScale",
        )
    } else {
        remember { mutableFloatStateOf(1.12f) }
    }

    val ambientRotation = if (effectiveAnimated && isPlaying) {
        rememberInfiniteTransition(label = "BackdropRotation").animateFloat(
            initialValue = -3.2f,
            targetValue = 3.2f,
            animationSpec = infiniteRepeatable(
                animation = tween(24_000, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse,
            ),
            label = "BackdropRotationValue",
        )
    } else {
        remember { mutableFloatStateOf(0f) }
    }

    val ambientPulse = if (effectiveAnimated && isPlaying) {
        rememberInfiniteTransition(label = "BackdropPulse").animateFloat(
            initialValue = 0.94f,
            targetValue = 1.08f,
            animationSpec = infiniteRepeatable(
                animation = tween(9_000, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse,
            ),
            label = "BackdropPulseValue",
        )
    } else {
        remember { mutableFloatStateOf(1f) }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(if (isCyberpunk) VeyraCyber.canvas else Color(0xFF090718))
    ) {
        if (artworkUrl.isNotBlank()) {
            val context = LocalContext.current
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(highResolutionArtworkUrl(artworkUrl, 720))
                    .crossfade(500)
                    .build(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        scaleX = ambientScale.value
                        scaleY = ambientScale.value
                        rotationZ = ambientRotation.value
                        alpha = when {
                            isCyberpunk -> 0.24f
                            rich -> 0.62f
                            else -> 0.52f
                        }
                    }
                    .blur(if (isCyberpunk) 64.dp else 52.dp),
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .drawBehind {
                    val phase = drift.value
                    val pulse = ambientPulse.value
                    val accent = tint.value
                    val glow = if (rich) RICH_GLOW_ALPHA else GLOW_ALPHA
                    val twoPi = (2.0 * kotlin.math.PI).toFloat()

                    val topX = size.width * (0.28f + 0.16f * kotlin.math.cos(phase * twoPi))
                    val topY = size.height * (0.14f + 0.10f * kotlin.math.sin(phase * twoPi))
                    val top = Offset(topX, topY)

                    val lowX = size.width * (0.74f - 0.18f * kotlin.math.sin(phase * twoPi * 1.2f))
                    val lowY = size.height * (0.72f - 0.12f * kotlin.math.cos(phase * twoPi * 0.8f))
                    val low = Offset(lowX, lowY)

                    val midX = size.width * (0.48f + 0.15f * kotlin.math.sin(phase * twoPi * 0.9f))
                    val midY = size.height * (0.42f + 0.12f * kotlin.math.cos(phase * twoPi * 1.1f))
                    val mid = Offset(midX, midY)

                    // Poza 1: Carátula o Aurora Violet-Indigo (o Cyan en Cyberpunk sin carátula)
                    val primaryOrb = when {
                        artworkUrl.isNotBlank() -> accent
                        isCyberpunk -> VeyraCyber.cyan
                        else -> Color(0xFF7C3AED)
                    }
                    drawRect(
                        Brush.radialGradient(
                            colors = listOf(
                                primaryOrb.copy(alpha = if (artworkUrl.isNotBlank()) glow * 2.15f else if (isCyberpunk) 0.36f else 0.50f),
                                Color.Transparent,
                            ),
                            center = top,
                            radius = size.maxDimension * 0.82f * pulse,
                        ),
                    )

                    // Poza 2: Complementario carátula o Magenta / Acid accent
                    val secondaryOrb = when {
                        artworkUrl.isNotBlank() -> lerp(base.value, accent, 0.40f)
                        isCyberpunk -> VeyraCyber.magenta
                        else -> Color(0xFFEC4899)
                    }
                    drawRect(
                        Brush.radialGradient(
                            colors = listOf(
                                secondaryOrb.copy(alpha = if (artworkUrl.isNotBlank()) glow * 1.7f else if (isCyberpunk) 0.24f else 0.34f),
                                Color.Transparent,
                            ),
                            center = low,
                            radius = size.maxDimension * 0.74f * pulse,
                        ),
                    )

                    // Poza 3: Sky Flare / Electric Liquid Light
                    drawRect(
                        Brush.radialGradient(
                            colors = listOf(
                                (if (isCyberpunk) VeyraCyber.acidYellow else Color(0xFF38BDF8)).copy(
                                    alpha = if (artworkUrl.isNotBlank()) glow * 0.85f else if (isCyberpunk) 0.14f else 0.36f,
                                ),
                                Color.Transparent,
                            ),
                            center = mid,
                            radius = size.maxDimension * 0.62f * pulse,
                        ),
                    )

                    // Velo calibrado: profundo #05070E en Cyberpunk para conservar contraste neón con color puro de carátula
                    val veilBase = if (isCyberpunk) Color(0xFF05070E) else Color(0xFF090718)
                    drawRect(
                        Brush.verticalGradient(
                            colors = listOf(
                                veilBase.copy(alpha = if (isCyberpunk) 0.46f else 0.08f),
                                veilBase.copy(alpha = if (isCyberpunk) 0.72f else 0.36f),
                            ),
                        )
                    )
                }
        )
    }
}

/**
 * Puts a sampled colour where the wash can use it: the cover's hue, its saturation pulled into a
 * usable band, and a lightness the wash chooses outright. Covers are routinely near-black or
 * washed out, and mixing those towards the app's chrome only ever produces mud.
 *
 * A cover with no hue worth keeping is left grey rather than given an invented one — the hue
 * angle of a near-neutral colour is noise, and forcing saturation onto it picks a random tint.
 */
private fun Color.wash(minSaturation: Float, maxSaturation: Float, lightness: Float): Color {
    val hsl = FloatArray(3)
    ColorUtils.colorToHSL(toArgb(), hsl)
    hsl[1] = if (hsl[1] < 0.08f) 0.05f else hsl[1].coerceIn(minSaturation, maxSaturation)
    hsl[2] = lightness
    return Color(ColorUtils.HSLToColor(hsl))
}

/** Low enough that body text keeps its contrast over the wash. */
private const val GLOW_ALPHA = 0.24f

/**
 * Behind glass the wash provides depth while staying deep, dark, and sleek.
 */
private const val RICH_GLOW_ALPHA = 0.32f
