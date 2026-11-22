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

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.sfg.veyra.mobile.ui.theme.CanopyColors
import dev.sfg.veyra.mobile.ui.theme.LocalReducedMotion
import dev.sfg.veyra.mobile.ui.theme.VeyraCyber

enum class StatusPillTone {
    ACTIVE,
    STREAMING,
    SYNC,
    PRIMARY,
    ACCENT,
    WARNING,
    COUNT,
    INFO,
    MUTED,
}

enum class StatusPillSize {
    SM,
    MD,
}

/**
 * Píldora de estado oficial de Veyra Music (Enterprise Slate & Obsidian Industrial).
 * Replica StatusPillBadge.vue de la versión desktop con micro-punto y borde hairline.
 */
@Composable
fun StatusPillBadge(
    label: String,
    modifier: Modifier = Modifier,
    status: StatusPillTone = StatusPillTone.INFO,
    dot: Boolean = true,
    pulse: Boolean = false,
    size: StatusPillSize = StatusPillSize.MD,
) {
    val isCyberpunk = LocalCyberpunkTheme.current
    val pillShape = if (isCyberpunk) CyberCutCompactShape else RoundedCornerShape(999.dp)
    val bg = when (status) {
        StatusPillTone.ACTIVE, StatusPillTone.STREAMING -> if (isCyberpunk) VeyraCyber.magenta.copy(alpha = 0.16f) else Color(0x1F10B981)
        StatusPillTone.SYNC, StatusPillTone.ACCENT -> Color(0x1F38BDF8)
        StatusPillTone.PRIMARY -> if (isCyberpunk) Color(0x28FACC15) else Color(0x2E8B5CF6)
        StatusPillTone.WARNING -> Color(0x1FF59E0B)
        StatusPillTone.COUNT -> Color(0x0DFFFFFF)
        StatusPillTone.INFO -> if (isCyberpunk) VeyraCyber.surface else Color.White.copy(alpha = 0.08f)
        StatusPillTone.MUTED -> Color(0x10FFFFFF)
    }

    val border = when (status) {
        StatusPillTone.ACTIVE, StatusPillTone.STREAMING -> if (isCyberpunk) VeyraCyber.magenta.copy(alpha = 0.72f) else Color(0x4210B981)
        StatusPillTone.SYNC, StatusPillTone.ACCENT -> if (isCyberpunk) VeyraCyber.cyan else Color(0x4738BDF8)
        StatusPillTone.PRIMARY -> if (isCyberpunk) VeyraCyber.acidYellow else Color(0x668B5CF6)
        StatusPillTone.WARNING -> Color(0x47F59E0B)
        StatusPillTone.COUNT -> Color(0x14FFFFFF)
        StatusPillTone.INFO -> if (isCyberpunk) VeyraCyber.cyan.copy(alpha = 0.75f) else Color.White.copy(alpha = 0.20f)
        StatusPillTone.MUTED -> CanopyColors.Rule
    }

    val textColor = when (status) {
        StatusPillTone.ACTIVE, StatusPillTone.STREAMING -> if (isCyberpunk) VeyraCyber.magenta else Color(0xFF34D399)
        StatusPillTone.SYNC, StatusPillTone.ACCENT -> if (isCyberpunk) VeyraCyber.cyan else Color(0xFF38BDF8)
        StatusPillTone.PRIMARY -> if (isCyberpunk) VeyraCyber.acidYellow else Color(0xFFC4B5FD)
        StatusPillTone.WARNING -> Color(0xFFFBBF24)
        StatusPillTone.COUNT -> CanopyColors.Muted
        StatusPillTone.INFO -> CanopyColors.Text
        StatusPillTone.MUTED -> CanopyColors.Muted
    }

    val dotColor = when (status) {
        StatusPillTone.ACTIVE, StatusPillTone.STREAMING -> if (isCyberpunk) VeyraCyber.magenta else Color(0xFF10B981)
        StatusPillTone.SYNC, StatusPillTone.ACCENT -> if (isCyberpunk) VeyraCyber.cyan else Color(0xFF38BDF8)
        StatusPillTone.PRIMARY -> if (isCyberpunk) VeyraCyber.acidYellow else Color(0xFF8B5CF6)
        StatusPillTone.WARNING -> Color(0xFFF59E0B)
        StatusPillTone.COUNT -> CanopyColors.Muted
        StatusPillTone.INFO -> if (isCyberpunk) VeyraCyber.cyan else CanopyColors.SecondaryAccent
        StatusPillTone.MUTED -> CanopyColors.Muted
    }

    val reducedMotion = LocalReducedMotion.current
    val dotAnim = if (pulse && !reducedMotion) {
        val infinite = rememberInfiniteTransition(label = "StatusDotPulse")
        val scale by infinite.animateFloat(
            initialValue = 1f,
            targetValue = 1.35f,
            animationSpec = infiniteRepeatable(
                animation = tween(1000),
                repeatMode = RepeatMode.Reverse,
            ),
            label = "StatusDotScale",
        )
        scale
    } else 1f

    val padH = if (size == StatusPillSize.SM) 7.dp else 9.dp
    val padV = if (size == StatusPillSize.SM) 2.dp else 3.5.dp
    val fontSize = if (size == StatusPillSize.SM) 10.sp else 11.5.sp
    val dotSize = if (size == StatusPillSize.SM) 5.dp else 6.dp

    Row(
        modifier = modifier
            .semantics(mergeDescendants = true) {}
            .background(bg, pillShape)
            .border(1.dp, border, pillShape)
            .padding(horizontal = padH, vertical = padV),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (dot) {
            Box(
                modifier = Modifier
                    .size(dotSize)
                    .scale(dotAnim)
                    .background(dotColor, if (isCyberpunk) CyberCutCompactShape else CircleShape),
            )
            Spacer(Modifier.width(if (size == StatusPillSize.SM) 4.5.dp else 6.dp))
        }
        val labelStyle = TextStyle(
            fontFamily = if (isCyberpunk) FontFamily.Monospace else FontFamily.Default,
            fontSize = fontSize,
            fontWeight = FontWeight.SemiBold,
            lineHeight = fontSize,
            letterSpacing = if (isCyberpunk) 0.5.sp else 0.2.sp,
        )
        if (isCyberpunk) {
            CyberScrambleText(
                text = label,
                style = labelStyle,
                color = textColor,
            )
        } else {
            Text(
                text = label,
                color = textColor,
                style = labelStyle,
            )
        }
    }
}
