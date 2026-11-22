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

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp

/** Iconografía vectorial propia de Veyra Music con identidad dual Aurora / Cyberpunk. */
enum class VeyraIconKind {
    HOME,
    SEARCH,
    LIBRARY,
    CONNECT,
    SETTINGS,
    PLAY,
    PAUSE,
    SKIP_NEXT,
    HEART_OUTLINE,
    HEART_FILLED,
    MORE,
}

/**
 * Icono vectorial dual-style:
 * - Liquid Aurora: contorno orgánico duo-tone con relleno translúcido.
 * - Cyberpunk: trazos angulares HUD biselados a 45°.
 */
@Composable
fun VeyraGlyphIcon(
    kind: VeyraIconKind,
    tint: Color,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    filled: Boolean = false,
    contentDescription: String? = null,
) {
    val isCyberpunk = LocalCyberpunkTheme.current
    val path = remember { Path() }
    val fillAlpha = if (selected || filled) 0.32f else 0.14f

    val semModifier = if (!contentDescription.isNullOrBlank()) {
        modifier.semantics { this.contentDescription = contentDescription }
    } else {
        modifier
    }

    Canvas(modifier = semModifier) {
        val w = size.width
        val h = size.height
        val strokeWidth = (if (isCyberpunk) 1.85.dp else 1.7.dp).toPx()
        val stroke = Stroke(
            width = strokeWidth,
            cap = if (isCyberpunk) StrokeCap.Square else StrokeCap.Round,
            join = if (isCyberpunk) StrokeJoin.Miter else StrokeJoin.Round,
        )

        if (isCyberpunk) {
            drawCyberpunkGlyph(kind, w, h, tint, stroke, strokeWidth, path, selected || filled)
        } else {
            drawLiquidAuroraGlyph(kind, w, h, tint, fillAlpha, stroke, strokeWidth, path, selected || filled)
        }
    }
}

