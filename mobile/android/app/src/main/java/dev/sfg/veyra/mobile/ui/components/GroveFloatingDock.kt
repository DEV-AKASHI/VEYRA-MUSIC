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

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import dev.sfg.veyra.connect.R
import dev.sfg.veyra.mobile.model.PlaybackSnapshot
import dev.sfg.veyra.mobile.model.TransitionMarker
import dev.sfg.veyra.mobile.playback.LiveAudioSpectrumMeter
import dev.sfg.veyra.mobile.ui.glass.GlassTone
import dev.sfg.veyra.mobile.ui.glass.LocalGlass
import dev.sfg.veyra.mobile.ui.glass.glassPane
import dev.sfg.veyra.mobile.ui.modifiers.tactilePress
import dev.sfg.veyra.mobile.ui.theme.CanopyColors
import dev.sfg.veyra.mobile.ui.theme.LocalAccent
import dev.sfg.veyra.mobile.ui.theme.LocalReducedMotion
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.roundToInt
import kotlin.math.sin

private val LiquidDockShape = RoundedCornerShape(32.dp)
private val LiquidCoverShape = CircleShape

/**
 * Liquid Design + Aurora UI MiniPlayer: refractive translucent capsule, circular artwork,
 * droplet Play/Pause button and Aurora spectrum wave synced to album palette.
 */
