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

import android.content.Intent
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.sfg.veyra.mobile.model.Track
import dev.sfg.veyra.mobile.ui.theme.CanopyColors
import dev.sfg.veyra.mobile.ui.theme.VeyraCyber

/**
 * Lyric quote card preview & share sheet styled for Aurora Glass or Cyberpunk Poster.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LyricsShareCardSheet(
    track: Track,
    quoteLines: List<String>,
    onDismiss: () -> Unit,
) {
    if (quoteLines.isEmpty()) return
    val context = LocalContext.current
    val isCyberpunk = LocalCyberpunkTheme.current
    val cardShape = if (isCyberpunk) VeyraCyber.shape else RoundedCornerShape(22.dp)
    val quoteText = quoteLines.joinToString("\n")

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
            Text(
                text = "Tarjeta de Letra",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = CanopyColors.Text,
            )
            Spacer(Modifier.height(14.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(cardShape)
                    .background(
                        if (isCyberpunk) {
                            Brush.verticalGradient(listOf(VeyraCyber.canvas, VeyraCyber.surfaceAlt))
                        } else {
                            Brush.linearGradient(
                                listOf(
                                    Color(0xFF7C3AED).copy(alpha = 0.55f),
                                    Color(0xFF2563EB).copy(alpha = 0.45f),
                                    Color(0xFF0B0F19).copy(alpha = 0.88f),
                                ),
                            )
                        },
                    )
                    .border(
                        BorderStroke(
                            if (isCyberpunk) VeyraCyber.borderWidth else 1.dp,
                            if (isCyberpunk) VeyraCyber.cyan else Color.White.copy(alpha = 0.28f),
                        ),
                        cardShape,
                    )
                    .padding(20.dp),
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        ArtworkTile(track.artworkUrl, track.title, Modifier.size(44.dp), if (isCyberpunk) 6 else 10)
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(
                                text = track.title,
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = Color.White,
                                maxLines = 1,
                            )
                            Text(
                                text = track.artist,
                                style = MaterialTheme.typography.bodySmall,
                                color = if (isCyberpunk) VeyraCyber.cyan else Color.White.copy(alpha = 0.72f),
                                maxLines = 1,
                            )
                        }
                        if (isCyberpunk) {
                            Box(
                                modifier = Modifier
                                    .clip(VeyraCyber.shape)
                                    .background(VeyraCyber.acidYellow)
                                    .padding(horizontal = 6.dp, vertical = 2.dp),
                            ) {
                                Text("VEYRA", color = VeyraCyber.ink, fontSize = 9.sp, fontWeight = FontWeight.Black)
                            }
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = "“$quoteText”",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            lineHeight = 28.sp,
                        ),
                        color = Color.White,
                    )
                }
            }

            Spacer(Modifier.height(16.dp))
            VeyraButton(
                label = "Compartir cita",
                onClick = {
                    val shareBody = "“$quoteText”\n— ${track.title} · ${track.artist} (Veyra Music)"
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, shareBody)
                    }
                    context.startActivity(Intent.createChooser(intent, track.title))
                    onDismiss()
                },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
