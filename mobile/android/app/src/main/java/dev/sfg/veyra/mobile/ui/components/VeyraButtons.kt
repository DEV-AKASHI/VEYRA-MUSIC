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
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.sfg.veyra.mobile.ui.theme.CanopyColors
import dev.sfg.veyra.mobile.ui.theme.LocalAccent
import dev.sfg.veyra.mobile.ui.theme.LocalReducedMotion
import dev.sfg.veyra.mobile.ui.theme.VeyraAlpha
import dev.sfg.veyra.mobile.ui.theme.VeyraCyber
import dev.sfg.veyra.mobile.ui.theme.VeyraRadius
import dev.sfg.veyra.mobile.ui.theme.VeyraSemantic
import dev.sfg.veyra.mobile.ui.theme.VeyraSize

enum class VeyraButtonVariant { PRIMARY, SECONDARY, DANGER, GHOST }

enum class VeyraButtonSize { SMALL, MEDIUM }

/**
 * Botón principal del sistema. Conserva el contrato táctil (48dp), expone
 * estados press/focus/disabled/loading y respeta reduce motion.
 *
 * @param loadingDescription texto localizado para TalkBack cuando [loading].
 */
@Composable
fun VeyraButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: VeyraButtonVariant = VeyraButtonVariant.PRIMARY,
    size: VeyraButtonSize = VeyraButtonSize.MEDIUM,
    enabled: Boolean = true,
    loading: Boolean = false,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
    loadingDescription: String? = null,
) {
    val isCyberpunk = LocalCyberpunkTheme.current
    val accent = LocalAccent.current
    val btnShape = if (isCyberpunk) VeyraCyber.shape else VeyraRadius.buttonShape

    val container = when (variant) {
        VeyraButtonVariant.PRIMARY -> if (isCyberpunk) VeyraCyber.acidYellow else accent
        VeyraButtonVariant.SECONDARY -> if (isCyberpunk) VeyraCyber.surface else Color.White.copy(alpha = 0.10f)
        VeyraButtonVariant.DANGER -> CanopyColors.Danger
        VeyraButtonVariant.GHOST -> Color.Transparent
    }
    val contentColor = when (variant) {
        VeyraButtonVariant.PRIMARY -> if (isCyberpunk) VeyraCyber.ink else Color.White
        VeyraButtonVariant.SECONDARY -> CanopyColors.Text
        VeyraButtonVariant.DANGER -> Color(0xFF070A12)
        VeyraButtonVariant.GHOST -> CanopyColors.Text
    }
    val borderColor = when (variant) {
        VeyraButtonVariant.PRIMARY -> if (isCyberpunk) VeyraCyber.ink else Color.White.copy(alpha = 0.36f)
        VeyraButtonVariant.SECONDARY -> if (isCyberpunk) VeyraCyber.cyan else Color.White.copy(alpha = 0.24f)
        VeyraButtonVariant.DANGER -> if (isCyberpunk) VeyraCyber.ink else Color.White.copy(alpha = 0.24f)
        VeyraButtonVariant.GHOST -> Color.Transparent
    }
    val hardShadowColor = when {
        !isCyberpunk || !enabled -> null
        variant == VeyraButtonVariant.PRIMARY -> VeyraCyber.cyan
        variant == VeyraButtonVariant.SECONDARY -> VeyraCyber.cyan.copy(alpha = 0.55f)
        variant == VeyraButtonVariant.DANGER -> CanopyColors.Danger.copy(alpha = 0.55f)
        else -> null
    }
    val primaryBrush = remember(accent, isCyberpunk) {
        if (isCyberpunk) {
            Brush.horizontalGradient(listOf(VeyraCyber.acidYellow, VeyraCyber.acidYellow))
        } else {
            Brush.horizontalGradient(listOf(Color(0xFF7C3AED), Color(0xFF2563EB)))
        }
    }
    val liquidSecondaryBrush = remember {
        Brush.verticalGradient(
            listOf(
                Color.White.copy(alpha = 0.14f),
                Color.White.copy(alpha = 0.06f),
            ),
        )
    }

    val surfaceModifier = when {
        variant == VeyraButtonVariant.PRIMARY -> Modifier.background(primaryBrush, btnShape)
        variant == VeyraButtonVariant.DANGER -> Modifier.background(container, btnShape)
        variant == VeyraButtonVariant.GHOST -> Modifier
        !isCyberpunk -> Modifier.background(liquidSecondaryBrush, btnShape)
        else -> Modifier.background(container, btnShape)
    }

    VeyraButtonShell(
        onClick = onClick,
        modifier = modifier,
        surfaceModifier = surfaceModifier,
        contentColor = contentColor,
        borderColor = borderColor,
        borderWidth = if (isCyberpunk && variant != VeyraButtonVariant.GHOST) 1.5.dp else VeyraSize.hairline,
        hardShadowColor = hardShadowColor,
        enabled = enabled,
        loading = loading,
        loadingDescription = loadingDescription,
        shape = btnShape,
        minHeight = if (size == VeyraButtonSize.SMALL) VeyraSize.buttonSmall else VeyraSize.buttonMedium,
        contentPadding = if (size == VeyraButtonSize.SMALL) {
            PaddingValues(horizontal = 14.dp, vertical = 6.dp)
        } else {
            PaddingValues(horizontal = 20.dp, vertical = 10.dp)
        },
        textStyle = if (size == VeyraButtonSize.SMALL) {
            MaterialTheme.typography.labelMedium
        } else {
            MaterialTheme.typography.labelLarge
        },
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        label = label,
    )
}