private fun DrawScope.drawLiquidAuroraGlyph(
    kind: VeyraIconKind,
    w: Float,
    h: Float,
    tint: Color,
    fillAlpha: Float,
    stroke: Stroke,
    strokeWidth: Float,
    path: Path,
    emphasized: Boolean,
) {
    val wash = tint.copy(alpha = fillAlpha)
    when (kind) {
        VeyraIconKind.HOME -> {
            path.reset()
            path.moveTo(w * 0.50f, h * 0.14f)
            path.lineTo(w * 0.86f, h * 0.44f)
            path.lineTo(w * 0.86f, h * 0.82f)
            path.lineTo(w * 0.62f, h * 0.82f)
            path.lineTo(w * 0.62f, h * 0.58f)
            path.lineTo(w * 0.38f, h * 0.58f)
            path.lineTo(w * 0.38f, h * 0.82f)
            path.lineTo(w * 0.14f, h * 0.82f)
            path.lineTo(w * 0.14f, h * 0.44f)
            path.close()
            drawPath(path, wash)
            drawPath(path, tint, style = stroke)
        }
        VeyraIconKind.SEARCH -> {
            val r = w * 0.28f
            val center = Offset(w * 0.43f, h * 0.43f)
            drawCircle(wash, radius = r, center = center)
            drawCircle(tint, radius = r, center = center, style = stroke)
            if (emphasized) {
                drawCircle(tint.copy(alpha = 0.55f), radius = r * 0.35f, center = Offset(w * 0.36f, h * 0.36f))
            }
            drawLine(
                color = tint,
                start = Offset(w * 0.64f, h * 0.64f),
                end = Offset(w * 0.85f, h * 0.85f),
                strokeWidth = strokeWidth * 1.15f,
                cap = StrokeCap.Round,
            )
        }
        VeyraIconKind.LIBRARY -> {
            val cardW = w * 0.54f
            val cardH = h * 0.62f
            drawRoundRect(
                color = wash,
                topLeft = Offset(w * 0.16f, h * 0.24f),
                size = Size(cardW, cardH),
                cornerRadius = CornerRadius(w * 0.12f),
            )
            drawRoundRect(
                color = tint,
                topLeft = Offset(w * 0.16f, h * 0.24f),
                size = Size(cardW, cardH),
                cornerRadius = CornerRadius(w * 0.12f),
                style = stroke,
            )
            path.reset()
            path.moveTo(w * 0.32f, h * 0.15f)
            path.lineTo(w * 0.78f, h * 0.15f)
            path.lineTo(w * 0.84f, h * 0.21f)
            path.lineTo(w * 0.84f, h * 0.70f)
            drawPath(path, tint.copy(alpha = 0.82f), style = stroke)
            // Nota musical minimalista dentro de la biblioteca
            drawCircle(tint, radius = w * 0.07f, center = Offset(w * 0.39f, h * 0.62f))
            drawLine(tint, Offset(w * 0.46f, h * 0.62f), Offset(w * 0.46f, h * 0.41f), strokeWidth = strokeWidth * 0.9f, cap = StrokeCap.Round)
            drawLine(tint, Offset(w * 0.46f, h * 0.41f), Offset(w * 0.55f, h * 0.44f), strokeWidth = strokeWidth * 0.9f, cap = StrokeCap.Round)
        }
        VeyraIconKind.CONNECT -> {
            drawCircle(wash, radius = w * 0.13f, center = Offset(w * 0.50f, h * 0.50f))
            drawCircle(tint, radius = w * 0.13f, center = Offset(w * 0.50f, h * 0.50f), style = stroke)
            drawArc(
                color = tint,
                startAngle = -55f,
                sweepAngle = 110f,
                useCenter = false,
                topLeft = Offset(w * 0.22f, h * 0.22f),
                size = Size(w * 0.56f, h * 0.56f),
                style = stroke,
            )
            drawArc(
                color = tint,
                startAngle = 125f,
                sweepAngle = 110f,
                useCenter = false,
                topLeft = Offset(w * 0.22f, h * 0.22f),
                size = Size(w * 0.56f, h * 0.56f),
                style = stroke,
            )
            drawArc(
                color = tint.copy(alpha = 0.65f),
                startAngle = -50f,
                sweepAngle = 100f,
                useCenter = false,
                topLeft = Offset(w * 0.08f, h * 0.08f),
                size = Size(w * 0.84f, h * 0.84f),
                style = stroke,
            )
            drawArc(
                color = tint.copy(alpha = 0.65f),
                startAngle = 130f,
                sweepAngle = 100f,
                useCenter = false,
                topLeft = Offset(w * 0.08f, h * 0.08f),
                size = Size(w * 0.84f, h * 0.84f),
                style = stroke,
            )
        }
        VeyraIconKind.SETTINGS -> {
            // Perfil / nodo fluido dual
            drawCircle(wash, radius = w * 0.18f, center = Offset(w * 0.50f, h * 0.34f))
            drawCircle(tint, radius = w * 0.18f, center = Offset(w * 0.50f, h * 0.34f), style = stroke)
            path.reset()
            path.moveTo(w * 0.20f, h * 0.82f)
            path.cubicTo(w * 0.22f, h * 0.60f, w * 0.78f, h * 0.60f, w * 0.80f, h * 0.82f)
            path.close()
            drawPath(path, wash)
            drawPath(path, tint, style = stroke)
        }
        VeyraIconKind.PLAY -> {
            path.reset()
            path.moveTo(w * 0.30f, h * 0.20f)
            path.lineTo(w * 0.80f, h * 0.50f)
            path.lineTo(w * 0.30f, h * 0.80f)
            path.close()
            if (emphasized) {
                drawPath(path, tint)
            } else {
                drawPath(path, wash)
                drawPath(path, tint, style = stroke)
            }
        }
        VeyraIconKind.PAUSE -> {
            val barW = w * 0.18f
            val barH = h * 0.56f
            val leftX = w * 0.26f
            val rightX = w * 0.56f
            val topY = h * 0.22f
            drawRoundRect(tint, Offset(leftX, topY), Size(barW, barH), CornerRadius(barW * 0.45f))
            drawRoundRect(tint, Offset(rightX, topY), Size(barW, barH), CornerRadius(barW * 0.45f))
        }
        VeyraIconKind.SKIP_NEXT -> {
            path.reset()
            path.moveTo(w * 0.22f, h * 0.24f)
            path.lineTo(w * 0.60f, h * 0.50f)
            path.lineTo(w * 0.22f, h * 0.76f)
            path.close()
            drawPath(path, wash)
            drawPath(path, tint, style = stroke)
            drawLine(tint, Offset(w * 0.74f, h * 0.26f), Offset(w * 0.74f, h * 0.74f), strokeWidth = strokeWidth * 1.15f, cap = StrokeCap.Round)
        }
        VeyraIconKind.HEART_OUTLINE, VeyraIconKind.HEART_FILLED -> {
            path.reset()
            path.moveTo(w * 0.50f, h * 0.82f)
            path.cubicTo(w * 0.14f, h * 0.58f, w * 0.14f, h * 0.24f, w * 0.34f, h * 0.22f)
            path.cubicTo(w * 0.44f, h * 0.21f, w * 0.50f, h * 0.30f, w * 0.50f, h * 0.33f)
            path.cubicTo(w * 0.50f, h * 0.30f, w * 0.56f, h * 0.21f, w * 0.66f, h * 0.22f)
            path.cubicTo(w * 0.86f, h * 0.24f, w * 0.86f, h * 0.58f, w * 0.50f, h * 0.82f)
            path.close()
            if (kind == VeyraIconKind.HEART_FILLED) {
                drawPath(path, tint)
            } else {
                drawPath(path, wash)
                drawPath(path, tint, style = stroke)
            }
        }
        VeyraIconKind.MORE -> {
            val r = w * 0.085f
            drawCircle(tint, r, Offset(w * 0.24f, h * 0.50f))
            drawCircle(tint, r, Offset(w * 0.50f, h * 0.50f))
            drawCircle(tint, r, Offset(w * 0.76f, h * 0.50f))
        }
    }
}

