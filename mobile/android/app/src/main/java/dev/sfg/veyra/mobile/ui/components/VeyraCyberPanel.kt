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

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import dev.sfg.veyra.mobile.ui.theme.LocalReducedMotion
import dev.sfg.veyra.mobile.ui.theme.VeyraAlpha
import dev.sfg.veyra.mobile.ui.theme.VeyraCyber
import dev.sfg.veyra.mobile.ui.theme.VeyraSemantic
import dev.sfg.veyra.mobile.ui.theme.VeyraSpace

/**
 * Panel Neo-Brutalism + Cyberpunk. Bloque de borde duro, bloque de sombra
 * sólido y acento neón único; sin blur ni degradados suaves.
 *
 * Uso restringido a lo que autorice la matriz de Fase 0/1. Al pulsar, el bloque
 * de sombra se recoge (reduce motion lo hace instantáneo).
 */
@Composable
fun CyberPanel(
    modifier: Modifier = Modifier,
    accent: Color = VeyraCyber.cyan,
    shape: Shape = VeyraCyber.shape,
    selected: Boolean = false,
    enabled: Boolean = true,
    contentPadding: PaddingValues = PaddingValues(VeyraSpace.lg),
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val reducedMotion = LocalReducedMotion.current
    val haptics = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    var focused by remember { mutableStateOf(false) }

    val shadowOffset by animateDpAsState(
        targetValue = if (pressed && enabled && onClick != null) 0.dp else VeyraCyber.hardShadow,
        animationSpec = if (reducedMotion) snap() else tween(90),
        label = "CyberShadowOffset",
    )

    val interactionModifier = if (onClick != null) {
        Modifier
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                role = Role.Button,
            ) {
                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onClick()
            }
            .onFocusChanged { focused = it.isFocused }
    } else {
        Modifier
    }

    Box(
        modifier = modifier.padding(
            end = VeyraCyber.hardShadow,
            bottom = VeyraCyber.hardShadow,
        ),
    ) {
        Box(
            Modifier
                .matchParentSize()
                .offset(shadowOffset, shadowOffset)
                .background(accent, shape),
        )
        Column(
            modifier = Modifier
                .graphicsLayer { alpha = if (enabled) 1f else VeyraAlpha.disabledSurface }
                .background(VeyraCyber.surface, shape)
                .border(
                    VeyraCyber.borderWidth,
                    if (selected) accent else accent.copy(alpha = 0.82f),
                    shape,
                )
                .then(
                    if (focused) Modifier.border(VeyraCyber.borderWidth, VeyraSemantic.focus, shape) else Modifier,
                )
                .clip(shape)
                .then(interactionModifier)
                .padding(contentPadding),
            content = content,
        )
    }
}
