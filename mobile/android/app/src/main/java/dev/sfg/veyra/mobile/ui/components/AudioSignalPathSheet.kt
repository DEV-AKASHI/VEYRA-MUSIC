/*
 * Copyright (C) 2026 SFG545 / AKASHI DEV
 *
 * This file is part of Veyra Music.
 *
 * Veyra Music is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 */

package dev.sfg.veyra.mobile.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.Headphones
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.sfg.veyra.mobile.audio.AudioOutput
import dev.sfg.veyra.mobile.model.OrchardSettings
import dev.sfg.veyra.mobile.ui.theme.CanopyColors
import dev.sfg.veyra.mobile.ui.theme.VeyraCyber

/**
 * Interactive Signal Path inspector showing Source -> Veyra DSP -> Physical Output.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AudioSignalPathSheet(
    open: Boolean,
    onDismiss: () -> Unit,
    sourceFormat: String,
    bitrateKbps: Int,
    output: AudioOutput,
    settings: OrchardSettings,
) {
    if (!open) return
    val isCyberpunk = LocalCyberpunkTheme.current

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = if (isCyberpunk) VeyraCyber.surface else Color(0xFF16132E),
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 24.dp)
                .navigationBarsPadding(),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = "Ruta de Señal (Audio Path)",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = CanopyColors.Text,
                )
                StatusPillBadge(
                    label = if (bitrateKbps > 320 || sourceFormat.contains("FLAC", true)) "LOSSLESS" else "HI-FI",
                    status = StatusPillTone.ACTIVE,
                    size = StatusPillSize.SM,
                )
            }
            Spacer(Modifier.height(16.dp))

            val sourceDesc = buildString {
                append(sourceFormat.ifBlank { "Stream AAC/Opus" })
                if (bitrateKbps > 0) append(" • $bitrateKbps kbps")
            }
            SignalStageNode(
                icon = Icons.Rounded.MusicNote,
                title = "1. Fuente de Audio",
                detail = sourceDesc,
                active = true,
                isCyberpunk = isCyberpunk,
            )

            SignalConnector(isCyberpunk)

            val dspParts = buildList {
                if (settings.equalizerConfig.enabled) add("EQ 10B (${settings.equalizerConfig.presetId})")
                if (settings.equalizerConfig.limiterEnabled) add("True Peak Limiter")
                if (settings.equalizerConfig.crossfeedEnabled) add("Crossfeed Binaural")
                if (settings.volumeNormalizationEnabled) add("Normalización")
                if (settings.crossfadeEnabled && settings.smartCrossfade) add("Smart Crossfade")
            }
            SignalStageNode(
                icon = Icons.Rounded.Tune,
                title = "2. Procesamiento DSP Veyra",
                detail = if (dspParts.isEmpty()) "Bit-Direct (Sin procesamiento adicional)" else dspParts.joinToString(" • "),
                active = dspParts.isNotEmpty(),
                isCyberpunk = isCyberpunk,
            )

            SignalConnector(isCyberpunk)

            SignalStageNode(
                icon = Icons.Rounded.Headphones,
                title = "3. Salida Física",
                detail = "${output.name} (${output.type.name.replace('_', ' ')})",
                active = true,
                isCyberpunk = isCyberpunk,
            )
        }
    }
}

@Composable
private fun SignalStageNode(
    icon: ImageVector,
    title: String,
    detail: String,
    active: Boolean,
    isCyberpunk: Boolean,
) {
    val shape = if (isCyberpunk) VeyraCyber.shape else RoundedCornerShape(14.dp)
    val accent = if (isCyberpunk) VeyraCyber.cyan else Color(0xFF38BDF8)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(if (isCyberpunk) VeyraCyber.surfaceAlt else Color.White.copy(alpha = 0.08f))
            .border(
                BorderStroke(
                    if (isCyberpunk) VeyraCyber.borderWidth else 1.dp,
                    if (active) accent.copy(alpha = 0.55f) else Color.White.copy(alpha = 0.16f),
                ),
                shape,
            )
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(if (isCyberpunk) VeyraCyber.shape else CircleShape)
                .background(accent.copy(alpha = 0.18f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, contentDescription = null, tint = accent, modifier = Modifier.size(20.dp))
        }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = CanopyColors.Text)
            Spacer(Modifier.height(2.dp))
            Text(detail, style = MaterialTheme.typography.bodySmall, color = CanopyColors.Muted)
        }
    }
}

@Composable
private fun SignalConnector(isCyberpunk: Boolean) {
    Box(
        modifier = Modifier
            .padding(start = 32.dp)
            .width(2.dp)
            .height(14.dp)
            .background(if (isCyberpunk) VeyraCyber.acidYellow else Color(0xFF8B5CF6).copy(alpha = 0.65f)),
    )
}
