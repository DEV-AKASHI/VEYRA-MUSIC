/*
 * Copyright (C) 2026 SFG545
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

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import dev.sfg.veyra.mobile.ui.scroll.OrchardLazyColumn as LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.PlaylistAdd
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.sfg.veyra.connect.R
import dev.sfg.veyra.mobile.model.Playlist
import dev.sfg.veyra.mobile.model.Track
import dev.sfg.veyra.mobile.ui.theme.CanopyColors
import dev.sfg.veyra.mobile.ui.theme.LocalAccent
import dev.sfg.veyra.mobile.ui.theme.VeyraCyber

/** Selects an existing YouTube Music playlist or creates a new one with the track. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun PlaylistPickerSheet(
    track: Track,
    playlists: List<Playlist>,
    onDismiss: () -> Unit,
    onSelect: (Playlist) -> Unit,
    isSignedIn: Boolean = true,
    onCreatePlaylist: ((String) -> Unit)? = null,
) {
    val isCyberpunk = LocalCyberpunkTheme.current
    val accent = LocalAccent.current
    var creating by remember { mutableStateOf(false) }
    var newTitle by remember { mutableStateOf("") }

    fun submitCreate() {
        val clean = newTitle.trim()
        if (clean.isEmpty() || onCreatePlaylist == null) return
        onCreatePlaylist(clean)
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = if (isCyberpunk) VeyraCyber.surface else Color(0xFF100E22),
        dragHandle = {
            BottomSheetDefaults.DragHandle(
                color = if (isCyberpunk) VeyraCyber.cyan.copy(alpha = 0.6f) else Color.White.copy(alpha = 0.35f),
            )
        },
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            contentPadding = PaddingValues(bottom = 28.dp),
        ) {
            item {
                Text(stringResource(R.string.act_add_to_playlist), style = MaterialTheme.typography.titleLarge, color = CanopyColors.Text)
                Text(
                    track.title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = CanopyColors.Muted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(Modifier.height(14.dp))
            }

            if (onCreatePlaylist != null) {
                item {
                    val cardShape = if (isCyberpunk) VeyraCyber.shape else RoundedCornerShape(18.dp)
                    val cardColor = if (isCyberpunk) VeyraCyber.surface else Color.White.copy(alpha = 0.08f)
                    val cardBorder = if (isCyberpunk) {
                        BorderStroke(VeyraCyber.borderWidth, VeyraCyber.cyan)
                    } else {
                        BorderStroke(1.dp, Color.White.copy(alpha = 0.20f))
                    }

                    if (!isSignedIn) {
                        Surface(
                            shape = cardShape,
                            color = cardColor,
                            border = cardBorder,
                            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                        ) {
                            Text(
                                text = stringResource(R.string.msg_sign_in_playlists),
                                style = MaterialTheme.typography.bodyMedium,
                                color = CanopyColors.Muted,
                                modifier = Modifier.padding(14.dp),
                            )
                        }
                    } else {
                        Surface(
                            onClick = { creating = !creating },
                            shape = cardShape,
                            color = cardColor,
                            border = cardBorder,
                            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                        ) {
                            Column(Modifier.padding(horizontal = 14.dp, vertical = 12.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        shape = if (isCyberpunk) VeyraCyber.shape else CircleShape,
                                        color = if (isCyberpunk) VeyraCyber.acidYellow else accent.copy(alpha = 0.22f),
                                        modifier = Modifier.size(34.dp),
                                    ) {
                                        Icon(
                                            Icons.Rounded.Add,
                                            contentDescription = null,
                                            tint = if (isCyberpunk) VeyraCyber.canvas else accent,
                                            modifier = Modifier.padding(7.dp),
                                        )
                                    }
                                    Spacer(Modifier.width(12.dp))
                                    Text(
                                        text = stringResource(R.string.act_new_playlist),
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                                        color = CanopyColors.Text,
                                    )
                                }

                                AnimatedVisibility(visible = creating) {
                                    Column(Modifier.padding(top = 12.dp)) {
                                        OutlinedTextField(
                                            value = newTitle,
                                            onValueChange = { newTitle = it },
                                            singleLine = true,
                                            placeholder = {
                                                Text(stringResource(R.string.playlist_name_hint), color = CanopyColors.Muted)
                                            },
                                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                                            keyboardActions = KeyboardActions(onDone = { submitCreate() }),
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedBorderColor = if (isCyberpunk) VeyraCyber.cyan else accent,
                                                unfocusedBorderColor = Color.White.copy(alpha = 0.24f),
                                                focusedTextColor = Color.White,
                                                unfocusedTextColor = Color.White,
                                                cursorColor = if (isCyberpunk) VeyraCyber.acidYellow else accent,
                                            ),
                                            shape = if (isCyberpunk) VeyraCyber.shape else RoundedCornerShape(14.dp),
                                            modifier = Modifier.fillMaxWidth(),
                                        )
                                        Spacer(Modifier.height(10.dp))
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.End,
                                        ) {
                                            TextButton(onClick = { creating = false; newTitle = "" }) {
                                                Text(stringResource(R.string.action_cancel), color = CanopyColors.Muted)
                                            }
                                            Spacer(Modifier.width(8.dp))
                                            Button(
                                                onClick = ::submitCreate,
                                                enabled = newTitle.trim().isNotEmpty(),
                                                shape = if (isCyberpunk) VeyraCyber.shape else CircleShape,
                                                colors = ButtonDefaults.buttonColors(
                                                    containerColor = if (isCyberpunk) VeyraCyber.acidYellow else accent,
                                                    contentColor = if (isCyberpunk) VeyraCyber.canvas else Color.White,
                                                ),
                                            ) {
                                                Text(
                                                    stringResource(R.string.act_create_and_add),
                                                    fontWeight = FontWeight.SemiBold,
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (playlists.isEmpty()) {
                item {
                    Text(
                        stringResource(R.string.picker_no_playlists),
                        style = MaterialTheme.typography.bodyMedium,
                        color = CanopyColors.Muted,
                        modifier = Modifier.padding(vertical = 16.dp),
                    )
                }
            } else {
                items(playlists, key = { it.id }) { playlist ->
                    val containsTrack = playlist.tracks.any { it.id == track.id }
                    Surface(
                        onClick = { if (!containsTrack) onSelect(playlist) },
                        enabled = !containsTrack,
                        color = Color.Transparent,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            ArtworkTile(playlist.artworkUrl, playlist.title, Modifier.size(42.dp), 8)
                            Spacer(Modifier.width(14.dp))
                            Column(Modifier.weight(1f)) {
                                Text(playlist.title, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium), color = CanopyColors.Text)
                                Text(
                                    if (containsTrack) stringResource(R.string.picker_already_added) else playlist.author,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = CanopyColors.Muted,
                                )
                            }
                            Icon(
                                Icons.AutoMirrored.Rounded.PlaylistAdd,
                                contentDescription = null,
                                tint = if (containsTrack) CanopyColors.Muted else accent,
                            )
                        }
                    }
                }
            }
        }
    }
}
