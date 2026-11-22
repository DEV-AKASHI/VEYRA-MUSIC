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
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
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
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import dev.sfg.veyra.connect.R
import dev.sfg.veyra.mobile.model.PlaybackSnapshot
import dev.sfg.veyra.mobile.ui.glass.GlassTone
import dev.sfg.veyra.mobile.ui.glass.LocalGlass
import dev.sfg.veyra.mobile.ui.glass.glassFill
import dev.sfg.veyra.mobile.ui.glass.glassPane
import dev.sfg.veyra.mobile.ui.navigation.Routes
import dev.sfg.veyra.mobile.ui.theme.CanopyColors
import dev.sfg.veyra.mobile.ui.theme.LocalAccent
import dev.sfg.veyra.mobile.ui.theme.LocalReducedMotion
import dev.sfg.veyra.mobile.ui.theme.VeyraCyber
import dev.sfg.veyra.mobile.ui.theme.VeyraRadius
import dev.sfg.veyra.mobile.ui.theme.VeyraSemantic
import dev.sfg.veyra.mobile.ui.theme.VeyraSize
import dev.sfg.veyra.mobile.ui.theme.VeyraSpace
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.roundToInt
import kotlin.math.sin

data class BottomDestination(val route: String, val labelRes: Int, val iconKind: VeyraIconKind)

private val standardDestinations = listOf(
    BottomDestination(Routes.HOME, R.string.nav_home, VeyraIconKind.HOME),
    BottomDestination(Routes.SEARCH, R.string.nav_search, VeyraIconKind.SEARCH),
    BottomDestination(Routes.LIBRARY, R.string.nav_library, VeyraIconKind.LIBRARY),
    BottomDestination(Routes.SETTINGS, R.string.nav_settings, VeyraIconKind.SETTINGS),
)

private val glassDestinations = listOf(
    BottomDestination(Routes.HOME, R.string.nav_home, VeyraIconKind.HOME),
    BottomDestination(Routes.SEARCH, R.string.nav_search, VeyraIconKind.SEARCH),
    BottomDestination(Routes.LIBRARY, R.string.nav_library, VeyraIconKind.LIBRARY),
    BottomDestination(Routes.DEVICES, R.string.nav_connect, VeyraIconKind.CONNECT),
    BottomDestination(Routes.SETTINGS, R.string.nav_settings, VeyraIconKind.SETTINGS),
)

/**
 * Altura combinada del mini reproductor y la barra de navegación flotante.
 */
val VeyraChromeHeight = 132.dp
val OrchardChromeHeight = VeyraChromeHeight

val LocalCyberpunkTheme = staticCompositionLocalOf { false }
val LocalLiquidAuroraTheme = staticCompositionLocalOf { true }

private val LiquidBottomBarShape = RoundedCornerShape(34.dp)
private val MiniPlayerShape = RoundedCornerShape(32.dp)

@Composable
fun OrchardBottomBar(currentRoute: String?, onSelect: (String) -> Unit) =
    VeyraBottomBar(currentRoute, onSelect)

