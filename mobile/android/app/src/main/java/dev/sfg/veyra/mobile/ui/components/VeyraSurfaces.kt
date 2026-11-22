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

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.sfg.veyra.mobile.ui.glass.GlassSurface
import dev.sfg.veyra.mobile.ui.glass.GlassTone
import dev.sfg.veyra.mobile.ui.theme.CanopyColors
import dev.sfg.veyra.mobile.ui.theme.LocalAccent
import dev.sfg.veyra.mobile.ui.theme.VeyraRadius
import dev.sfg.veyra.mobile.ui.theme.VeyraSize
import dev.sfg.veyra.mobile.ui.theme.VeyraSpace

/**
 * Contenedor de contenido compartido. Delega en [GlassSurface] para que cards,
 * listas y paneles respondan igual a temas, glass, press, foco y disabled.
 */
@Composable
fun VeyraCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    onClickLabel: String? = null,
    selected: Boolean = false,
    enabled: Boolean = true,
    tone: GlassTone = GlassTone.PANEL,
    shape: Shape = VeyraRadius.mdShape,
    contentPadding: PaddingValues = PaddingValues(VeyraSpace.lg),
    content: @Composable BoxScope.() -> Unit,
) {
    GlassSurface(
        modifier = modifier,
        tone = tone,
        shape = shape,
        selected = selected,
        enabled = enabled,
        contentPadding = contentPadding,
        onClick = onClick,
        onClickLabel = onClickLabel,
        content = content,
    )
}

/** Divisor hairline del sistema; [strong] para separar secciones. */
@Composable
fun VeyraDivider(
    modifier: Modifier = Modifier,
    strong: Boolean = false,
    inset: Dp = 0.dp,
) {
    Box(
        modifier
            .padding(horizontal = inset)
            .fillMaxWidth()
            .height(VeyraSize.hairline)
            .background(if (strong) CanopyColors.RuleStrong else CanopyColors.Rule),
    )
}

/**
 * Estado vacío canónico: icono en disco de acento, título, explicación y una
 * acción opcional. El título es heading para TalkBack.
 */
@Composable
fun VeyraEmptyState(
    icon: ImageVector,
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
) {
    val accent = LocalAccent.current

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = VeyraSpace.xl, vertical = VeyraSpace.xxl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .background(accent.copy(alpha = 0.14f), CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = accent,
                modifier = Modifier.size(VeyraSize.iconLg),
            )
        }
        Spacer(Modifier.height(VeyraSpace.lg))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = CanopyColors.Text,
            textAlign = TextAlign.Center,
            modifier = Modifier.semantics { heading() },
        )
        Spacer(Modifier.height(VeyraSpace.sm))
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            color = CanopyColors.Muted,
            textAlign = TextAlign.Center,
            modifier = Modifier.widthIn(max = 320.dp),
        )
        if (actionLabel != null && onAction != null) {
            Spacer(Modifier.height(VeyraSpace.lg))
            VeyraButton(
                label = actionLabel,
                onClick = onAction,
                variant = VeyraButtonVariant.SECONDARY,
            )
        }
    }
}
