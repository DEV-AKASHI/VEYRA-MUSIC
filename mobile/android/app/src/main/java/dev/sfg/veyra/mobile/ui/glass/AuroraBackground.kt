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

import android.app.ActivityManager
import android.content.Context
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.platform.LocalContext
import dev.sfg.veyra.mobile.ui.theme.CanopyColors
import dev.sfg.veyra.mobile.ui.theme.LocalAccent
import dev.sfg.veyra.mobile.ui.theme.LocalReducedMotion
import dev.sfg.veyra.mobile.ui.theme.VeyraMotion
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Fondo Aurora de la interfaz principal.
 *
 * Tres orbes radiales derivados del acento (portada o color del sistema) se
 * desplazan sobre un velo Obsidian. El dibujo reutiliza brushes creados una sola
 * vez: en cada frame solo cambian traslaciones y escalas, sin allocations ni
 * desenfoques por frame.
 *
 * Fallback: sin movimiento (`reducedMotion`), en dispositivos Low RAM o con
 * [active] en false, los orbes quedan estáticos. No depende de RenderEffect, así
 * que funciona igual desde API 24.
 */
@Composable
fun AuroraBackground(
    modifier: Modifier = Modifier,
    accent: Color = LocalAccent.current,
    active: Boolean = true,
    intensity: Float = 1f,
    content: @Composable BoxScope.() -> Unit = {},
) {
    val reducedMotion = LocalReducedMotion.current
    val lowRamDevice = rememberLowRamDevice()
    val animated = active && !reducedMotion && !lowRamDevice

    val phase: State<Float> = if (animated) {
        val transition = rememberInfiniteTransition(label = "AuroraDrift")
        transition.animateFloat(
            initialValue = 0f,
            targetValue = (2f * PI).toFloat(),
            animationSpec = infiniteRepeatable(
                animation = tween(VeyraMotion.auroraDriftMillis, easing = LinearEasing),
                repeatMode = RepeatMode.Restart,
            ),
            label = "AuroraPhase",
        )
    } else {
        remember { mutableFloatStateOf(0f) }
    }

    // Brushes de radio unitario: el degradado se escala por transformación, nunca
    // se reconstruye por frame. Solo cambian si cambia el acento o la intensidad.
    val orbBrushes = remember(accent, intensity) {
        val colors = listOf(
            lerp(accent, CanopyColors.SecondaryAccent, 0.28f),
            lerp(accent, CanopyColors.Favorite, 0.18f),
            lerp(accent, CanopyColors.SecondaryAccent, 0.58f),
        )
        colors.map { orb ->
            Brush.radialGradient(
                colors = listOf(
                    orb.copy(alpha = (0.34f * intensity).coerceIn(0f, 1f)),
                    orb.copy(alpha = (0.13f * intensity).coerceIn(0f, 1f)),
                    Color.Transparent,
                ),
                center = Offset.Zero,
                radius = 1f,
            )
        }
    }
    val baseWash = remember {
        Brush.verticalGradient(
            0f to CanopyColors.Chrome.copy(alpha = 0.20f),
            0.55f to CanopyColors.Chrome.copy(alpha = 0.55f),
            1f to CanopyColors.Chrome,
        )
    }
    val bottomVeil = remember {
        Brush.verticalGradient(
            0f to Color.Transparent,
            0.62f to CanopyColors.Chrome.copy(alpha = 0.46f),
            1f to CanopyColors.Chrome,
        )
    }

    Box(modifier.fillMaxSize()) {
        Canvas(Modifier.fillMaxSize()) {
            drawRect(baseWash)

            val drift = phase.value
            val minSide = size.minDimension
            // Transformación directa de canvas: ni lambdas ni objetos por orbe/frame.
            // El degradado unitario se centra en (cx, cy) y se escala a `radius` px.
            val canvas = drawContext.canvas
            var index = 0
            while (index < orbBrushes.size) {
                val seed = index * 2.1f
                val cx = size.width * (0.5f + 0.22f * sin(drift + seed))
                val cy = size.height * (0.24f + 0.16f * index) +
                    size.height * 0.08f * cos(drift * 0.8f + seed)
                val radius = minSide * (0.48f + 0.07f * index)

                canvas.save()
                canvas.translate(cx, cy)
                canvas.scale(radius, radius)
                drawCircle(brush = orbBrushes[index], radius = 1f, center = Offset.Zero)
                canvas.restore()
                index++
            }

            drawRect(bottomVeil)
        }
        content()
    }
}

/** Un solo chequeo por contexto; evita animar aurora en gama de entrada. */
@Composable
internal fun rememberLowRamDevice(): Boolean {
    val context = LocalContext.current
    return remember(context) {
        val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
        manager?.isLowRamDevice == true
    }
}
