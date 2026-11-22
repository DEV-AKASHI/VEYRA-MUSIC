/*
 * Copyright (C) 2026 AKASHI DEV
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

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.sfg.veyra.mobile.playback.LiveAudioSpectrumMeter
import dev.sfg.veyra.mobile.ui.theme.LocalAccent
import dev.sfg.veyra.mobile.ui.theme.LocalReducedMotion
import dev.sfg.veyra.mobile.ui.theme.VeyraCyber
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

/** Forma biselada oficial para bloques protagonistas en Cyberpunk. */
val CyberCutHeroShape = CutCornerShape(
    topStart = 18.dp,
    bottomEnd = 18.dp,
    topEnd = 4.dp,
    bottomStart = 4.dp,
)

val CyberCutCompactShape = CutCornerShape(
    topStart = 8.dp,
    bottomEnd = 8.dp,
    topEnd = 2.dp,
    bottomStart = 2.dp,
)

/** Dibuja los 4 corchetes de mira `[ ]` en las esquinas de tarjetas destacadas Cyberpunk. */
fun Modifier.cyberCornerBrackets(
    color: Color = VeyraCyber.cyan,
    bracketLength: Dp = 12.dp,
    strokeWidth: Dp = 2.dp,
    inset: Dp = 0.dp,
): Modifier = drawWithContent {
    drawContent()
    val len = bracketLength.toPx()
    val sw = strokeWidth.toPx()
    val pad = inset.toPx()
    val left = pad
    val top = pad
    val right = size.width - pad
    val bottom = size.height - pad

    // Superior izquierda
    drawLine(color, Offset(left, top), Offset(left + len, top), sw)
    drawLine(color, Offset(left, top), Offset(left, top + len), sw)
    // Superior derecha
    drawLine(color, Offset(right - len, top), Offset(right, top), sw)
    drawLine(color, Offset(right, top), Offset(right, top + len), sw)
    // Inferior izquierda
    drawLine(color, Offset(left, bottom - len), Offset(left, bottom), sw)
    drawLine(color, Offset(left, bottom), Offset(left + len, bottom), sw)
    // Inferior derecha
    drawLine(color, Offset(right - len, bottom), Offset(right, bottom), sw)
    drawLine(color, Offset(right, bottom - len), Offset(right, bottom), sw)
}

/** Línea láser horizontal que recorre la tarjeta una vez al cambiar [triggerKey]. */
fun Modifier.cyberScanBeam(
    triggerKey: Any?,
    color: Color = VeyraCyber.cyan,
): Modifier = composed {
    val reducedMotion = LocalReducedMotion.current
    val progress = remember { Animatable(0f) }

    LaunchedEffect(triggerKey, reducedMotion) {
        if (reducedMotion) return@LaunchedEffect
        progress.snapTo(0f)
        progress.animateTo(1f, animationSpec = tween(420, easing = FastOutSlowInEasing))
    }

    drawWithContent {
        drawContent()
        val p = progress.value
        if (p in 0.02f..0.98f) {
            val y = size.height * p
            drawRect(
                brush = Brush.verticalGradient(
                    0f to Color.Transparent,
                    0.5f to color.copy(alpha = 0.24f),
                    1f to Color.Transparent,
                    startY = (y - 16.dp.toPx()).coerceAtLeast(0f),
                    endY = (y + 16.dp.toPx()).coerceAtMost(size.height),
                ),
            )
            drawLine(
                color = color.copy(alpha = 0.72f),
                start = Offset(0f, y),
                end = Offset(size.width, y),
                strokeWidth = 1.5.dp.toPx(),
            )
        }
    }
}