private fun DrawScope.drawCyberpunkGlyph(
    kind: VeyraIconKind,
    w: Float,
    h: Float,
    tint: Color,
    stroke: Stroke,
    strokeWidth: Float,
    path: Path,
    emphasized: Boolean,
) {
    val fillTint = tint.copy(alpha = if (emphasized) 0.28f else 0.10f)
    when (kind) {
        VeyraIconKind.HOME -> {
            // Silueta HUD biselada con muesca en esquina
            path.reset()
            path.moveTo(w * 0.50f, h * 0.14f)
            path.lineTo(w * 0.86f, h * 0.40f)
            path.lineTo(w * 0.86f, h * 0.84f)
            path.lineTo(w * 0.60f, h * 0.84f)
            path.lineTo(w * 0.60f, h * 0.58f)
            path.lineTo(w * 0.40f, h * 0.58f)
            path.lineTo(w * 0.40f, h * 0.84f)
            path.lineTo(w * 0.14f, h * 0.84f)
            path.lineTo(w * 0.14f, h * 0.48f)
            path.close()
            drawPath(path, fillTint)
            drawPath(path, tint, style = stroke)
        }
        VeyraIconKind.SEARCH -> {
            // Lente octogonal + retícula central
            path.reset()
            path.moveTo(w * 0.30f, h * 0.16f)
            path.lineTo(w * 0.56f, h * 0.16f)
            path.lineTo(w * 0.70f, h * 0.30f)
            path.lineTo(w * 0.70f, h * 0.56f)
            path.lineTo(w * 0.56f, h * 0.70f)
            path.lineTo(w * 0.30f, h * 0.70f)
            path.lineTo(w * 0.16f, h * 0.56f)
            path.lineTo(w * 0.16f, h * 0.30f)
            path.close()
            drawPath(path, fillTint)
            drawPath(path, tint, style = stroke)
            drawLine(tint, Offset(w * 0.38f, h * 0.43f), Offset(w * 0.48f, h * 0.43f), strokeWidth = strokeWidth * 0.85f)
            drawLine(tint, Offset(w * 0.43f, h * 0.38f), Offset(w * 0.43f, h * 0.48f), strokeWidth = strokeWidth * 0.85f)
            drawLine(tint, Offset(w * 0.65f, h * 0.65f), Offset(w * 0.86f, h * 0.86f), strokeWidth = strokeWidth * 1.15f)
        }
        VeyraIconKind.LIBRARY -> {
            // Módulos escalonados con esquina cortada
            path.reset()
            path.moveTo(w * 0.16f, h * 0.24f)
            path.lineTo(w * 0.58f, h * 0.24f)
            path.lineTo(w * 0.70f, h * 0.36f)
            path.lineTo(w * 0.70f, h * 0.84f)
            path.lineTo(w * 0.16f, h * 0.84f)
            path.close()
            drawPath(path, fillTint)
            drawPath(path, tint, style = stroke)
            path.reset()
            path.moveTo(w * 0.34f, h * 0.14f)
            path.lineTo(w * 0.76f, h * 0.14f)
            path.lineTo(w * 0.86f, h * 0.24f)
            path.lineTo(w * 0.86f, h * 0.72f)
            drawPath(path, tint, style = stroke)
            drawLine(tint, Offset(w * 0.30f, h * 0.50f), Offset(w * 0.56f, h * 0.50f), strokeWidth = strokeWidth * 0.9f)
            drawLine(tint, Offset(w * 0.30f, h * 0.64f), Offset(w * 0.48f, h * 0.64f), strokeWidth = strokeWidth * 0.9f)
        }
        VeyraIconKind.CONNECT -> {
            // Nodo diamante + barras de señal angulares
            path.reset()
            path.moveTo(w * 0.50f, h * 0.36f)
            path.lineTo(w * 0.64f, h * 0.50f)
            path.lineTo(w * 0.50f, h * 0.64f)
            path.lineTo(w * 0.36f, h * 0.50f)
            path.close()
            drawPath(path, fillTint)
            drawPath(path, tint, style = stroke)
            path.reset()
            path.moveTo(w * 0.24f, h * 0.26f)
            path.lineTo(w * 0.14f, h * 0.36f)
            path.lineTo(w * 0.14f, h * 0.64f)
            path.lineTo(w * 0.24f, h * 0.74f)
            drawPath(path, tint, style = stroke)
            path.reset()
            path.moveTo(w * 0.76f, h * 0.26f)
            path.lineTo(w * 0.86f, h * 0.36f)
            path.lineTo(w * 0.86f, h * 0.64f)
            path.lineTo(w * 0.76f, h * 0.74f)
            drawPath(path, tint, style = stroke)
        }
        VeyraIconKind.SETTINGS -> {
            // Hexágono técnico con núcleo geométrico
            path.reset()
            path.moveTo(w * 0.34f, h * 0.16f)
            path.lineTo(w * 0.66f, h * 0.16f)
            path.lineTo(w * 0.86f, h * 0.50f)
            path.lineTo(w * 0.66f, h * 0.84f)
            path.lineTo(w * 0.34f, h * 0.84f)
            path.lineTo(w * 0.14f, h * 0.50f)
            path.close()
            drawPath(path, fillTint)
            drawPath(path, tint, style = stroke)
            drawRect(
                color = tint,
                topLeft = Offset(w * 0.40f, h * 0.40f),
                size = Size(w * 0.20f, h * 0.20f),
                style = stroke,
            )
        }
        VeyraIconKind.PLAY -> {
            path.reset()
            path.moveTo(w * 0.28f, h * 0.18f)
            path.lineTo(w * 0.82f, h * 0.50f)
            path.lineTo(w * 0.28f, h * 0.82f)
            path.close()
            if (emphasized) {
                drawPath(path, tint)
            } else {
                drawPath(path, fillTint)
                drawPath(path, tint, style = stroke)
            }
        }
        VeyraIconKind.PAUSE -> {
            drawRect(tint, Offset(w * 0.26f, h * 0.20f), Size(w * 0.17f, h * 0.60f))
            drawRect(tint, Offset(w * 0.57f, h * 0.20f), Size(w * 0.17f, h * 0.60f))
        }
        VeyraIconKind.SKIP_NEXT -> {
            path.reset()
            path.moveTo(w * 0.20f, h * 0.22f)
            path.lineTo(w * 0.60f, h * 0.50f)
            path.lineTo(w * 0.20f, h * 0.78f)
            path.close()
            drawPath(path, fillTint)
            drawPath(path, tint, style = stroke)
            drawRect(tint, Offset(w * 0.70f, h * 0.22f), Size(w * 0.10f, h * 0.56f))
        }
        VeyraIconKind.HEART_OUTLINE, VeyraIconKind.HEART_FILLED -> {
            // Corazón geométrico poligonal
            path.reset()
            path.moveTo(w * 0.50f, h * 0.84f)
            path.lineTo(w * 0.14f, h * 0.46f)
            path.lineTo(w * 0.24f, h * 0.22f)
            path.lineTo(w * 0.42f, h * 0.22f)
            path.lineTo(w * 0.50f, h * 0.32f)
            path.lineTo(w * 0.58f, h * 0.22f)
            path.lineTo(w * 0.76f, h * 0.22f)
            path.lineTo(w * 0.86f, h * 0.46f)
            path.close()
            if (kind == VeyraIconKind.HEART_FILLED) {
                drawPath(path, tint)
            } else {
                drawPath(path, fillTint)
                drawPath(path, tint, style = stroke)
            }
        }
        VeyraIconKind.MORE -> {
            val s = w * 0.14f
            drawRect(tint, Offset(w * 0.17f, h * 0.50f - s / 2), Size(s, s))
            drawRect(tint, Offset(w * 0.43f, h * 0.50f - s / 2), Size(s, s))
            drawRect(tint, Offset(w * 0.69f, h * 0.50f - s / 2), Size(s, s))
        }
    }
}