/**
 * Botón circular de acciones del reproductor y del chrome.
 *
 * @param toggleable cuando es un estado (favorito, shuffle, repeat) usa
 *   semántica de checkbox y anuncia [selected].
 */
@Composable
fun VeyraIconButton(
    icon: ImageVector,
    contentDescription: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    selected: Boolean = false,
    loading: Boolean = false,
    toggleable: Boolean = false,
    tint: Color? = null,
    size: Dp = VeyraSize.iconButton,
    loadingDescription: String? = null,
) {
    val isCyberpunk = LocalCyberpunkTheme.current
    val accent = LocalAccent.current
    val reducedMotion = LocalReducedMotion.current
    val haptics = LocalHapticFeedback.current
    val btnShape = if (isCyberpunk) VeyraCyber.shape else CircleShape

    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    var focused by remember { mutableStateOf(false) }

    val pressScale by animateFloatAsState(
        targetValue = if (pressed && enabled) 0.92f else 1f,
        animationSpec = if (reducedMotion) snap() else spring(dampingRatio = 0.7f, stiffness = 700f),
        label = "IconButtonPress",
    )

    val resolvedTint = tint ?: when {
        isCyberpunk && selected -> VeyraCyber.ink
        selected -> accent
        else -> CanopyColors.MutedStrong
    }

    val surfaceModifier = when {
        isCyberpunk && selected -> Modifier.background(VeyraCyber.cyan, btnShape)
        isCyberpunk -> Modifier.background(VeyraCyber.surface.copy(alpha = 0.85f), btnShape)
        selected -> Modifier.background(accent.copy(alpha = 0.20f), btnShape)
        else -> Modifier.background(Color.White.copy(alpha = 0.08f), btnShape)
    }
    val ringModifier = when {
        focused -> Modifier.border(VeyraSize.focusRing, VeyraSemantic.focus, btnShape)
        isCyberpunk && selected -> Modifier.border(1.5.dp, VeyraCyber.acidYellow, btnShape)
        isCyberpunk -> Modifier.border(1.dp, VeyraCyber.cyan.copy(alpha = 0.55f), btnShape)
        selected -> Modifier.border(VeyraSize.hairline, accent.copy(alpha = 0.65f), btnShape)
        else -> Modifier.border(VeyraSize.hairline, Color.White.copy(alpha = 0.16f), btnShape)
    }

    val interactionModifier = if (toggleable) {
        Modifier.toggleable(
            value = selected,
            interactionSource = interactionSource,
            indication = null,
            enabled = enabled && !loading,
            role = Role.Checkbox,
            onValueChange = {
                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onClick()
            },
        )
    } else {
        Modifier.clickable(
            interactionSource = interactionSource,
            indication = null,
            enabled = enabled && !loading,
            role = Role.Button,
        ) {
            haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            onClick()
        }
    }

    Box(
        modifier = modifier
            .minimumInteractiveComponentSize()
            .size(size)
            .graphicsLayer {
                scaleX = pressScale
                scaleY = pressScale
                alpha = if (enabled) 1f else VeyraAlpha.disabledSurface
            }
            .then(surfaceModifier)
            .then(ringModifier)
            .clip(btnShape)
            .then(interactionModifier)
            .onFocusChanged { focused = it.isFocused }
            .semantics {
                // Al cargar desaparece el Icon: el nodo debe seguir siendo
                // identificable y anunciar su estado.
                if (loading) {
                    if (!contentDescription.isNullOrBlank()) this.contentDescription = contentDescription
                    if (!loadingDescription.isNullOrBlank()) stateDescription = loadingDescription
                }
            },
        contentAlignment = Alignment.Center,
    ) {
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(VeyraSize.iconSm),
                color = resolvedTint,
                strokeWidth = 2.dp,
            )
        } else {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = resolvedTint,
                modifier = Modifier.size(VeyraSize.icon),
            )
        }
    }
}

/**
 * Chip de filtro. Cápsula con estado selected y objetivo táctil de 48dp aunque
 * su altura visual sea 32dp.
 */