/** Micro-glitch cromático rápido (90ms) al activar controles principales en Cyberpunk. */
fun Modifier.cyberGlitchBurst(
    triggerCount: Int,
): Modifier = composed {
    val reducedMotion = LocalReducedMotion.current
    val shift = remember { Animatable(0f) }

    LaunchedEffect(triggerCount, reducedMotion) {
        if (triggerCount <= 0 || reducedMotion) return@LaunchedEffect
        shift.snapTo(2.4f)
        shift.animateTo(-1.4f, tween(45))
        shift.animateTo(0f, tween(45))
    }

    val currentShift = shift.value
    this
        .graphicsLayer {
            translationX = currentShift.dp.toPx()
        }
        .drawWithContent {
            drawContent()
            if (kotlin.math.abs(currentShift) > 0.2f) {
                drawRect(
                    color = VeyraCyber.cyan.copy(alpha = 0.14f),
                    topLeft = Offset(-2.dp.toPx(), 0f),
                )
                drawRect(
                    color = VeyraCyber.magenta.copy(alpha = 0.10f),
                    topLeft = Offset(2.dp.toPx(), 0f),
                )
            }
        }
}

/** Reflejo especular diagonal reactivo al desplazamiento en tarjetas Liquid Aurora. */
fun Modifier.liquidSpecularSheen(
    scrollBias: Float = 0f,
    tint: Color = Color(0xFF38BDF8),
): Modifier = drawWithContent {
    drawContent()
    val shift = (scrollBias.coerceIn(-1f, 1f)) * size.width * 0.28f
    val startX = size.width * 0.18f + shift
    drawRect(
        brush = Brush.linearGradient(
            colors = listOf(
                Color.Transparent,
                Color.White.copy(alpha = 0.10f),
                tint.copy(alpha = 0.08f),
                Color.Transparent,
            ),
            start = Offset(startX, 0f),
            end = Offset(startX + size.width * 0.55f, size.height),
        ),
    )
}

/** Onda expansiva luminosa nacida en el punto táctil sin interceptar eventos de clic. */
fun Modifier.liquidAuroraTouchRipple(
    color: Color? = null,
): Modifier = composed {
    val reducedMotion = LocalReducedMotion.current
    val accent = color ?: LocalAccent.current
    val scope = rememberCoroutineScope()
    var tapPoint by remember { mutableStateOf(Offset.Unspecified) }
    val rippleProgress = remember { Animatable(0f) }

    this
        .pointerInput(reducedMotion) {
            if (reducedMotion) return@pointerInput
            awaitEachGesture {
                val down = awaitFirstDown(requireUnconsumed = false)
                tapPoint = down.position
                scope.launch {
                    rippleProgress.snapTo(0f)
                    rippleProgress.animateTo(1f, tween(340, easing = FastOutSlowInEasing))
                }
            }
        }
        .drawBehind {
            val p = rippleProgress.value
            val origin = tapPoint
            if (p in 0.01f..0.99f && origin != Offset.Unspecified) {
                val maxR = size.maxDimension * 0.85f
                val alpha = (1f - p) * 0.28f
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            accent.copy(alpha = alpha),
                            Color(0xFF38BDF8).copy(alpha = alpha * 0.5f),
                            Color.Transparent,
                        ),
                        center = origin,
                        radius = (maxR * p).coerceAtLeast(1f),
                    ),
                    center = origin,
                    radius = (maxR * p).coerceAtLeast(1f),
                )
            }
        }
}

/** Ecualizador animado de 3 barras para pistas o colecciones en reproducción. */
@Composable
fun VeyraLiveEqualizerBars(
    playing: Boolean,
    modifier: Modifier = Modifier,
    color: Color? = null,
) {
    val isCyberpunk = LocalCyberpunkTheme.current
    val reducedMotion = LocalReducedMotion.current
    val barColor = color ?: if (isCyberpunk) VeyraCyber.acidYellow else Color(0xFF38BDF8)

    val phase = if (playing && !reducedMotion) {
        val infinite = rememberInfiniteTransition(label = "LiveEq")
        val anim by infinite.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(680, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse,
            ),
            label = "LiveEqLevel",
        )
        anim
    } else {
        0.45f
    }

    Canvas(modifier = modifier) {
        val barWidth = size.width / 5f
        val gap = barWidth * 0.75f
        val corner = if (isCyberpunk) 0f else barWidth * 0.5f
        val heights = floatArrayOf(
            0.35f + 0.55f * phase,
            0.90f - 0.55f * phase,
            0.45f + 0.45f * (1f - phase),
        )
        heights.forEachIndexed { idx, ratio ->
            val bh = (size.height * ratio).coerceAtLeast(size.height * 0.25f)
            val x = idx * (barWidth + gap)
            val y = size.height - bh
            drawRoundRect(
                color = barColor,
                topLeft = Offset(x, y),
                size = Size(barWidth, bh),
                cornerRadius = CornerRadius(corner, corner),
            )
        }
    }
}