@Composable
fun VeyraBottomBar(currentRoute: String?, onSelect: (String) -> Unit) {
    val glass = LocalGlass.current.enabled
    val glassTint = LocalGlass.current.tint
    val isCyberpunk = LocalCyberpunkTheme.current
    val isLiquidAurora = LocalLiquidAuroraTheme.current && !isCyberpunk
    val reduceMotion = LocalReducedMotion.current
    val destinations = if (glass) glassDestinations else standardDestinations

    val selectedIndex = remember(currentRoute, destinations) {
        val idx = destinations.indexOfFirst { it.route == currentRoute }
        if (idx != -1) idx
        else if (currentRoute == Routes.SETTINGS_HOME_LAYOUT) {
            destinations.indexOfFirst { it.route == Routes.SETTINGS }.takeIf { it != -1 }
        } else {
            null
        }
    }
    var lastKnownIndex by remember { mutableIntStateOf(selectedIndex ?: 0) }
    LaunchedEffect(selectedIndex) {
        if (selectedIndex != null) {
            lastKnownIndex = selectedIndex
        }
    }
    val activeIndex = selectedIndex ?: lastKnownIndex

    val tintColor = if (isCyberpunk) {
        VeyraCyber.cyan
    } else if (glassTint != Color.Unspecified && glassTint.alpha > 0f) {
        glassTint
    } else {
        LocalAccent.current
    }
    val barShape = if (isCyberpunk) VeyraCyber.shape else LiquidBottomBarShape

    var glitchBurst by remember { mutableIntStateOf(0) }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (isCyberpunk) {
                    Modifier
                        .padding(horizontal = VeyraSpace.md, vertical = VeyraSpace.xs)
                        .cyberGlitchBurst(glitchBurst)
                        .clip(barShape)
                        .background(VeyraCyber.canvas, barShape)
                        .border(VeyraCyber.borderWidth, VeyraCyber.cyan.copy(alpha = 0.80f), barShape)
                } else {
                    Modifier
                        .padding(horizontal = VeyraSpace.md, vertical = VeyraSpace.xs)
                        .then(if (glass) Modifier.glassPane(barShape, GlassTone.CHROME) else Modifier.clip(barShape))
                        .background(
                            brush = Brush.verticalGradient(
                                listOf(
                                    androidx.compose.ui.graphics.lerp(Color(0xDE130F28), tintColor, 0.15f),
                                    Color(0xEC0B091A),
                                ),
                            ),
                            shape = barShape,
                        )
                        .border(
                            width = 1.1.dp,
                            brush = Brush.verticalGradient(
                                listOf(
                                    Color.White.copy(alpha = 0.44f),
                                    Color.White.copy(alpha = 0.12f),
                                    tintColor.copy(alpha = 0.24f),
                                ),
                            ),
                            shape = barShape,
                        )
                },
            ),
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp),
        ) {
            val count = destinations.size
            val slotWidth = maxWidth / count
            val indicatorWidth = if (slotWidth - 8.dp < 56.dp) slotWidth - 8.dp else 56.dp
            val indicatorHeight = 32.dp
            val targetOffset = (slotWidth * (activeIndex + 0.5f)) - (indicatorWidth / 2)

            val animatedOffset by animateDpAsState(
                targetValue = targetOffset,
                animationSpec = if (reduceMotion) snap() else spring(
                    dampingRatio = 0.78f,
                    stiffness = 380f,
                ),
                label = "BottomBarIndicatorX",
            )
            val indicatorAlpha by animateFloatAsState(
                targetValue = if (selectedIndex != null) 1f else 0f,
                animationSpec = if (reduceMotion) snap() else tween(180),
                label = "BottomBarIndicatorAlpha",
            )

            if (indicatorAlpha > 0.001f) {
                val indicatorShape = if (isCyberpunk) CyberCutCompactShape else CircleShape
                if (!isCyberpunk) {
                    Box(
                        modifier = Modifier
                            .offset(x = animatedOffset - 6.dp, y = 2.dp)
                            .size(width = indicatorWidth + 12.dp, height = indicatorHeight + 10.dp)
                            .alpha(indicatorAlpha * 0.72f)
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        Color(0xFF8B5CF6).copy(alpha = 0.44f),
                                        Color(0xFF38BDF8).copy(alpha = 0.28f),
                                        Color.Transparent,
                                    ),
                                ),
                                shape = CircleShape,
                            ),
                    )
                }
                Box(
                    modifier = Modifier
                        .offset(x = animatedOffset, y = 6.dp)
                        .size(width = indicatorWidth, height = indicatorHeight)
                        .alpha(indicatorAlpha)
                        .background(
                            brush = if (isCyberpunk) {
                                Brush.horizontalGradient(listOf(VeyraCyber.acidYellow, VeyraCyber.acidYellow))
                            } else {
                                Brush.horizontalGradient(
                                    listOf(
                                        Color(0xFF8B5CF6).copy(alpha = 0.60f),
                                        Color(0xFF38BDF8).copy(alpha = 0.48f),
                                    ),
                                )
                            },
                            shape = indicatorShape,
                        )
                        .border(
                            width = if (isCyberpunk) VeyraCyber.borderWidth else 1.dp,
                            brush = if (isCyberpunk) {
                                Brush.verticalGradient(listOf(VeyraCyber.ink, VeyraCyber.ink))
                            } else {
                                Brush.verticalGradient(
                                    listOf(
                                        Color.White.copy(alpha = 0.56f),
                                        Color(0xFF38BDF8).copy(alpha = 0.36f),
                                    ),
                                )
                            },
                            shape = indicatorShape,
                        ),
                )
            }

            NavigationBar(
                containerColor = Color.Transparent,
                tonalElevation = 0.dp,
                // The Scaffold already insets the whole chrome column above the system navigation bar.
                // Letting the bar apply that inset a second time would eat into its fixed 64dp height,
                // which squashed the icons and labels under 3-button navigation.
                windowInsets = WindowInsets(0),
                modifier = Modifier.height(64.dp),
            ) {
                val haptic = androidx.compose.ui.platform.LocalHapticFeedback.current
                destinations.forEach { destination ->
                    val isSelected = currentRoute == destination.route ||
                        (destination.route == Routes.SETTINGS && currentRoute == Routes.SETTINGS_HOME_LAYOUT)
                    val labelText = stringResource(destination.labelRes)
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = {
                            if (!isSelected) {
                                if (isCyberpunk) glitchBurst++
                                haptic.performHapticFeedback(
                                    androidx.compose.ui.hapticfeedback.HapticFeedbackType.TextHandleMove,
                                )
                            }
                            onSelect(destination.route)
                        },
                        icon = {
                            val iconScale by animateFloatAsState(
                                targetValue = if (isSelected && !reduceMotion) 1.08f else 1.0f,
                                animationSpec = if (reduceMotion) snap() else spring(dampingRatio = 0.50f, stiffness = 500f),
                                label = "NavIconScale",
                            )
                            val iconColor = when {
                                isCyberpunk && isSelected -> VeyraCyber.ink
                                isSelected -> Color.White
                                else -> Color.White.copy(alpha = 0.72f)
                            }
                            VeyraGlyphIcon(
                                kind = destination.iconKind,
                                tint = iconColor,
                                selected = isSelected,
                                contentDescription = labelText,
                                modifier = Modifier
                                    .size(22.dp)
                                    .scale(iconScale),
                            )
                        },
                        label = {
                            Text(
                                text = labelText,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                maxLines = 1,
                                softWrap = false,
                                overflow = TextOverflow.Ellipsis,
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = if (isCyberpunk) VeyraCyber.ink else Color.White,
                            selectedTextColor = if (isCyberpunk) VeyraCyber.acidYellow else Color.White,
                            indicatorColor = Color.Transparent,
                            unselectedIconColor = Color.White.copy(alpha = 0.72f),
                            unselectedTextColor = Color.White.copy(alpha = 0.72f),
                        ),
                    )
                }
            }
        }
    }
}

