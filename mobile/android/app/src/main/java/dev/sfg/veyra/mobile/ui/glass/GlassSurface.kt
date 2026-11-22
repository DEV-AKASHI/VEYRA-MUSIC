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

package dev.sfg.veyra.mobile.ui.glass

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import dev.sfg.veyra.mobile.ui.theme.CanopyColors
import dev.sfg.veyra.mobile.ui.theme.LocalAccent
import dev.sfg.veyra.mobile.ui.theme.LocalReducedMotion
import dev.sfg.veyra.mobile.ui.theme.VeyraRadius
import dev.sfg.veyra.mobile.ui.theme.VeyraSize

/**
 * Superficie glass compartida.
 *
 * Con el efecto cristal activo delega en [glassPane]; apagado, cae a una
 * superficie Slate sólida con borde hairline. En ambos casos conserva el mismo
 * contrato: forma, estados press/focus/disabled/selected y clipping de contenido.
 *
 * @param onClick si se pasa, la superficie es interactiva (rol Button) y añade
 *   feedback háptico y compresión elástica.
 */
@Composable
fun GlassSurface(
    modifier: Modifier = Modifier,
    tone: GlassTone = GlassTone.PANEL,
    shape: Shape = VeyraRadius.mdShape,
    selected: Boolean = false,
    enabled: Boolean = true,
    border: Boolean = true,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    onClick: (() -> Unit)? = null,
    onClickLabel: String? = null,
    content: @Composable BoxScope.() -> Unit,
) {
    val glassEnabled = LocalGlass.current.enabled
    val accent = LocalAccent.current
    val reducedMotion = LocalReducedMotion.current
    val haptics = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    var focused by remember { mutableStateOf(false) }

    val pressScale by animateFloatAsState(
        targetValue = if (pressed && enabled && onClick != null) 0.985f else 1f,
        animationSpec = if (reducedMotion) snap() else spring(dampingRatio = 0.72f, stiffness = 640f),
        label = "GlassSurfacePress",
    )

    val surfaceModifier = if (glassEnabled) {
        Modifier.glassPane(shape, tone)
    } else {
        Modifier.background(glassFill(CanopyColors.Surface), shape)
    }

    val ringModifier = when {
        focused -> Modifier.border(VeyraSize.focusRing, accent, shape)
        selected -> Modifier.border(VeyraSize.hairline, accent.copy(alpha = 0.62f), shape)
        glassEnabled -> Modifier
        border -> Modifier.border(VeyraSize.hairline, CanopyColors.Rule, shape)
        else -> Modifier
    }

    val interactionModifier = if (onClick != null) {
        Modifier
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                onClickLabel = onClickLabel,
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
        modifier = modifier
            .graphicsLayer {
                scaleX = pressScale
                scaleY = pressScale
                alpha = if (enabled) 1f else 0.55f
            }
            .then(surfaceModifier)
            .then(ringModifier)
            .clip(shape)
            .then(interactionModifier),
    ) {
        Box(Modifier.padding(contentPadding)) {
            content()
        }
    }
}