/** Reloj de frame compartible para lectores de audio sin recomponer árboles. */
@Composable
fun rememberLiveAudioTick(
    isPlaying: Boolean,
    reduceMotion: Boolean,
): State<Long> {
    val frameTick = remember { mutableLongStateOf(0L) }
    LaunchedEffect(isPlaying, reduceMotion) {
        if (reduceMotion) return@LaunchedEffect
        val stopDeadlineNs = if (isPlaying) Long.MAX_VALUE else System.nanoTime() + 450_000_000L
        while (isPlaying || System.nanoTime() < stopDeadlineNs) {
            withFrameNanos { ns ->
                frameTick.longValue = ns
            }
        }
    }
    return frameTick
}

/** Rellena [outBands] desde audio PCM real o fallback armónico y retorna la energía grave promedio. */
fun sampleLiveAudioBands(
    outBands: FloatArray,
    isPlaying: Boolean,
    tickNs: Long,
): Float {
    val hasLivePcm = LiveAudioSpectrumMeter.readSpectrum(outBands, isPlaying)
    if (!hasLivePcm && isPlaying) {
        val sec = (tickNs / 1_000_000_000.0).toFloat()
        for (i in outBands.indices) {
            val norm = i / (outBands.size - 1f)
            val bassBias = 1.0f - norm * 0.42f
            val w1 = abs(sin(sec * 3.1f + i * 0.42f))
            val w2 = abs(sin(sec * 5.6f - i * 0.78f))
            val w3 = abs(cos(sec * 8.4f + i * 1.15f))
            outBands[i] = ((0.46f * w1 + 0.34f * w2 + 0.20f * w3) * bassBias).coerceIn(0.05f, 1f)
        }
    }
    return ((outBands[0] + outBands[1] + outBands[2] + outBands[3]) * 0.25f).coerceIn(0f, 1f)
}

/** Decodificación técnica breve (150ms) para etiquetas HUD de Cyberpunk. */
@Composable
fun CyberScrambleText(
    text: String,
    style: TextStyle,
    color: Color,
    modifier: Modifier = Modifier,
    maxLines: Int = 1,
    fontWeight: FontWeight? = null,
) {
    val isCyberpunk = LocalCyberpunkTheme.current
    val reducedMotion = LocalReducedMotion.current
    var rendered by remember(text) { mutableStateOf(text) }

    LaunchedEffect(text, isCyberpunk, reducedMotion) {
        if (!isCyberpunk || reducedMotion || text.length < 3) {
            rendered = text
            return@LaunchedEffect
        }
        val glyphs = "01/X#A9"
        val steps = 4
        for (step in 1..steps) {
            val revealCount = (text.length * step) / steps
            rendered = buildString(text.length) {
                text.forEachIndexed { i, ch ->
                    if (i < revealCount || ch == ' ' || ch == '[' || ch == ']' || ch == '/') {
                        append(ch)
                    } else {
                        append(glyphs[(i + step) % glyphs.length])
                    }
                }
            }
            delay(36)
        }
        rendered = text
    }

    Text(
        text = rendered,
        style = if (fontWeight != null) style.copy(fontWeight = fontWeight) else style,
        color = color,
        maxLines = maxLines,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier,
    )
}