@Composable
fun VeyraChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: ImageVector? = null,
) {
    val isCyberpunk = LocalCyberpunkTheme.current
    val accent = LocalAccent.current
    val haptics = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }
    val chipShape = if (isCyberpunk) VeyraCyber.shape else VeyraRadius.pillShape

    val container = when {
        isCyberpunk && selected -> VeyraCyber.acidYellow
        isCyberpunk -> VeyraCyber.surface
        selected -> accent.copy(alpha = 0.22f)
        else -> Color.White.copy(alpha = 0.08f)
    }
    val border = when {
        isCyberpunk && selected -> VeyraCyber.cyan
        isCyberpunk -> VeyraCyber.cyan.copy(alpha = 0.55f)
        selected -> accent.copy(alpha = 0.68f)
        else -> Color.White.copy(alpha = 0.18f)
    }
    val contentColor = when {
        !enabled -> CanopyColors.Muted
        isCyberpunk && selected -> VeyraCyber.ink
        else -> CanopyColors.Text
    }
    val borderWidth = if (isCyberpunk) 1.5.dp else VeyraSize.hairline

    val surfaceModifier = Modifier.background(container, chipShape)

    Row(
        modifier = modifier
            .minimumInteractiveComponentSize()
            .heightIn(min = VeyraSize.chip)
            .then(surfaceModifier)
            .border(borderWidth, border, chipShape)
            .clip(chipShape)
            .toggleable(
                value = selected,
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                role = Role.Checkbox,
                onValueChange = {
                    haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onClick()
                },
            )
            .semantics(mergeDescendants = true) {}
            .padding(horizontal = 14.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (leadingIcon != null) {
            Icon(
                imageVector = leadingIcon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(VeyraSize.iconSm),
            )
            Spacer(Modifier.width(6.dp))
        }
        Text(
            text = label,
            color = contentColor,
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun VeyraButtonShell(
    onClick: () -> Unit,
    modifier: Modifier,
    surfaceModifier: Modifier,
    contentColor: Color,
    borderColor: Color,
    borderWidth: Dp,
    hardShadowColor: Color?,
    enabled: Boolean,
    loading: Boolean,
    loadingDescription: String?,
    shape: RoundedCornerShape,
    minHeight: Dp,
    contentPadding: PaddingValues,
    textStyle: androidx.compose.ui.text.TextStyle,
    leadingIcon: ImageVector?,
    trailingIcon: ImageVector?,
    label: String,
) {
    val reducedMotion = LocalReducedMotion.current
    val haptics = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    var focused by remember { mutableStateOf(false) }

    val isDown = pressed && enabled && !loading
    val pressScale by animateFloatAsState(
        targetValue = if (isDown && hardShadowColor == null) 0.965f else 1f,
        animationSpec = if (reducedMotion) snap() else spring(dampingRatio = 0.72f, stiffness = 700f),
        label = "ButtonPress",
    )
    val mechanicalShift by animateDpAsState(
        targetValue = if (isDown && hardShadowColor != null) 3.dp else 0.dp,
        animationSpec = if (reducedMotion) snap() else spring(dampingRatio = 0.8f, stiffness = 900f),
        label = "CyberButtonShift",
    )

    val ringColor = if (focused) VeyraSemantic.focus else borderColor
    val ringWidth = if (focused) VeyraSize.focusRing else borderWidth

    Box(
        modifier = modifier
            .minimumInteractiveComponentSize()
            .heightIn(min = minHeight)
            .drawBehind {
                if (hardShadowColor != null) {
                    val px = 3.dp.toPx()
                    val r = 6.dp.toPx()
                    drawRoundRect(
                        color = hardShadowColor,
                        topLeft = Offset(px, px),
                        size = size,
                        cornerRadius = CornerRadius(r, r),
                    )
                }
            }
            .graphicsLayer {
                translationX = mechanicalShift.toPx()
                translationY = mechanicalShift.toPx()
                scaleX = pressScale
                scaleY = pressScale
                alpha = if (enabled) 1f else VeyraAlpha.disabledSurface
            }
            .then(surfaceModifier)
            .border(ringWidth, ringColor, shape)
            .clip(shape)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled && !loading,
                role = Role.Button,
            ) {
                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onClick()
            }
            .onFocusChanged { focused = it.isFocused }
            .semantics {
                if (loading && !loadingDescription.isNullOrBlank()) {
                    stateDescription = loadingDescription
                }
            }
            .padding(contentPadding),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            if (loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(VeyraSize.iconSm),
                    color = contentColor,
                    strokeWidth = 2.dp,
                )
                Spacer(Modifier.width(8.dp))
            } else if (leadingIcon != null) {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.size(VeyraSize.iconSm),
                )
                Spacer(Modifier.width(8.dp))
            }
            Text(
                text = label,
                color = contentColor,
                style = textStyle.copy(fontWeight = FontWeight.SemiBold),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (trailingIcon != null && !loading) {
                Spacer(Modifier.width(8.dp))
                Icon(
                    imageVector = trailingIcon,
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.size(VeyraSize.iconSm),
                )
            }
        }
    }
}
