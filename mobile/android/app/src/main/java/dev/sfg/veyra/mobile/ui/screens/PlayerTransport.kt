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

package dev.sfg.veyra.mobile.ui.screens

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Repeat
import androidx.compose.material.icons.rounded.RepeatOne
import androidx.compose.material.icons.rounded.Shuffle
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material.icons.rounded.SkipPrevious
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.sfg.veyra.connect.R
import dev.sfg.veyra.mobile.model.PlaybackStatus
import dev.sfg.veyra.mobile.model.RepeatMode
import dev.sfg.veyra.mobile.ui.components.LocalCyberpunkTheme
import dev.sfg.veyra.mobile.ui.modifiers.tactilePress
import dev.sfg.veyra.mobile.ui.theme.CanopyColors
import dev.sfg.veyra.mobile.ui.theme.LocalAccent
import dev.sfg.veyra.mobile.ui.theme.LocalReducedMotion
import dev.sfg.veyra.mobile.ui.theme.VeyraCyber
import dev.sfg.veyra.mobile.ui.theme.VeyraSize
import dev.sfg.veyra.mobile.ui.theme.legibleOnDarkChrome
import dev.sfg.veyra.mobile.ui.tv.tvFocusable
import androidx.compose.animation.core.snap
import androidx.compose.foundation.layout.offset

/**
 * Ergonomic transport controls row providing direct access to Previous,
 * Hero Play/Pause with artwork accent tint, and Next.
 */
@Composable
fun PlayerTransportControls(
    isPlaying: Boolean,
    status: PlaybackStatus,
    shuffle: Boolean,
    repeatMode: RepeatMode,
    localControls: Boolean,
    onToggle: () -> Unit,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onShuffle: () -> Unit,
    onRepeat: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val haptics = LocalHapticFeedback.current
    val isCyberpunk = LocalCyberpunkTheme.current
    val controlShape = if (isCyberpunk) VeyraCyber.shape else CircleShape

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Shuffle Toggle Button
        TransportToggleOption(
            icon = Icons.Rounded.Shuffle,
            description = stringResource(R.string.action_shuffle),
            active = shuffle,
            enabled = localControls,
            isCyberpunk = isCyberpunk,
            onClick = {
                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onShuffle()
            },
        )

        val transportSlotBorder = if (isCyberpunk) {
            Brush.verticalGradient(listOf(VeyraCyber.cyan.copy(alpha = 0.7f), VeyraCyber.cyan.copy(alpha = 0.7f)))
        } else {
            Brush.verticalGradient(listOf(Color.White.copy(alpha = 0.28f), Color.White.copy(alpha = 0.08f)))
        }
        val transportDiscBorder = if (isCyberpunk) {
            Brush.verticalGradient(listOf(VeyraCyber.cyan, VeyraCyber.cyan))
        } else {
            Brush.verticalGradient(listOf(Color.White.copy(alpha = 0.34f), Color.White.copy(alpha = 0.10f)))
        }
        val transportDiscGradient = if (isCyberpunk) {
            Brush.verticalGradient(listOf(VeyraCyber.surfaceAlt, VeyraCyber.surface))
        } else {
            Brush.verticalGradient(colors = listOf(Color.White.copy(alpha = 0.16f), Color.White.copy(alpha = 0.06f)))
        }

        // Previous Button
        Box(
            modifier = Modifier
                .size(52.dp)
                .tvFocusable(shape = controlShape, scale = 1.12f, onClick = {
                    haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onPrevious()
                })
                .clip(controlShape)
                .background(if (isCyberpunk) VeyraCyber.ink else Color.White.copy(alpha = 0.06f))
                .border(if (isCyberpunk) VeyraCyber.borderWidth else 1.dp, transportSlotBorder, controlShape),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(controlShape)
                    .background(transportDiscGradient)
                    .border(if (isCyberpunk) 0.dp else 1.dp, transportDiscBorder, controlShape)
                    .tactilePress(onClick = {
                        haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onPrevious()
                    }),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Rounded.SkipPrevious,
                    contentDescription = stringResource(R.string.playerx_previous_track_cd),
                    tint = if (isCyberpunk) VeyraCyber.text else Color.White,
                    modifier = Modifier.size(26.dp),
                )
            }
        }

        // Hero Play/Pause Button
        HeroPlayButton(
            isPlaying = isPlaying,
            isBuffering = status == PlaybackStatus.BUFFERING || status == PlaybackStatus.LOADING,
            isCyberpunk = isCyberpunk,
            onClick = {
                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                onToggle()
            },
        )

        // Next Button
        Box(
            modifier = Modifier
                .size(52.dp)
                .tvFocusable(shape = controlShape, scale = 1.12f, onClick = {
                    haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onNext()
                })
                .clip(controlShape)
                .background(if (isCyberpunk) VeyraCyber.ink else Color.White.copy(alpha = 0.06f))
                .border(if (isCyberpunk) VeyraCyber.borderWidth else 1.dp, transportSlotBorder, controlShape),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(controlShape)
                    .background(transportDiscGradient)
                    .border(if (isCyberpunk) 0.dp else 1.dp, transportDiscBorder, controlShape)
                    .tactilePress(onClick = {
                        haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onNext()
                    }),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Rounded.SkipNext,
                    contentDescription = stringResource(R.string.playerx_next_track_cd),
                    tint = if (isCyberpunk) VeyraCyber.text else Color.White,
                    modifier = Modifier.size(26.dp),
                )
            }
        }

        // Repeat Toggle Button
        TransportToggleOption(
            icon = if (repeatMode == RepeatMode.ONE) Icons.Rounded.RepeatOne else Icons.Rounded.Repeat,
            description = stringResource(R.string.action_repeat),
            active = repeatMode != RepeatMode.OFF,
            enabled = localControls,
            isCyberpunk = isCyberpunk,
            onClick = {
                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onRepeat()
            },
        )
    }
}