@Composable
fun GroveFloatingDock(
    playback: PlaybackSnapshot,
    onTogglePlay: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    transition: TransitionMarker? = null,
    mixProgress: Float? = null,
    onArtworkBounds: ((Rect) -> Unit)? = null,
    onClear: () -> Unit = {},
) {
    val track = playback.currentTrack ?: return
    var dragOffsetY by remember { mutableFloatStateOf(0f) }
    var isDragging by remember { mutableStateOf(false) }
    val settleAnim = remember { Animatable(0f) }
    val coroutineScope = rememberCoroutineScope()
    val density = LocalDensity.current
    val haptics = LocalHapticFeedback.current
    val reduceMotion = LocalReducedMotion.current
    val glass = LocalGlass.current.enabled

    val currentOffsetY = if (isDragging) dragOffsetY.coerceAtLeast(0f) else settleAnim.value.coerceAtLeast(0f)
    val dismissThresholdPx = with(density) { 48.dp.toPx() }
    val dismissVelocityPx = with(density) { 300.dp.toPx() }
    val fullDismissPx = with(density) { 84.dp.toPx() }

    val marker = transition?.takeIf {
        it.trackId.isNotBlank() && it.trackId == track.id && it.startMs > 0 && it.startMs < playback.durationMs
    }
    val effectiveDuration = (marker?.startMs ?: playback.durationMs).coerceAtLeast(1)
    val progress = if (effectiveDuration > 0) {
        (playback.positionMs.toFloat() / effectiveDuration.toFloat()).coerceIn(0f, 1f)
    } else 0f
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = if (reduceMotion) snap() else spring(),
        label = "GroveDockProgress",
    )

    val infiniteTransition = rememberInfiniteTransition(label = "GroveDockAura")
    val auraAlpha by infiniteTransition.animateFloat(
        initialValue = 0.20f,
        targetValue = 0.45f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = androidx.compose.animation.core.FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "GroveAuraPulse",
    )

    // Fase sinusoidal activa para la onda de avance del mini-reproductor
    val wavePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2400, easing = androidx.compose.animation.core.LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "MiniWavePhase",
    )
    val waveAmplitude by animateFloatAsState(
        targetValue = if (playback.isPlaying && !reduceMotion) 2.2f else 0f,
        animationSpec = if (reduceMotion) snap() else spring(dampingRatio = 0.85f, stiffness = Spring.StiffnessMediumLow),
        label = "MiniWaveAmplitude",
    )

    val dragModifier = Modifier.draggable(
        orientation = Orientation.Vertical,
        state = rememberDraggableState { delta ->
            dragOffsetY += delta
        },
        onDragStarted = {
            dragOffsetY = settleAnim.value
            isDragging = true
        },
        onDragStopped = { velocity ->
            val swipeUpThresholdPx = with(density) { -20.dp.toPx() }
            val swipeUpVelocityPx = with(density) { -260.dp.toPx() }
            val isSwipeUp = dragOffsetY < swipeUpThresholdPx || velocity < swipeUpVelocityPx
            val committed = dragOffsetY > dismissThresholdPx || velocity > dismissVelocityPx

            isDragging = false
            coroutineScope.launch {
                settleAnim.snapTo(dragOffsetY)
                if (isSwipeUp) {
                    onClick()
                    settleAnim.animateTo(0f, spring(dampingRatio = 0.85f, stiffness = Spring.StiffnessMediumLow))
                    dragOffsetY = 0f
                } else if (committed) {
                    settleAnim.animateTo(fullDismissPx, tween(140))
                    onClear()
                    settleAnim.snapTo(0f)
                    dragOffsetY = 0f
                } else {
                    settleAnim.animateTo(0f, spring(dampingRatio = 0.8f, stiffness = Spring.StiffnessMediumLow))
                    dragOffsetY = 0f
                }
            }
        },
    )

    val dismissAlpha = (1f - (currentOffsetY / fullDismissPx)).coerceIn(0f, 1f)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 6.dp)
            .offset { IntOffset(0, currentOffsetY.roundToInt()) }
            .alpha(dismissAlpha)
            .then(dragModifier)
    ) {
        // Liquid Aurora ambient halo reactivo al audio real
        val dockTick = rememberLiveAudioTick(playback.isPlaying, reduceMotion)
        if (playback.isPlaying && !reduceMotion) {
            val trackAccent = LocalAccent.current
            val liveDockBeat = LiveAudioSpectrumMeter.readBeatEnergy(true)
            val dynamicHaloAlpha = if (liveDockBeat >= 0f) 0.25f + 0.45f * liveDockBeat else auraAlpha
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .padding(horizontal = 6.dp)
                    .clip(LiquidDockShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                trackAccent.copy(alpha = dynamicHaloAlpha * 0.56f),
                                Color(0xFF8B5CF6).copy(alpha = dynamicHaloAlpha * 0.42f),
                                Color(0xFF38BDF8).copy(alpha = dynamicHaloAlpha * 0.30f),
                                Color.Transparent,
                            ),
                        ),
                    ),
            )
        }

        // Liquid Design calibrated translucent capsule (legible over scrolled text)
        val trackTint = LocalAccent.current
        val chassisGradient = Brush.verticalGradient(
            colors = listOf(
                androidx.compose.ui.graphics.lerp(Color(0xE0130F28), trackTint, 0.16f),
                androidx.compose.ui.graphics.lerp(Color(0xEB0B091A), Color(0xFF1E1B4B), 0.22f),
            ),
        )
        val chassisBorderBrush = Brush.verticalGradient(
            listOf(
                Color.White.copy(alpha = 0.46f),
                Color.White.copy(alpha = 0.14f),
                Color(0xFF38BDF8).copy(alpha = 0.28f),
            ),
        )

        val dockDescription = "${track.title}, ${dev.sfg.veyra.mobile.ui.components.localizedArtistName(track.artist)}"
        Surface(
            shape = LiquidDockShape,
            color = Color.Transparent,
            shadowElevation = 0.dp,
            border = androidx.compose.foundation.BorderStroke(1.1.dp, chassisBorderBrush),
            modifier = Modifier
                .fillMaxWidth()
                .then(if (glass) Modifier.glassPane(LiquidDockShape, GlassTone.CHROME) else Modifier)
                .clip(LiquidDockShape)
                .background(chassisGradient)
                .semantics {
                    role = Role.Button
                    contentDescription = dockDescription
                }
                .clickable(onClick = {
                    haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onClick()
                })
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(68.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Carátula circular líquida con halo de cristal Aurora
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(LiquidCoverShape)
                            .clickable(onClick = {
                                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                onClick()
                            })
                            .background(Color.White.copy(alpha = 0.10f))
                            .border(
                                1.5.dp,
                                Brush.sweepGradient(
                                    listOf(
                                        Color.White.copy(alpha = 0.56f),
                                        Color(0xFF38BDF8).copy(alpha = 0.48f),
                                        Color(0xFF8B5CF6).copy(alpha = 0.42f),
                                        Color.White.copy(alpha = 0.18f),
                                        Color.White.copy(alpha = 0.56f),
                                    ),
                                ),
                                LiquidCoverShape,
                            )
                    ) {
                        AnimatedContent(
                            targetState = track.artworkUrl to track.title,
                            transitionSpec = {
                                fadeIn(animationSpec = tween(350)) togetherWith fadeOut(animationSpec = tween(250))
                            },
                            label = "LiquidArtworkCrossfade",
                        ) { (artUrl, artTitle) ->
                            ArtworkTile(
                                url = artUrl,
                                description = artTitle,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .onGloballyPositioned { onArtworkBounds?.invoke(it.boundsInRoot()) },
                                radius = 999
                            )
                        }
                    }

                    Spacer(Modifier.width(12.dp))

                    // Información del tema y artista
                    AnimatedContent(
                        targetState = track.title to track.artist,
                        transitionSpec = {
                            (fadeIn(animationSpec = tween(300, delayMillis = 30)) +
                                slideInVertically(animationSpec = tween(300, delayMillis = 30)) { it / 3 }) togetherWith
                                (fadeOut(animationSpec = tween(180)) +
                                    slideOutVertically(animationSpec = tween(180)) { -it / 3 })
                        },
                        label = "GroveTrackTransition",
                        modifier = Modifier.weight(1f),
                    ) { (title, artist) ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(onClick = {
                                    haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    onClick()
                                })
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (playback.isPlaying) {
                                    VeyraLiveEqualizerBars(
                                        playing = true,
                                        modifier = Modifier.size(width = 13.dp, height = 11.dp),
                                    )
                                    Spacer(Modifier.width(6.dp))
                                }
                                Text(
                                    text = title,
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = CanopyColors.Text,
                                    maxLines = 1,
                                    modifier = Modifier.basicMarquee(),
                                )
                            }
                            Spacer(Modifier.height(2.dp))
                            Text(
                                text = dev.sfg.veyra.mobile.ui.components.localizedArtistName(artist),
                                style = MaterialTheme.typography.bodySmall,
                                color = CanopyColors.Muted,
                                maxLines = 1
                            )
                        }
                    }

                    Spacer(Modifier.width(8.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Liquid droplet Play/Pause button
                        val playLiquidGradient = Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF8B5CF6),
                                Color(0xFF6366F1),
                                Color(0xFF38BDF8),
                            ),
                        )
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .semantics {
                                    role = Role.Button
                                }
                                .tactilePress(onClick = {
                                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                    onTogglePlay()
                                }),
                            contentAlignment = Alignment.Center,
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(playLiquidGradient)
                                    .border(
                                        1.1.dp,
                                        Brush.verticalGradient(
                                            listOf(
                                                Color.White.copy(alpha = 0.55f),
                                                Color.White.copy(alpha = 0.14f),
                                            ),
                                        ),
                                        CircleShape,
                                    ),
                                contentAlignment = Alignment.Center,
                            ) {
                                Crossfade(
                                    targetState = playback.isPlaying,
                                    animationSpec = if (reduceMotion) snap() else tween(180),
                                    label = "LiquidPlayCrossfade",
                                ) { playing ->
                                    VeyraGlyphIcon(
                                        kind = if (playing) VeyraIconKind.PAUSE else VeyraIconKind.PLAY,
                                        tint = Color.White,
                                        filled = true,
                                        contentDescription = if (playing) {
                                            stringResource(R.string.action_pause)
                                        } else {
                                            stringResource(R.string.action_play)
                                        },
                                        modifier = Modifier.size(20.dp),
                                    )
                                }
                            }
                        }

                        Spacer(Modifier.width(4.dp))

                        // Liquid circular Next button
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .semantics {
                                    role = Role.Button
                                }
                                .tactilePress(onClick = {
                                    haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    onNext()
                                }),
                            contentAlignment = Alignment.Center,
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.12f))
                                    .border(1.dp, Color.White.copy(alpha = 0.28f), CircleShape),
                                contentAlignment = Alignment.Center,
                            ) {
                                VeyraGlyphIcon(
                                    kind = VeyraIconKind.SKIP_NEXT,
                                    tint = Color.White,
                                    contentDescription = stringResource(R.string.action_next),
                                    modifier = Modifier.size(18.dp),
                                )
                            }
                        }
                    }
                }

                // Liquid Aurora active wave
                val miniWavePath = remember { Path() }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(14.dp)
                        .align(Alignment.BottomCenter)
                        .padding(horizontal = 20.dp),
                ) {
                    val waveBrush = Brush.horizontalGradient(
                        colors = listOf(Color(0xFF8B5CF6), Color(0xFFEC4899), Color(0xFF38BDF8)),
                    )
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val canvasWidth = size.width
                        val canvasHeight = size.height
                        if (canvasWidth <= 2f || canvasHeight <= 2f) return@Canvas
                        val midY = canvasHeight / 2f
                        val progressClamped = animatedProgress.coerceIn(0f, 1f)
                        val progressX = (canvasWidth * progressClamped).coerceIn(0f, canvasWidth)

                        // Canal de fondo translúcido
                        drawLine(
                            color = Color.White.copy(alpha = 0.10f),
                            start = androidx.compose.ui.geometry.Offset(0f, midY),
                            end = androidx.compose.ui.geometry.Offset(canvasWidth, midY),
                            strokeWidth = 2.5.dp.toPx(),
                            cap = StrokeCap.Round,
                        )

                        // Trazo ondulado activo reactivo al beat real del audio
                        val _dockFrame = dockTick.value
                        val beatWaveScale = if (playback.isPlaying && !reduceMotion) {
                            val b = LiveAudioSpectrumMeter.readBeatEnergy(true)
                            if (b >= 0f) 0.70f + 1.15f * b else 1f
                        } else 1f
                        val wavelength = 54.dp.toPx().coerceAtLeast(10f)
                        if (progressX > 1f && wavelength > 0f) {
                            miniWavePath.reset()
                            val ampPx = (waveAmplitude * beatWaveScale).dp.toPx().coerceIn(0f, (canvasHeight / 2f) - 1.5f)
                            val k = (2 * PI / wavelength).toFloat()
                            val leadInDist = 18.dp.toPx()
                            miniWavePath.moveTo(0f, midY)

                            var x = 0f
                            val step = 1.2f
                            var lastY = midY
                            while (x <= progressX) {
                                val tIn = (x / leadInDist).coerceIn(0f, 1f)
                                val tOut = ((progressX - x) / leadInDist).coerceIn(0f, 1f)
                                val envelope = (tIn * tIn * (3f - 2f * tIn)) * (tOut * tOut * (3f - 2f * tOut))
                                val wave = sin(k * x - wavePhase)
                                val y = (midY + wave * ampPx * envelope).coerceIn(1f, canvasHeight - 1f)
                                if (y.isFinite()) {
                                    miniWavePath.lineTo(x, y)
                                    lastY = y
                                }
                                x += step
                            }

                            // Halo difuso suave
                            drawPath(
                                path = miniWavePath,
                                brush = waveBrush,
                                style = Stroke(width = 4.2.dp.toPx(), cap = StrokeCap.Round),
                                alpha = 0.25f,
                            )

                            // Trazo principal nítido y esbelto
                            drawPath(
                                path = miniWavePath,
                                brush = waveBrush,
                                style = Stroke(width = 2.2.dp.toPx(), cap = StrokeCap.Round),
                            )

                            // Punto frontal con resplandor Sky Flare
                            if (progressX.isFinite() && lastY.isFinite()) {
                                drawCircle(
                                    color = Color(0xFF38BDF8).copy(alpha = 0.40f),
                                    radius = 5.dp.toPx(),
                                    center = androidx.compose.ui.geometry.Offset(progressX, lastY),
                                )
                                drawCircle(
                                    color = Color.White,
                                    radius = 2.5.dp.toPx(),
                                    center = androidx.compose.ui.geometry.Offset(progressX, lastY),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
