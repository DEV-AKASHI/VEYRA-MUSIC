/*
 * Copyright (C) 2026 SFG545 / AKASHI DEV
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
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp

/**
 * Conmutador táctil neumórfico para Veyra Music (Obsidian Industrial).
 * Canal empotrado profundo (#090D15) con disco convexo y micro-LED indicador Sky Flare (#38BDF8).
 */
@Composable
fun NeumorphicSwitch(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val haptic = LocalHapticFeedback.current
    val trackWidth = 50.dp
    val trackHeight = 28.dp
    val thumbSize = 22.dp
    val thumbPadding = 3.dp

    val thumbOffset by animateDpAsState(
        targetValue = if (checked) trackWidth - thumbSize - thumbPadding else thumbPadding,
        animationSpec = spring(dampingRatio = 0.72f, stiffness = 550f),
        label = "NeumorphicSwitchThumbOffset",
    )

    val ledColor by animateColorAsState(
        targetValue = if (checked) Color(0xFF38BDF8) else Color(0xFF334155),
        animationSpec = tween(180),
        label = "NeumorphicSwitchLedColor",
    )

    val isCyberpunk = LocalCyberpunkTheme.current
    val switchShape = if (isCyberpunk) dev.sfg.veyra.mobile.ui.theme.VeyraCyber.shape else RoundedCornerShape(999.dp)
    val trackBorderColor by animateColorAsState(
        targetValue = if (isCyberpunk) {
            if (checked) dev.sfg.veyra.mobile.ui.theme.VeyraCyber.cyan else dev.sfg.veyra.mobile.ui.theme.VeyraCyber.textMuted.copy(alpha = 0.55f)
        } else {
            if (checked) Color(0x8838BDF8) else Color(0x38FFFFFF)
        },
        animationSpec = tween(200),
        label = "SwitchBorder",
    )
    val trackFillColor by animateColorAsState(
        targetValue = if (isCyberpunk) {
            if (checked) dev.sfg.veyra.mobile.ui.theme.VeyraCyber.surfaceAlt else dev.sfg.veyra.mobile.ui.theme.VeyraCyber.ink
        } else {
            if (checked) Color(0xAA7C3AED) else Color.White.copy(alpha = 0.12f)
        },
        animationSpec = tween(200),
        label = "SwitchTrackFill",
    )

    Box(
        modifier = modifier
            .size(width = trackWidth, height = trackHeight)
            .clip(switchShape)
            .clickable(
                enabled = enabled && onCheckedChange != null,
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
            ) {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onCheckedChange?.invoke(!checked)
            }
            .background(trackFillColor)
            .border(if (isCyberpunk) 2.dp else 1.dp, trackBorderColor, switchShape),
        contentAlignment = Alignment.CenterStart,
    ) {
        val thumbShape = if (isCyberpunk) dev.sfg.veyra.mobile.ui.theme.VeyraCyber.shape else CircleShape
        Box(
            modifier = Modifier
                .offset(x = thumbOffset)
                .size(thumbSize)
                .shadow(elevation = if (isCyberpunk) 0.dp else 4.dp, shape = thumbShape, spotColor = Color.Black)
                .clip(thumbShape)
                .background(
                    if (isCyberpunk) {
                        if (checked) dev.sfg.veyra.mobile.ui.theme.VeyraCyber.acidYellow else dev.sfg.veyra.mobile.ui.theme.VeyraCyber.surface
                    } else {
                        Color.White.copy(alpha = if (checked) 0.96f else 0.78f)
                    }
                )
                .border(
                    width = if (isCyberpunk) 1.5.dp else 1.dp,
                    color = if (isCyberpunk) dev.sfg.veyra.mobile.ui.theme.VeyraCyber.ink else Color.White.copy(alpha = 0.55f),
                    shape = thumbShape,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier
                    .size(5.dp)
                    .clip(thumbShape)
                    .background(if (isCyberpunk) dev.sfg.veyra.mobile.ui.theme.VeyraCyber.ink else ledColor)
                    .then(
                        if (checked) {
                            Modifier.drawBehind {
                                drawCircle(
                                    color = Color(0x6638BDF8),
                                    radius = size.minDimension * 1.6f,
                                )
                            }
                        } else Modifier
                    ),
            )
        }
    }
}
