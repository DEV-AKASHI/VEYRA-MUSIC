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

package dev.sfg.veyra.mobile.ui.preview

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CloudOff
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.sfg.veyra.mobile.ui.components.CyberPanel
import dev.sfg.veyra.mobile.ui.components.StatusPillBadge
import dev.sfg.veyra.mobile.ui.components.StatusPillSize
import dev.sfg.veyra.mobile.ui.components.StatusPillTone
import dev.sfg.veyra.mobile.ui.components.VeyraButton
import dev.sfg.veyra.mobile.ui.components.VeyraButtonSize
import dev.sfg.veyra.mobile.ui.components.VeyraButtonVariant
import dev.sfg.veyra.mobile.ui.components.VeyraCard
import dev.sfg.veyra.mobile.ui.components.VeyraChip
import dev.sfg.veyra.mobile.ui.components.VeyraDivider
import dev.sfg.veyra.mobile.ui.components.VeyraEmptyState
import dev.sfg.veyra.mobile.ui.components.VeyraIconButton
import dev.sfg.veyra.mobile.ui.components.VeyraSheetHandle
import dev.sfg.veyra.mobile.ui.glass.AuroraBackground
import dev.sfg.veyra.mobile.ui.theme.CanopyColors
import dev.sfg.veyra.mobile.ui.theme.VeyraCyber
import dev.sfg.veyra.mobile.ui.theme.VeyraRadius
import dev.sfg.veyra.mobile.ui.theme.VeyraSpace
import dev.sfg.veyra.mobile.ui.theme.VeyraTheme

private val PreviewBase = Modifier
    .fillMaxWidth()
    .padding(VeyraSpace.lg)

@Preview(
    name = "Aurora · superficies",
    showBackground = true,
    backgroundColor = 0xFF0B0F19,
    widthDp = 390,
    heightDp = 780,
)
@Composable
private fun AuroraSurfacesPreview() {
    VeyraTheme(reducedMotion = true) {
        AuroraBackground {
            Column(PreviewBase, verticalArrangement = Arrangement.spacedBy(VeyraSpace.md)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Veyra Music", style = MaterialTheme.typography.headlineMedium, color = CanopyColors.Text)
                    Spacer(Modifier.weight(1f))
                    StatusPillBadge("AURORA", status = StatusPillTone.PRIMARY)
                }
                StatusPillBadge(
                    label = "SINCRO ACTIVA",
                    status = StatusPillTone.SYNC,
                    pulse = true,
                    size = StatusPillSize.SM,
                )
                VeyraCard(contentPadding = androidx.compose.foundation.layout.PaddingValues(VeyraSpace.lg)) {
                    Column {
                        Text("GlassSurface PANEL", style = MaterialTheme.typography.titleMedium, color = CanopyColors.Text)
                        Text(
                            "Superficie compartida con press, foco y disabled.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = CanopyColors.Muted,
                        )
                    }
                }
                VeyraCard(
                    selected = true,
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(VeyraSpace.lg),
                ) {
                    Text("Card seleccionada", style = MaterialTheme.typography.titleMedium, color = CanopyColors.Text)
                }
                VeyraDivider(strong = true)
                Row(horizontalArrangement = Arrangement.spacedBy(VeyraSpace.sm)) {
                    VeyraChip(label = "Todo", selected = true, onClick = {})
                    VeyraChip(label = "Álbumes", selected = false, onClick = {})
                    VeyraChip(label = "Sin red", selected = false, onClick = {}, enabled = false)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(VeyraSpace.sm)) {
                    VeyraIconButton(Icons.Rounded.PlayArrow, "Reproducir", onClick = {})
                    VeyraIconButton(Icons.Rounded.FavoriteBorder, "Favorito", onClick = {}, selected = true, toggleable = true)
                    VeyraIconButton(Icons.Rounded.GraphicEq, "Cargando", onClick = {}, loading = true)
                }
            }
        }
    }
}

@Preview(
    name = "Botones · estados",
    showBackground = true,
    backgroundColor = 0xFF0B0F19,
    widthDp = 390,
)
@Composable
private fun VeyraButtonsPreview() {
    VeyraTheme(reducedMotion = true) {
        Column(PreviewBase, verticalArrangement = Arrangement.spacedBy(VeyraSpace.sm)) {
            VeyraButton("Reproducir ahora", onClick = {}, leadingIcon = Icons.Rounded.PlayArrow)
            VeyraButton("Guardar", onClick = {}, variant = VeyraButtonVariant.SECONDARY)
            VeyraButton("Cargando", onClick = {}, loading = true, loadingDescription = "Cargando")
            VeyraButton("Deshabilitado", onClick = {}, enabled = false)
            VeyraButton("Eliminar", onClick = {}, variant = VeyraButtonVariant.DANGER)
            VeyraButton("Ver cola", onClick = {}, variant = VeyraButtonVariant.GHOST, size = VeyraButtonSize.SMALL)
        }
    }
}

@Preview(
    name = "Cyberpunk · panel",
    showBackground = true,
    backgroundColor = 0xFF070A12,
    widthDp = 390,
)
@Composable
private fun CyberPanelPreview() {
    VeyraTheme(reducedMotion = true) {
        Column(PreviewBase, verticalArrangement = Arrangement.spacedBy(VeyraSpace.xl)) {
            CyberPanel {
                Text("MODO CYBER", style = MaterialTheme.typography.titleMedium, color = VeyraCyber.text)
                Text("Borde duro y sombra sólida.", style = MaterialTheme.typography.bodyMedium, color = VeyraCyber.textMuted)
            }
            CyberPanel(accent = VeyraCyber.magenta, selected = true) {
                Text("LIVE SESSION", style = MaterialTheme.typography.titleMedium, color = VeyraCyber.text)
            }
        }
    }
}

@Preview(
    name = "Hoja · asa",
    showBackground = true,
    backgroundColor = 0xFF0B0F19,
    widthDp = 390,
)
@Composable
private fun SheetHandlePreview() {
    VeyraTheme(reducedMotion = true) {
        Column(PreviewBase) {
            VeyraCard(
                shape = VeyraRadius.xlShape,
                contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp),
            ) {
                Column {
                    VeyraSheetHandle()
                    VeyraDivider()
                    Spacer(Modifier.height(VeyraSpace.lg))
                }
            }
        }
    }
}

@Preview(
    name = "Estado vacío",
    showBackground = true,
    backgroundColor = 0xFF0B0F19,
    widthDp = 390,
    heightDp = 600,
)
@Composable
private fun EmptyStatePreview() {
    VeyraTheme(reducedMotion = true) {
        Column(PreviewBase) {
            VeyraEmptyState(
                icon = Icons.Rounded.CloudOff,
                title = "Sin conexión",
                description = "Tus descargas siguen disponibles. Vuelve a intentarlo cuando recuperes la red.",
                actionLabel = "Reintentar",
                onAction = {},
            )
        }
    }
}
