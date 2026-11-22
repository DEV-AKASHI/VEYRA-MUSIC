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

package dev.sfg.veyra.mobile.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Tokens base del sistema móvil. Los demás agentes consumen estos valores y no
 * redefinen escalas locales: así Aurora, Cyberpunk y los estados comparten ritmo.
 */
object VeyraSpace {
    val xxs = 2.dp
    val xs = 4.dp
    val sm = 8.dp
    val md = 12.dp
    val lg = 16.dp
    val xl = 24.dp
    val xxl = 32.dp

    /** Margen horizontal de pantalla. */
    val screenGutter = 16.dp
}

/** Radios squircle. [pill] solo para cápsulas de estado y filtros. */
object VeyraRadius {
    val xs = 6.dp
    val sm = 10.dp
    val md = 14.dp
    val lg = 20.dp
    val xl = 24.dp
    val dock = 26.dp
    val pill = 999.dp

    val button = 12.dp

    val xsShape = RoundedCornerShape(xs)
    val smShape = RoundedCornerShape(sm)
    val mdShape = RoundedCornerShape(md)
    val lgShape = RoundedCornerShape(lg)
    val xlShape = RoundedCornerShape(xl)
    val dockShape = RoundedCornerShape(dock)
    val pillShape = RoundedCornerShape(pill)
    val buttonShape = RoundedCornerShape(button)
}

/**
 * Objetivos táctiles. 48dp es el mínimo accesible; los controles compactos
 * conservan su altura visual y delegan el área táctil a
 * `Modifier.minimumInteractiveComponentSize()`.
 */
object VeyraSize {
    val touchTarget = 48.dp
    val iconButton = 44.dp
    val buttonMedium = 48.dp
    val buttonSmall = 36.dp
    val chip = 32.dp
    val iconSm = 18.dp
    val icon = 22.dp
    val iconLg = 26.dp
    val hairline = 1.dp
    val focusRing = 2.dp
}

/** Opacidades semánticas para estados y superficies. */
object VeyraAlpha {
    const val disabledContent = 0.42f
    const val disabledSurface = 0.55f
    const val selectedWash = 0.14f
    const val mutedIcon = 0.72f
    const val scrim = 0.72f
}

/**
 * Rol semántico de color. Los componentes leen roles, no hex. El acento real
 * vive en [LocalAccent] porque puede seguir al fondo de pantalla o a la portada.
 */
object VeyraSemantic {
    val canvas = CanopyColors.Chrome
    val canvasAlt = CanopyColors.Canvas

    val surface = CanopyColors.Surface
    val surfaceRaised = CanopyColors.SurfaceHover
    val glass = CanopyColors.Glass
    val glassChrome = CanopyColors.GlassChrome

    val border = CanopyColors.Rule
    val borderStrong = CanopyColors.RuleStrong

    val textPrimary = CanopyColors.Text
    val textSecondary = CanopyColors.Muted
    val textTertiary = CanopyColors.Eyebrow

    val action = CanopyColors.Accent
    val actionDeep = CanopyColors.AccentDeep
    val onAction = Color.White
    val focus = CanopyColors.SecondaryAccent

    val success = Color(0xFF34D399)
    val warning = CanopyColors.Warning
    val error = CanopyColors.Danger

    val scrim = CanopyColors.Chrome.copy(alpha = VeyraAlpha.scrim)
}

/**
 * Tokens de la interfaz secundaria Neo-Brutalism + Cyberpunk.
 *
 * Uso restringido: únicamente superficies o estados aprobados en la matriz de
 * Fase 0/1. No sustituye el fondo principal ni la tipografía base.
 */
object VeyraCyber {
    val ink = Color(0xFF04060B)
    val canvas = Color(0xFF070A12)
    val surface = Color(0xFF101826)
    val surfaceAlt = Color(0xFF172236)

    val cyan = Color(0xFF22D3EE)
    val acidYellow = Color(0xFFFACC15)
    val magenta = Color(0xFFF472B6)
    val lime = Color(0xFFA3E635)
    val amber = Color(0xFFFBBF24)

    val text = CanopyColors.Text
    val textMuted = CanopyColors.Muted

    val borderWidth = 2.dp
    val hardShadow = 5.dp

    /** Esquina casi recta: el contraste duro es la firma del lenguaje. */
    val shape = RoundedCornerShape(6.dp)
    val pillShape = RoundedCornerShape(999.dp)
}

/** Duraciones y curvas compartidas. */
object VeyraMotion {
    const val instantMillis = 0
    const val fastMillis = 120
    const val standardMillis = 180
    const val expressiveMillis = 280
    const val auroraDriftMillis = 26_000
    const val pulseMillis = 1_000

    /**
     * Reduce motion colapsa la duración a cero en lugar de acortarla: un cambio
     * de estado sigue siendo perceptible, pero no hay desplazamiento animado.
     */
    fun durationMillis(baseMillis: Int, reducedMotion: Boolean): Int =
        if (reducedMotion) instantMillis else baseMillis.coerceAtLeast(0)

    /** El sistema desactiva animaciones cuando la escala global llega a cero. */
    fun shouldAnimate(animatorDurationScale: Float): Boolean =
        animatorDurationScale.isFinite() && animatorDurationScale > 0f
}
