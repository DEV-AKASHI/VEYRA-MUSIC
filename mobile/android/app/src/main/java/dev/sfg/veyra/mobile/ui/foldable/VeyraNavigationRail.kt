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

package dev.sfg.veyra.mobile.ui.foldable

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Groups
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.LibraryMusic
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.sfg.veyra.connect.R
import dev.sfg.veyra.mobile.ui.components.LocalCyberpunkTheme
import dev.sfg.veyra.mobile.ui.components.OrchardMark
import dev.sfg.veyra.mobile.ui.glass.GlassTone
import dev.sfg.veyra.mobile.ui.glass.LocalGlass
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

import dev.sfg.veyra.mobile.ui.tv.tvNavRailFocusable
import dev.sfg.veyra.mobile.ui.tv.LocalIsTv

data class RailDestination(val route: String, val labelRes: Int, val icon: ImageVector)

private val foldableDestinations = listOf(
    RailDestination(Routes.HOME, R.string.nav_home, Icons.Rounded.Home),
    RailDestination(Routes.SEARCH, R.string.nav_search, Icons.Rounded.Search),
    RailDestination(Routes.LIBRARY, R.string.nav_library, Icons.Rounded.LibraryMusic),
    RailDestination(Routes.DEVICES, R.string.nav_connect, Icons.Rounded.Groups),
    RailDestination(Routes.SETTINGS, R.string.nav_settings, Icons.Rounded.Person),
)

private val NavRailShape = RoundedCornerShape(28.dp)

/**
 * Vertical navigation rail designed for foldable devices (unfolded) and tablets.
 * Sits elegantly along the left edge, replacing the bottom bar and freeing up
 * valuable vertical screen real estate.
 */
@Composable
fun VeyraNavigationRail(
    currentRoute: String?,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val glass = LocalGlass.current.enabled
    val glassTint = LocalGlass.current.tint
    val isCyberpunk = LocalCyberpunkTheme.current
    val tintColor = if (isCyberpunk) {
        VeyraCyber.cyan
    } else if (glassTint != Color.Unspecified && glassTint.alpha > 0f) {
        glassTint
    } else {
        LocalAccent.current
    }
    val reduceMotion = LocalReducedMotion.current

    val selectedIndex = remember(currentRoute) {
        val idx = foldableDestinations.indexOfFirst { it.route == currentRoute }
        if (idx != -1) idx
        else if (currentRoute == Routes.SETTINGS_HOME_LAYOUT) {
            foldableDestinations.indexOfFirst { it.route == Routes.SETTINGS }.takeIf { it != -1 }
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
    val isTv = LocalIsTv.current
    val destinationFocusRequesters = remember { List(foldableDestinations.size) { FocusRequester() } }
    LaunchedEffect(selectedIndex, isTv) {
        if (isTv && selectedIndex != null) {
            runCatching { destinationFocusRequesters[selectedIndex].requestFocus() }
        }
    }

    val railBackdrop = remember(glass, tintColor, isCyberpunk) {
        if (isCyberpunk) {
            Brush.verticalGradient(listOf(VeyraCyber.canvas, VeyraCyber.ink))
        } else {
            Brush.verticalGradient(
                listOf(
                    tintColor.copy(alpha = if (glass) 0.22f else 0.12f),
                    VeyraSemantic.canvasAlt.copy(alpha = if (glass) 0.78f else 0.94f),
                    VeyraSemantic.canvas.copy(alpha = if (glass) 0.84f else 0.97f),
                ),
            )
        }
    }
    val itemShape = if (isCyberpunk) VeyraCyber.shape else VeyraRadius.mdShape

    Box(
        modifier = modifier
            .fillMaxHeight()
            .width(FoldableNavRailWidth)
            .then(
                if (glass && !isCyberpunk) Modifier.glassPane(RoundedCornerShape(0.dp), GlassTone.CHROME) else Modifier
            )
            .background(railBackdrop)
            .drawWithContent {
                drawContent()
                drawLine(
                    color = if (isCyberpunk) VeyraCyber.cyan.copy(alpha = 0.45f) else VeyraSemantic.borderStrong,
                    start = Offset(size.width, 0f),
                    end = Offset(size.width, size.height),
                    strokeWidth = if (isCyberpunk) VeyraCyber.borderWidth.toPx() else VeyraSize.hairline.toPx(),
                )
            },
        contentAlignment = Alignment.TopCenter,
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .width(FoldableNavRailWidth)
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(vertical = VeyraSpace.lg),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            val homeLabel = stringResource(R.string.nav_home)
            Box(
                modifier = Modifier
                    .size(VeyraSize.touchTarget)
                    .clip(CircleShape)
                    .semantics {
                        role = Role.Button
                        contentDescription = homeLabel
                    }
                    .tvNavRailFocusable(isSelected = false, shape = CircleShape) {
                        onSelect(Routes.HOME)
                    },
                contentAlignment = Alignment.Center,
            ) {
                OrchardMark(Modifier.size(34.dp))
            }

            Spacer(Modifier.height(VeyraSpace.sm))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                foldableDestinations.forEachIndexed { index, destination ->
                    val isSelected = selectedIndex == index
                    val labelText = stringResource(destination.labelRes)
                    val unselectedColor = if (isCyberpunk) {
                        VeyraCyber.textMuted
                    } else if (glass) {
                        Color.White.copy(alpha = 0.72f)
                    } else {
                        CanopyColors.Muted
                    }
                    val itemColor by animateColorAsState(
                        targetValue = if (isSelected) {
                            if (isCyberpunk) VeyraCyber.cyan else VeyraSemantic.textPrimary
                        } else {
                            unselectedColor
                        },
                        animationSpec = if (reduceMotion) snap() else tween(180),
                        label = "RailItemColor_${destination.route}",
                    )

                    val pillBg = if (isSelected) {
                        if (isCyberpunk) VeyraCyber.surfaceAlt else tintColor.copy(alpha = 0.30f)
                    } else {
                        Color.Transparent
                    }

                    Surface(
                        shape = itemShape,
                        color = pillBg,
                        border = if (isSelected) {
                            BorderStroke(
                                if (isCyberpunk) VeyraCyber.borderWidth else VeyraSize.hairline,
                                if (isCyberpunk) VeyraCyber.cyan else VeyraSemantic.focus.copy(alpha = 0.55f),
                            )
                        } else null,
                        modifier = Modifier
                            .size(width = 64.dp, height = 56.dp)
                            .semantics(mergeDescendants = true) {
                                selected = isSelected
                                role = Role.Tab
                                contentDescription = labelText
                            }
                            .then(if (isTv) Modifier.focusRequester(destinationFocusRequesters[index]) else Modifier)
                            .tvNavRailFocusable(isSelected = isSelected, shape = itemShape) {
                                onSelect(destination.route)
                            },
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = VeyraSpace.xs),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Icon(
                                destination.icon,
                                contentDescription = null,
                                tint = itemColor,
                                modifier = Modifier.size(VeyraSize.icon),
                            )
                            Spacer(Modifier.height(VeyraSpace.xxs))
                            Text(
                                text = labelText,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = itemColor,
                                maxLines = 1,
                                softWrap = false,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    }
                }
            }
        }
    }
}