@Composable
private fun HeroPlayButton(
    isPlaying: Boolean,
    isBuffering: Boolean,
    isCyberpunk: Boolean,
    onClick: () -> Unit,
) {
    val reduceMotion = LocalReducedMotion.current
    if (isCyberpunk) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(80.dp)
                .padding(end = 4.dp, bottom = 4.dp)
                .tvFocusable(shape = VeyraCyber.shape, scale = 1.06f, onClick = onClick),
        ) {
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .offset(4.dp, 4.dp)
                    .background(VeyraCyber.cyan, VeyraCyber.shape),
            )
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .clip(VeyraCyber.shape)
                    .background(VeyraCyber.acidYellow)
                    .border(VeyraCyber.borderWidth, VeyraCyber.ink, VeyraCyber.shape)
                    .tactilePress(onClick = onClick),
                contentAlignment = Alignment.Center,
            ) {
                if (isBuffering) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(32.dp),
                        color = VeyraCyber.ink,
                        strokeWidth = 3.dp,
                    )
                } else {
                    Crossfade(
                        targetState = isPlaying,
                        animationSpec = if (reduceMotion) snap() else tween(160),
                        label = "CyberPlayPauseIcon",
                    ) { playing ->
                        Icon(
                            imageVector = if (playing) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                            contentDescription = if (playing) {
                                stringResource(R.string.action_pause)
                            } else {
                                stringResource(R.string.action_play)
                            },
                            tint = VeyraCyber.ink,
                            modifier = Modifier.size(36.dp),
                        )
                    }
                }
            }
        }
        return
    }
    val playGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF8B5CF6),
            Color(0xFF6366F1),
            Color(0xFF38BDF8),
        ),
    )
    val infiniteTransition = androidx.compose.animation.core.rememberInfiniteTransition(label = "HeroAura")
    val auraAlpha by infiniteTransition.animateFloat(
        initialValue = 0.28f,
        targetValue = 0.58f,
        animationSpec = androidx.compose.animation.core.infiniteRepeatable(
            animation = tween(2400, easing = androidx.compose.animation.core.FastOutSlowInEasing),
            repeatMode = androidx.compose.animation.core.RepeatMode.Reverse,
        ),
        label = "HeroAuraPulse",
    )
    // Segundo breathing más lento para crear profundidad dual
    val auraAlphaSlow by infiniteTransition.animateFloat(
        initialValue = 0.18f,
        targetValue = 0.40f,
        animationSpec = androidx.compose.animation.core.infiniteRepeatable(
            animation = tween(4800, easing = androidx.compose.animation.core.FastOutSlowInEasing),
            repeatMode = androidx.compose.animation.core.RepeatMode.Reverse,
        ),
        label = "HeroAuraSlowPulse",
    )
    // Anillo orbital giratorio
    val orbitalAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = androidx.compose.animation.core.infiniteRepeatable(
            animation = tween(3600, easing = androidx.compose.animation.core.LinearEasing),
            repeatMode = androidx.compose.animation.core.RepeatMode.Restart,
        ),
        label = "HeroOrbitalAngle",
    )
    // Micro-scale pulse sincronizado con playback
    val scalePulse by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.025f,
        animationSpec = androidx.compose.animation.core.infiniteRepeatable(
            animation = tween(1800, easing = androidx.compose.animation.core.FastOutSlowInEasing),
            repeatMode = androidx.compose.animation.core.RepeatMode.Reverse,
        ),
        label = "HeroScalePulse",
    )
    val animatedScale by animateFloatAsState(
        targetValue = if (isPlaying && !reduceMotion) scalePulse else 1f,
        animationSpec = if (reduceMotion) snap() else tween(300),
        label = "HeroScaleAnim",
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(84.dp)
            .tvFocusable(shape = CircleShape, scale = 1.08f, onClick = onClick),
    ) {
        if (isPlaying && !reduceMotion) {
            // Halo exterior lento (capa profunda)
            Box(
                modifier = Modifier
                    .size(84.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                Color(0xFF1E6CEB).copy(alpha = auraAlphaSlow * 0.55f),
                                Color.Transparent,
                            ),
                        ),
                    ),
            )
            // Halo interior rápido (capa superficial)
            Box(
                modifier = Modifier
                    .size(84.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                Color(0xFF38BDF8).copy(alpha = auraAlpha * 0.75f),
                                Color(0xFF1E6CEB).copy(alpha = auraAlpha * 0.45f),
                                Color.Transparent,
                            ),
                        ),
                    ),
            )
            // Anillo orbital: arco giratorio sutil alrededor del botón
            androidx.compose.foundation.Canvas(
                modifier = Modifier.size(84.dp),
            ) {
                val stroke = 1.8.dp.toPx()
                val radius = (size.minDimension / 2f) - stroke
                if (radius > 0f) {
                    drawArc(
                        brush = Brush.sweepGradient(
                            0.0f to Color(0xFF38BDF8).copy(alpha = 0.60f),
                            0.35f to Color(0xFF1E6CEB).copy(alpha = 0.25f),
                            0.5f to Color.Transparent,
                            1.0f to Color.Transparent,
                        ),
                        startAngle = orbitalAngle,
                        sweepAngle = 120f,
                        useCenter = false,
                        style = androidx.compose.ui.graphics.drawscope.Stroke(
                            width = stroke,
                            cap = StrokeCap.Round,
                        ),
                        topLeft = androidx.compose.ui.geometry.Offset(stroke, stroke),
                        size = androidx.compose.ui.geometry.Size(radius * 2f, radius * 2f),
                    )
                }
            }
        }

        // Aurora frosted glass Play/Pause circle
        Box(
            modifier = Modifier
                .size(72.dp)
                .graphicsLayer {
                    scaleX = animatedScale
                    scaleY = animatedScale
                }
                .clip(CircleShape)
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color.White.copy(alpha = 0.24f),
                            Color.White.copy(alpha = 0.10f),
                        ),
                    ),
                )
                .border(
                    1.4.dp,
                    Brush.verticalGradient(
                        listOf(
                            Color.White.copy(alpha = 0.50f),
                            Color.White.copy(alpha = 0.14f),
                        ),
                    ),
                    CircleShape,
                )
                .tactilePress(onClick = onClick),
            contentAlignment = Alignment.Center,
        ) {
            if (isBuffering) {
                CircularProgressIndicator(
                    modifier = Modifier.size(34.dp),
                    color = Color.White,
                    strokeWidth = 3.dp,
                )
            } else {
                Crossfade(
                    targetState = isPlaying,
                    animationSpec = if (reduceMotion) snap() else tween(160),
                    label = "PlayPauseIcon",
                ) { playing ->
                    Icon(
                        imageVector = if (playing) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                        contentDescription = if (playing) {
                            stringResource(R.string.action_pause)
                        } else {
                            stringResource(R.string.action_play)
                        },
                        tint = Color.White,
                        modifier = Modifier.size(34.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun TransportToggleOption(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    description: String,
    active: Boolean,
    enabled: Boolean,
    isCyberpunk: Boolean = false,
    onClick: () -> Unit,
) {
    val activeColor by animateColorAsState(
        targetValue = when {
            !enabled -> Color.White.copy(alpha = 0.25f)
            active -> if (isCyberpunk) VeyraCyber.cyan else Color(0xFF38BDF8)
            else -> Color(0xFF94A3B8)
        },
        label = "ToggleActiveColor",
    )
    val optionShape = if (isCyberpunk) VeyraCyber.shape else CircleShape

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        // Platillo empotrado neumórfico / Cyber con área táctil >= 48dp
        Box(
            modifier = Modifier
                .size(VeyraSize.touchTarget)
                .tvFocusable(shape = optionShape, scale = 1.15f, onClick = if (enabled) onClick else null)
                .clip(optionShape)
                .background(if (isCyberpunk) VeyraCyber.surface else Color(0xFF080C14))
                .border(
                    if (isCyberpunk) VeyraCyber.borderWidth else 1.dp,
                    if (isCyberpunk) {
                        Brush.verticalGradient(
                            listOf(
                                if (active && enabled) VeyraCyber.cyan else VeyraCyber.textMuted.copy(alpha = 0.35f),
                                if (active && enabled) VeyraCyber.cyan else VeyraCyber.textMuted.copy(alpha = 0.35f),
                            ),
                        )
                    } else {
                        Brush.verticalGradient(
                            listOf(Color.Black.copy(alpha = 0.8f), Color.White.copy(alpha = 0.07f)),
                        )
                    },
                    optionShape,
                ),
            contentAlignment = Alignment.Center,
        ) {
            // Disco interior táctil
            val discGradient = if (isCyberpunk) {
                Brush.verticalGradient(
                    listOf(
                        if (active && enabled) VeyraCyber.surfaceAlt else VeyraCyber.surface,
                        if (active && enabled) VeyraCyber.surfaceAlt else VeyraCyber.surface,
                    ),
                )
            } else if (active && enabled) {
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF1E6CEB).copy(alpha = 0.45f),
                        Color(0xFF0F3A78).copy(alpha = 0.60f),
                    ),
                )
            } else {
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF151D2D),
                        Color(0xFF0E1422),
                    ),
                )
            }
            val discBorder = if (active && enabled) {
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF38BDF8).copy(alpha = 0.65f),
                        Color(0xFF1E6CEB).copy(alpha = 0.25f),
                    ),
                )
            } else {
                Brush.verticalGradient(
                    listOf(
                        Color.White.copy(alpha = 0.08f),
                        Color.Black.copy(alpha = 0.35f),
                    ),
                )
            }

            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(optionShape)
                    .background(discGradient)
                    .then(if (isCyberpunk) Modifier else Modifier.border(1.dp, discBorder, optionShape))
                    .tactilePress(enabled = enabled, onClick = onClick),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = description,
                    tint = activeColor,
                    modifier = Modifier.size(20.dp),
                )
            }
        }
        Spacer(Modifier.height(5.dp))
        // Micro-LED de estado con halo óptico
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(8.dp),
        ) {
            if (active && enabled) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF38BDF8).copy(alpha = 0.30f)),
                )
            }
            Box(
                modifier = Modifier
                    .size(3.5.dp)
                    .clip(CircleShape)
                    .background(
                        if (active && enabled) Color(0xFF38BDF8) else Color.White.copy(alpha = 0.12f),
                    ),
            )
        }
    }
}
