/*
 * Copyright (C) 2026 SFG545 / AKASHI DEV
 *
 * This file is part of Veyra Music.
 *
 * Veyra Music is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 */

package dev.sfg.veyra.mobile.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.unit.dp
import dev.sfg.veyra.mobile.playback.LiveAudioSpectrumMeter
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Multi-layer liquid perimeter RGB edge aura synced to playback rhythm and artwork colors.
 */
@Composable
fun AuraSyncEdgeOverlay(
    enabled: Boolean,
    isPlaying: Boolean,
    accentColor: Color,
    secondaryColor: Color,
    reduceMotion: Boolean,
    cyberpunk: Boolean,
    modifier: Modifier = Modifier,
) {
    val visibility by animateFloatAsState(
        targetValue = if (enabled) 1f else 0f,
        animationSpec = tween(durationMillis = if (reduceMotion) 0 else 280),
        label = "AuraVisibility",
    )
    if (visibility <= 0.01f) return

    val playingFactor by animateFloatAsState(
        targetValue = if (isPlaying) 1f else 0.42f,
        animationSpec = tween(durationMillis = if (reduceMotion) 0 else 320),
        label = "AuraPlayingFactor",
    )

    val transition = rememberInfiniteTransition(label = "AuraSyncLoop")
    val colorPhase by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "AuraColorPhase",
    )
    val beatPulse by transition.animateFloat(
        initialValue = 0.65f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 560),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "AuraBeatPulse",
    )
    val frameTick = rememberLiveAudioTick(isPlaying = enabled && isPlaying, reduceMotion = reduceMotion)

    val phase = if (isPlaying && !reduceMotion) colorPhase else 0f
    val fallbackBeat = if (isPlaying && !reduceMotion) beatPulse else 0.88f

    val palette = if (cyberpunk) {
        listOf(
            Color(0xFF22D3EE),
            Color(0xFFFACC15),
            Color(0xFFF472B6),
            accentColor,
        )
    } else {
        listOf(
            accentColor,
            Color(0xFF38BDF8),
            Color(0xFF8B5CF6),
            Color(0xFFEC4899),
            secondaryColor,
        )
    }

    fun sampleCycle(offset: Float): Color {
        val t = ((phase + offset) % 1f) * palette.size
        val idx = t.toInt().coerceIn(0, palette.lastIndex)
        val next = (idx + 1) % palette.size
        return lerp(palette[idx], palette[next], t - idx)
    }

    val c0 = sampleCycle(0.00f)
    val c1 = sampleCycle(0.25f)
    val c2 = sampleCycle(0.50f)
    val c3 = sampleCycle(0.75f)

    Canvas(modifier = modifier.fillMaxSize()) {
        // Lectura en fase de dibujo para que el borde RGB pulse al ritmo real sin recomponer
        val _tick = frameTick.value
        val liveBeat = if (isPlaying && !reduceMotion) LiveAudioSpectrumMeter.readBeatEnergy(true) else -1f
        val effectiveBeat = if (liveBeat >= 0f) 0.42f + 0.72f * liveBeat else fallbackBeat
        val masterAlpha = (visibility * playingFactor * effectiveBeat).coerceIn(0f, 1.15f)
        val tierScale = if (liveBeat >= 0f) 0.82f + 0.42f * liveBeat else 1f

        val corner = if (cyberpunk) 10.dp.toPx() else 30.dp.toPx()
        val angleRad = phase * 2f * PI.toFloat()
        val dx = size.width * 0.52f * cos(angleRad)
        val dy = size.height * 0.52f * sin(angleRad)
        val center = Offset(size.width * 0.5f, size.height * 0.5f)
        val brush = Brush.linearGradient(
            colors = listOf(c0, c1, c2, c3),
            start = Offset(center.x - dx, center.y - dy),
            end = Offset(center.x + dx, center.y + dy),
        )

        // 4-tier liquid neon perimeter glow reactivo al beat
        drawRoundRect(
            brush = brush,
            cornerRadius = CornerRadius(corner, corner),
            style = Stroke(width = (26.dp * tierScale).toPx()),
            alpha = (0.10f * masterAlpha).coerceIn(0f, 0.25f),
        )
        drawRoundRect(
            brush = brush,
            cornerRadius = CornerRadius(corner, corner),
            style = Stroke(width = (14.dp * tierScale).toPx()),
            alpha = (0.18f * masterAlpha).coerceIn(0f, 0.36f),
        )
        drawRoundRect(
            brush = brush,
            cornerRadius = CornerRadius(corner, corner),
            style = Stroke(width = (6.dp * tierScale).toPx()),
            alpha = (0.36f * masterAlpha).coerceIn(0f, 0.55f),
        )
        drawRoundRect(
            brush = brush,
            cornerRadius = CornerRadius(corner, corner),
            style = Stroke(width = (2.5.dp * (0.9f + 0.25f * tierScale)).toPx()),
            alpha = (0.88f * masterAlpha).coerceIn(0f, 1f),
        )
    }
}