/** Expressive floating mini-player component matching SimpMusic design. */
@Composable
fun MiniPlayer(
    playback: PlaybackSnapshot,
    onTogglePlay: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    transition: dev.sfg.veyra.mobile.model.TransitionMarker? = null,
    /** Raw overlap progress, retained after visible identity has moved to the incoming track. */
    mixProgress: Float? = null,
    /** Reports the thumbnail's place on screen so the full player can fly its cover into it. */
    onArtworkBounds: ((Rect) -> Unit)? = null,
    onClear: () -> Unit = {},
) {
    val track = playback.currentTrack ?: return
    var dragOffsetY by remember { mutableFloatStateOf(0f) }
    var isDragging by remember { mutableStateOf(false) }
    val settleAnim = remember { Animatable(0f) }
    val coroutineScope = rememberCoroutineScope()
    val density = LocalDensity.current

    val currentOffsetY = if (isDragging) dragOffsetY.coerceAtLeast(0f) else settleAnim.value.coerceAtLeast(0f)
    val dismissThresholdPx = with(density) { 44.dp.toPx() }
    val dismissVelocityPx = with(density) { 300.dp.toPx() }
    val fullDismissPx = with(density) { 80.dp.toPx() }

    val marker = transition?.takeIf {
        it.trackId.isNotBlank() && it.trackId == track.id && it.startMs > 0 && it.startMs < playback.durationMs
    }
    val effectiveDuration = (marker?.startMs ?: playback.durationMs).coerceAtLeast(1)

    val progress = if (effectiveDuration > 0) {
        (playback.positionMs.toFloat() / effectiveDuration.toFloat()).coerceIn(0f, 1f)
    } else 0f

    val reduceMotion = LocalReducedMotion.current
    val isCyberpunk = LocalCyberpunkTheme.current
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = if (reduceMotion) snap() else spring(),
        label = "MiniPlayerProgress",
    )
    // The pill picks up the cover's colours so it reads as part of the artwork.
    val palette = rememberArtworkPalette(track.artworkUrl)
    val glass = LocalGlass.current.enabled && !isCyberpunk
    val infiniteTransition = rememberInfiniteTransition(label = "MiniPlayerWave")
    val wavePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "MiniPlayerWavePhase",
    )
    val waveAmplitude by animateFloatAsState(
        targetValue = if (playback.isPlaying && !reduceMotion) 1.8f else 0f,
        animationSpec = if (reduceMotion) snap() else spring(dampingRatio = 0.85f, stiffness = Spring.StiffnessMediumLow),
        label = "MiniPlayerWaveAmplitude",
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

    val trackDescription = "${track.title}, ${dev.sfg.veyra.mobile.ui.components.localizedArtistName(track.artist)}"
    val pillShape = if (isCyberpunk) VeyraCyber.shape else MiniPlayerShape
    Card(
        shape = pillShape,
        colors = CardDefaults.cardColors(
            containerColor = if (isCyberpunk) VeyraCyber.surface else Color.White.copy(alpha = 0.10f),
        ),
        border = androidx.compose.foundation.BorderStroke(
            if (isCyberpunk) VeyraCyber.borderWidth else 1.dp,
            if (isCyberpunk) VeyraCyber.cyan else Color.White.copy(alpha = 0.28f),
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = VeyraSpace.md, vertical = VeyraSpace.xs)
            .offset { IntOffset(0, currentOffsetY.roundToInt()) }
            .alpha(dismissAlpha)
            .then(dragModifier)
            .then(if (glass) Modifier.glassPane(pillShape, GlassTone.CHROME) else Modifier)
            .clip(pillShape)
            .semantics {
                role = Role.Button
                contentDescription = trackDescription
            }
            .clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            lerp(palette.accent, Color.White, 0.18f),
                            lerp(palette.bottom, Color.White, 0.06f),
                        ),
                    ),
                    alpha = if (isCyberpunk) 0.18f else 0.26f,
                ),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AnimatedContent(
                    targetState = track.artworkUrl to track.title,
                    transitionSpec = {
                        fadeIn(animationSpec = tween(350)) togetherWith fadeOut(animationSpec = tween(250))
                    },
                    label = "MiniPlayerArtworkCrossfade",
                ) { (artUrl, artTitle) ->
                    ArtworkTile(
                        url = artUrl,
                        description = artTitle,
                        modifier = Modifier
                            .size(44.dp)
                            .onGloballyPositioned { onArtworkBounds?.invoke(it.boundsInRoot()) },
                        radius = 10
                    )
                }
                Spacer(Modifier.width(10.dp))
                AnimatedContent(
                    targetState = track.title to track.artist,
                    transitionSpec = {
                        (fadeIn(animationSpec = tween(350, delayMillis = 40)) + slideInVertically(animationSpec = tween(350, delayMillis = 40)) { it / 3 }) togetherWith
                            (fadeOut(animationSpec = tween(200)) + slideOutVertically(animationSpec = tween(200)) { -it / 3 })
                    },
                    label = "MiniPlayerTrackTransition",
                    modifier = Modifier.weight(1f),
                ) { (title, artist) ->
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
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
                        Text(
                            text = dev.sfg.veyra.mobile.ui.components.localizedArtistName(artist),
                            style = MaterialTheme.typography.bodySmall,
                            color = CanopyColors.Muted,
                            maxLines = 1
                        )
                    }
                }
                Spacer(Modifier.width(6.dp))
                val miniPlayShape = if (isCyberpunk) CyberCutCompactShape else CircleShape
                val miniPlayGradient = if (isCyberpunk) {
                    Brush.linearGradient(colors = listOf(VeyraCyber.acidYellow, VeyraCyber.acidYellow))
                } else {
                    Brush.linearGradient(colors = listOf(Color(0xFF8B5CF6), Color(0xFF38BDF8)))
                }
                IconButton(
                    onClick = onTogglePlay,
                    modifier = Modifier.size(48.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(miniPlayShape)
                            .background(miniPlayGradient)
                            .border(
                                if (isCyberpunk) VeyraCyber.borderWidth else 1.dp,
                                if (isCyberpunk) VeyraCyber.ink else Color.White.copy(alpha = 0.36f),
                                miniPlayShape,
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        androidx.compose.animation.Crossfade(
                            targetState = playback.isPlaying,
                            animationSpec = if (reduceMotion) snap() else tween(180),
                            label = "MiniPlayCrossfade",
                        ) { playing ->
                            VeyraGlyphIcon(
                                kind = if (playing) VeyraIconKind.PAUSE else VeyraIconKind.PLAY,
                                tint = if (isCyberpunk) VeyraCyber.ink else Color.White,
                                filled = true,
                                contentDescription = if (playing) {
                                    stringResource(R.string.action_pause)
                                } else {
                                    stringResource(R.string.action_play)
                                },
                                modifier = Modifier.size(22.dp),
                            )
                        }
                    }
                }
            }

            // Onda sinusoidal activa en la base del MiniPlayer (sin allocations por frame)
            val glow = rememberTransitionGlow(mixProgress ?: transitionProgress(playback, marker))
            val miniWavePath = remember { Path() }
            val miniWaveBrush = remember {
                Brush.horizontalGradient(
                    colors = listOf(
                        Color(0xFF1E6CEB),
                        Color(0xFF2275F8),
                        Color(0xFF38BDF8),
                    ),
                )
            }
            val rainbowBrush = rememberRainbowBrush()
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 14.dp),
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height
                    if (w <= 2f || h <= 2f) return@Canvas
                    val midY = h / 2f
                    val progressX = (w * animatedProgress.coerceIn(0f, 1f)).coerceIn(0f, w)

                    drawLine(
                        color = Color.White.copy(alpha = 0.12f),
                        start = Offset(0f, midY),
                        end = Offset(w, midY),
                        strokeWidth = 2.dp.toPx(),
                        cap = StrokeCap.Round,
                    )

                    if (progressX > 1f) {
                        miniWavePath.reset()
                        miniWavePath.moveTo(0f, midY)
                        if (waveAmplitude > 0.05f && progressX > 10f) {
                            val wavelength = 48.dp.toPx().coerceAtLeast(10f)
                            val ampPx = waveAmplitude.dp.toPx().coerceIn(0f, (h / 2f) - 1f)
                            val k = (2 * PI / wavelength).toFloat()
                            val leadIn = 14.dp.toPx()
                            var x = 1.2f
                            while (x <= progressX) {
                                val tIn = (x / leadIn).coerceIn(0f, 1f)
                                val tOut = ((progressX - x) / leadIn).coerceIn(0f, 1f)
                                val env = (tIn * tIn * (3f - 2f * tIn)) * (tOut * tOut * (3f - 2f * tOut))
                                val y = (midY + sin(k * x - wavePhase) * ampPx * env).coerceIn(1f, h - 1f)
                                miniWavePath.lineTo(x, y)
                                x += 1.2f
                            }
                        }
                        miniWavePath.lineTo(progressX, midY)

                        drawPath(
                            path = miniWavePath,
                            brush = if (glow > 0.01f) rainbowBrush else miniWaveBrush,
                            style = Stroke(width = 2.2.dp.toPx(), cap = StrokeCap.Round),
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PlaybackTargetLabel(name: String, isLocal: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        color = CanopyColors.Surface,
        shape = CircleShape,
        border = androidx.compose.foundation.BorderStroke(1.dp, CanopyColors.Rule),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                Modifier
                    .size(8.dp)
                    .background(
                        if (isLocal) CanopyColors.Muted else LocalAccent.current,
                        CircleShape,
                    ),
            )
            Spacer(Modifier.width(8.dp))
            Text(
                name,
                style = MaterialTheme.typography.labelSmall,
                color = if (isLocal) CanopyColors.Text else LocalAccent.current,
            )
        }
    }
}
