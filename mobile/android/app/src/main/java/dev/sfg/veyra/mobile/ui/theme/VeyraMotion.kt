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

import android.content.ContentResolver
import android.database.ContentObserver
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalContext

/**
 * El usuario pidió reducir el movimiento en el sistema (escala de animación = 0).
 *
 * Se expone como CompositionLocal para que cualquier primitiva consulte una sola
 * fuente y no registre observadores propios.
 */
val LocalReducedMotion = staticCompositionLocalOf { false }

/**
 * Lectura composable del estado de movimiento reducido.
 *
 * Es el punto de entrada que consumen shell, reproductor y componentes. Lee el
 * CompositionLocal provisto por [VeyraTheme], así un preview o una prueba puede
 * forzarlo sin tocar ajustes del sistema.
 */
@Composable
fun rememberReduceMotion(): Boolean = LocalReducedMotion.current

/**
 * Observa `ANIMATOR_DURATION_SCALE`. Se registra una vez por tema, no por
 * componente, y se libera al salir de composición.
 */
@Composable
fun rememberReducedMotion(): Boolean {
    val resolver = LocalContext.current.contentResolver
    var reduced by remember(resolver) { mutableStateOf(readReducedMotion(resolver)) }

    DisposableEffect(resolver) {
        val observer = object : ContentObserver(Handler(Looper.getMainLooper())) {
            override fun onChange(selfChange: Boolean) {
                reduced = readReducedMotion(resolver)
            }
        }
        runCatching {
            resolver.registerContentObserver(
                Settings.Global.getUriFor(Settings.Global.ANIMATOR_DURATION_SCALE),
                false,
                observer,
            )
        }
        onDispose {
            runCatching { resolver.unregisterContentObserver(observer) }
        }
    }

    return reduced
}

/** Lectura defensiva: algunos OEM bloquean el ajuste y devuelven excepciones. */
internal fun readReducedMotion(resolver: ContentResolver): Boolean {
    val scale = runCatching {
        Settings.Global.getFloat(resolver, Settings.Global.ANIMATOR_DURATION_SCALE, 1f)
    }.getOrDefault(1f)
    return !VeyraMotion.shouldAnimate(scale)
}
