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

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import dev.sfg.veyra.mobile.ui.theme.CanopyColors
import dev.sfg.veyra.mobile.ui.theme.LocalAccent
import dev.sfg.veyra.mobile.ui.theme.VeyraRadius
import dev.sfg.veyra.mobile.ui.theme.VeyraSemantic
import dev.sfg.veyra.mobile.ui.theme.VeyraSize
import dev.sfg.veyra.mobile.ui.theme.VeyraSpace

/**
 * Hoja inferior estándar del sistema.
 *
 * Usa superficie Slate sólida: la hoja vive en su propia ventana y no puede
 * muestrear con fiabilidad el backdrop de la ventana principal, así que no se
 * aplica glass aquí. El acento solo aparece en el asa.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VeyraModalSheet(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    accent: Color = LocalAccent.current,
    content: @Composable ColumnScope.() -> Unit,
) {
    val isCyberpunk = LocalCyberpunkTheme.current
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        sheetState = sheetState,
        containerColor = if (isCyberpunk) dev.sfg.veyra.mobile.ui.theme.VeyraCyber.surface else Color(0xFF16132E),
        contentColor = CanopyColors.Text,
        scrimColor = VeyraSemantic.scrim,
        tonalElevation = 0.dp,
        dragHandle = { VeyraSheetHandle(accent = accent) },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = VeyraSpace.sm),
            content = content,
        )
    }
}

/** Asa de hoja con acento: 36x4dp, radio cápsula. */
@Composable
fun VeyraSheetHandle(accent: Color = LocalAccent.current) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp, bottom = 6.dp),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            Modifier
                .size(width = 36.dp, height = 4.dp)
                .background(accent.copy(alpha = 0.55f), RoundedCornerShape(VeyraRadius.pill)),
        )
    }
}

/**
 * Contenedor de diálogo con jerarquía squircle y borde hairline. Superficie
 * sólida por la misma razón que [VeyraModalSheet].
 */
@Composable
fun VeyraDialogShell(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    properties: DialogProperties = DialogProperties(),
    content: @Composable ColumnScope.() -> Unit,
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = properties,
    ) {
        val isCyberpunk = LocalCyberpunkTheme.current
        Surface(
            modifier = modifier
                .widthIn(max = 480.dp)
                .fillMaxWidth(),
            shape = if (isCyberpunk) dev.sfg.veyra.mobile.ui.theme.VeyraCyber.shape else VeyraRadius.xlShape,
            color = if (isCyberpunk) dev.sfg.veyra.mobile.ui.theme.VeyraCyber.surface else Color(0xFF16132E),
            contentColor = CanopyColors.Text,
            border = BorderStroke(
                if (isCyberpunk) dev.sfg.veyra.mobile.ui.theme.VeyraCyber.borderWidth else 1.dp,
                if (isCyberpunk) dev.sfg.veyra.mobile.ui.theme.VeyraCyber.cyan else Color.White.copy(alpha = 0.24f),
            ),
            tonalElevation = 0.dp,
        ) {
            Column(
                modifier = Modifier.padding(VeyraSpace.xl),
                content = content,
            )
        }
    }
}
