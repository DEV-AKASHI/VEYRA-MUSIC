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

package dev.sfg.veyra.mobile.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import dev.sfg.veyra.mobile.ui.scroll.OrchardLazyColumn as LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.sfg.veyra.mobile.download.DownloadItem
import dev.sfg.veyra.mobile.download.DownloadStatus
import dev.sfg.veyra.mobile.model.CatalogItem
import dev.sfg.veyra.mobile.model.LibraryFilter
import dev.sfg.veyra.mobile.model.LibrarySnapshot
import dev.sfg.veyra.mobile.model.Track
import dev.sfg.veyra.mobile.ui.components.CatalogCard
import dev.sfg.veyra.mobile.ui.components.LocalCyberpunkTheme
import dev.sfg.veyra.mobile.ui.components.MessagePanel
import dev.sfg.veyra.mobile.ui.theme.VeyraCyber
import dev.sfg.veyra.mobile.ui.theme.VeyraRadius
import dev.sfg.veyra.mobile.ui.components.OrchardChromeHeight
import dev.sfg.veyra.mobile.ui.components.OrchardFilterChips
import dev.sfg.veyra.mobile.ui.components.OrchardSectionHeader
import dev.sfg.veyra.mobile.ui.components.TrackRow
import dev.sfg.veyra.mobile.ui.components.filterCatalogItems
import dev.sfg.veyra.mobile.ui.components.filterTracks
import dev.sfg.veyra.mobile.ui.components.normalizeSearchText
import dev.sfg.veyra.mobile.ui.glass.GlassStyle
import dev.sfg.veyra.mobile.ui.glass.LocalGlass
import dev.sfg.veyra.mobile.ui.theme.CanopyColors
import dev.sfg.veyra.mobile.ui.theme.LocalAccent
import androidx.compose.ui.res.stringResource
import dev.sfg.veyra.connect.R

private val LibraryFilter.labelRes: Int
    get() = when (this) {
        LibraryFilter.PLAYLISTS -> R.string.library_filter_playlists
        LibraryFilter.ARTISTS -> R.string.library_filter_artists
        LibraryFilter.ALBUMS -> R.string.library_filter_albums
        LibraryFilter.SONGS -> R.string.library_filter_songs
        LibraryFilter.RECENT -> R.string.library_filter_recent
        LibraryFilter.DOWNLOADS -> R.string.library_filter_downloads
    }

@Composable
fun LibraryScreen(
    library: LibrarySnapshot,
    filter: LibraryFilter,
    onFilterChange: (LibraryFilter) -> Unit,
    downloads: List<DownloadItem> = emptyList(),
    downloadedTrackIds: Set<String> = emptySet(),
    downloadingTrackIds: Set<String> = emptySet(),
    totalBytesUsed: Long = 0L,
    onPlay: (Track) -> Unit,
    onPlayQueue: ((List<Track>, Int) -> Unit)? = null,
    onPlayNext: ((Track) -> Unit)?,
    onAddToQueue: ((Track) -> Unit)?,
    onOpenDetail: (String) -> Unit,
    onDownloadTrack: ((Track) -> Unit)? = null,
    onRemoveDownloadTrack: ((String) -> Unit)? = null,
    onShare: ((Track) -> Unit)? = null,
    isSignedIn: Boolean = true,
    onCreatePlaylist: ((String, (String) -> Unit) -> Unit)? = null,
) {
    val context = LocalContext.current
    val isCyberpunk = LocalCyberpunkTheme.current
    val accent = LocalAccent.current
    var searchQuery by remember { mutableStateOf("") }
    var showCreatePlaylistDialog by remember { mutableStateOf(false) }

    if (showCreatePlaylistDialog && onCreatePlaylist != null) {
        CreatePlaylistDialog(
            onDismiss = { showCreatePlaylistDialog = false },
            onCreate = { title ->
                showCreatePlaylistDialog = false
                onCreatePlaylist(title) { createdId ->
                    onOpenDetail(createdId)
                }
            },
        )
    }

    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = OrchardChromeHeight)) {
            item {
                Column {
                    Column(Modifier.padding(horizontal = 16.dp).padding(top = 16.dp, bottom = 12.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            Text(stringResource(R.string.library_title), style = MaterialTheme.typography.displayLarge.copy(fontWeight = FontWeight.Bold))
                            dev.sfg.veyra.mobile.ui.components.StatusPillBadge(
                                label = context.getString(filter.labelRes).uppercase(),
                                status = dev.sfg.veyra.mobile.ui.components.StatusPillTone.ACTIVE,
                                size = dev.sfg.veyra.mobile.ui.components.StatusPillSize.SM,
                            )
                        }
                        Spacer(Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            dev.sfg.veyra.mobile.ui.components.StatusPillBadge(
                                label = "${library.savedPlaylists.size} LISTAS",
                                status = dev.sfg.veyra.mobile.ui.components.StatusPillTone.INFO,
                                dot = false,
                                size = dev.sfg.veyra.mobile.ui.components.StatusPillSize.SM,
                            )
                            dev.sfg.veyra.mobile.ui.components.StatusPillBadge(
                                label = "${library.likedTracks.size} FAVORITAS",
                                status = dev.sfg.veyra.mobile.ui.components.StatusPillTone.PRIMARY,
                                dot = false,
                                size = dev.sfg.veyra.mobile.ui.components.StatusPillSize.SM,
                            )
                            dev.sfg.veyra.mobile.ui.components.StatusPillBadge(
                                label = "${downloadedTrackIds.size} OFFLINE",
                                status = dev.sfg.veyra.mobile.ui.components.StatusPillTone.SYNC,
                                dot = false,
                                size = dev.sfg.veyra.mobile.ui.components.StatusPillSize.SM,
                            )
                        }
                    }
                    OrchardFilterChips(
                        options = LibraryFilter.entries,
                        selected = filter,
                        label = { context.getString(it.labelRes) },
                        onSelect = onFilterChange,
                    )
                    Spacer(Modifier.height(12.dp))

                    // Plain search field
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        val isCyberpunk = LocalCyberpunkTheme.current
                        val searchShape = if (isCyberpunk) VeyraCyber.shape else CircleShape
                        Surface(
                            shape = searchShape,
                            color = if (isCyberpunk) VeyraCyber.surface else Color.White.copy(alpha = 0.08f),
                            border = BorderStroke(
                                if (isCyberpunk) VeyraCyber.borderWidth else 1.dp,
                                if (isCyberpunk) VeyraCyber.cyan else Color.White.copy(alpha = 0.24f),
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .shadow(if (isCyberpunk) 0.dp else 6.dp, searchShape, spotColor = Color.Black.copy(alpha = 0.45f)),
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Icon(
                                    Icons.Rounded.Search,
                                    contentDescription = null,
                                    tint = Color(0xFF38BDF8),
                                    modifier = Modifier.size(18.dp),
                                )
                                Spacer(Modifier.width(8.dp))
                                Box(
                                    modifier = Modifier.weight(1f),
                                    contentAlignment = Alignment.CenterStart,
                                ) {
                                    if (searchQuery.isEmpty()) {
                                        Text(
                                            text = stringResource(R.string.library_search_in, stringResource(filter.labelRes).lowercase()),
                                            style = TextStyle(
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Normal,
                                                color = Color(0xFF94A3B8),
                                            ),
                                            maxLines = 1,
                                        )
                                    }
                                    BasicTextField(
                                        value = searchQuery,
                                        onValueChange = { searchQuery = it },
                                        modifier = Modifier.fillMaxWidth(),
                                        textStyle = TextStyle(
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = Color.White,
                                        ),
                                        cursorBrush = SolidColor(LocalAccent.current),
                                        singleLine = true,
                                    )
                                }
                                if (searchQuery.isNotEmpty()) {
                                    Surface(
                                        onClick = { searchQuery = "" },
                                        shape = CircleShape,
                                        color = Color.Transparent,
                                        modifier = Modifier.size(22.dp),
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                Icons.Rounded.Close,
                                                contentDescription = stringResource(R.string.action_cancel),
                                                tint = Color.White,
                                                modifier = Modifier.size(13.dp),
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                }
            }
            when (filter) {
                LibraryFilter.PLAYLISTS -> {
                    if (onCreatePlaylist != null) {
                        item {
                            Surface(
                                onClick = {
                                    if (isSignedIn) {
                                        showCreatePlaylistDialog = true
                                    } else {
                                        onCreatePlaylist("", {})
                                    }
                                },
                                shape = if (isCyberpunk) VeyraCyber.shape else RoundedCornerShape(18.dp),
                                color = if (isCyberpunk) VeyraCyber.surface else Color.White.copy(alpha = 0.08f),
                                border = BorderStroke(
                                    if (isCyberpunk) VeyraCyber.borderWidth else 1.dp,
                                    if (isCyberpunk) VeyraCyber.cyan else Color.White.copy(alpha = 0.22f),
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                                    .padding(bottom = 12.dp),
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
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
                                        Column {
                                            Text(
                                                text = stringResource(R.string.act_new_playlist),
                                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                                                color = Color.White,
                                            )
                                            if (!isSignedIn) {
                                                Text(
                                                    text = stringResource(R.string.msg_sign_in_playlists),
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = CanopyColors.Muted,
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    val playlists = library.savedPlaylists.map { CatalogItem.Collection(it) }
                    val filtered = filterCatalogItems(playlists, searchQuery)
                    collections(
                        context.getString(R.string.library_your_playlists),
                        filtered,
                        if (searchQuery.isNotBlank()) context.getString(R.string.library_no_matching_playlists) else context.getString(R.string.library_no_saved_playlists),
                        if (searchQuery.isNotBlank()) context.getString(R.string.library_no_matching_playlists) else context.getString(R.string.library_no_saved_playlists_desc),
                        onOpenDetail,
                    )
                }
                LibraryFilter.ARTISTS -> {
                    val artists = library.savedArtists.map { CatalogItem.Performer(it) }
                    val filtered = filterCatalogItems(artists, searchQuery)
                    collections(
                        context.getString(R.string.library_saved_artists),
                        filtered,
                        if (searchQuery.isNotBlank()) context.getString(R.string.library_no_matching_artists) else context.getString(R.string.library_no_saved_artists),
                        if (searchQuery.isNotBlank()) context.getString(R.string.library_no_matching_artists) else context.getString(R.string.library_no_saved_artists_desc),
                        onOpenDetail,
                    )
                }
                LibraryFilter.ALBUMS -> {
                    val albums = library.savedAlbums.map { CatalogItem.Record(it) }
                    val filtered = filterCatalogItems(albums, searchQuery)
                    collections(
                        context.getString(R.string.library_saved_albums),
                        filtered,
                        if (searchQuery.isNotBlank()) context.getString(R.string.library_no_matching_albums) else context.getString(R.string.library_no_saved_albums),
                        if (searchQuery.isNotBlank()) context.getString(R.string.library_no_matching_albums) else context.getString(R.string.library_no_saved_albums_desc),
                        onOpenDetail,
                    )
                }
                LibraryFilter.SONGS -> {
                    val filtered = filterTracks(library.likedTracks, searchQuery)
                    songs(
                        title = context.getString(R.string.library_saved_songs),
                        values = filtered,
                        emptyTitle = if (searchQuery.isNotBlank()) context.getString(R.string.library_no_matching_songs) else context.getString(R.string.library_no_saved_songs),
                        emptyMessage = if (searchQuery.isNotBlank()) context.getString(R.string.library_no_matching_songs) else context.getString(R.string.library_no_saved_songs_desc),
                        downloadedTrackIds = downloadedTrackIds,
                        downloadingTrackIds = downloadingTrackIds,
                        onPlay = onPlay,
                        onPlayNext = onPlayNext,
                        onAdd = onAddToQueue,
                        onDownloadTrack = onDownloadTrack,
                        onRemoveDownloadTrack = onRemoveDownloadTrack,
                        onOpen = onOpenDetail,
                        onShare = onShare,
                    )
                }
                LibraryFilter.RECENT -> {
                    val filtered = filterTracks(library.recentlyPlayed, searchQuery)
                    tracks(
                        context.getString(R.string.library_recently_played),
                        filtered,
                        if (searchQuery.isNotBlank()) context.getString(R.string.library_no_matching_recent) else context.getString(R.string.library_nothing_played_yet),
                        if (searchQuery.isNotBlank()) context.getString(R.string.library_no_matching_recent) else context.getString(R.string.library_nothing_played_yet_desc),
                        downloadedTrackIds,
                        downloadingTrackIds,
                        onPlay,
                        onPlayNext,
                        onAddToQueue,
                        onDownloadTrack,
                        onRemoveDownloadTrack,
                        onShare,
                        onOpenDetail,
                    )
                }
                LibraryFilter.DOWNLOADS -> {
                    val normalizedQuery = normalizeSearchText(searchQuery)
                    val filtered = if (normalizedQuery.isNotBlank()) {
                        downloads.filter {
                            normalizeSearchText(it.track.title).contains(normalizedQuery) ||
                                normalizeSearchText(it.track.artist).contains(normalizedQuery) ||
                                normalizeSearchText(it.track.album).contains(normalizedQuery)
                        }
                    } else downloads
                    downloadsList(
                        downloads = filtered,
                        totalBytesUsed = totalBytesUsed,
                        onPlay = onPlay,
                        onPlayQueue = onPlayQueue,
                        onRemoveDownload = { id -> onRemoveDownloadTrack?.invoke(id) },
                    )
                }
            }
        }
}

private fun LazyListScope.downloadsList(
    downloads: List<DownloadItem>,
    totalBytesUsed: Long,
    onPlay: (Track) -> Unit,
    onPlayQueue: ((List<Track>, Int) -> Unit)? = null,
    onRemoveDownload: (String) -> Unit,
) {
    val completed = downloads.filter { it.status == DownloadStatus.COMPLETED }
    val active = downloads.filter { it.status == DownloadStatus.DOWNLOADING || it.status == DownloadStatus.QUEUED }
    val failed = downloads.filter { it.status == DownloadStatus.FAILED }

    if (active.isEmpty() && completed.isEmpty() && failed.isEmpty()) {
        item {
            MessagePanel(
                title = stringResource(R.string.library_no_downloaded_tracks),
                message = stringResource(R.string.library_no_downloaded_tracks_desc),
            )
        }
        return
    }

    if (completed.isNotEmpty()) {
        item {
            Text(
                stringResource(R.string.library_offline_tracks_used, formatStorageSize(totalBytesUsed)),
                style = MaterialTheme.typography.bodyMedium,
                color = CanopyColors.Muted,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
            )
        }
    }

    if (active.isNotEmpty()) {
        item {
            Text(
                stringResource(R.string.library_downloading_count, active.size),
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = LocalAccent.current,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            )
        }
        items(active, key = { "active_${it.track.id}" }) { item ->
            DownloadingRow(item = item, onCancel = { onRemoveDownload(item.track.id) })
        }
    }

    if (completed.isNotEmpty()) {
        item {
            Spacer(Modifier.height(8.dp))
            Text(
                stringResource(R.string.library_downloaded_count, completed.size),
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = CanopyColors.Muted,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            )
        }
        items(completed, key = { "completed_${it.track.id}" }) { item ->
            val trackIndex = completed.indexOf(item).coerceAtLeast(0)
            DownloadedTrackRow(
                item = item,
                onPlay = {
                    if (onPlayQueue != null) {
                        onPlayQueue(completed.map { it.track }, trackIndex)
                    } else {
                        onPlay(item.track)
                    }
                },
                onDelete = { onRemoveDownload(item.track.id) },
            )
        }
    }

    if (failed.isNotEmpty()) {
        item {
            Spacer(Modifier.height(16.dp))
            Text(
                stringResource(R.string.library_failed_count, failed.size),
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            )
        }
        items(failed, key = { "failed_${it.track.id}" }) { item ->
            DownloadingRow(item = item, onCancel = { onRemoveDownload(item.track.id) })
        }
    }
}

private fun LazyListScope.songs(
    title: String,
    values: List<Track>,
    emptyTitle: String,
    emptyMessage: String,
    downloadedTrackIds: Set<String>,
    downloadingTrackIds: Set<String> = emptySet(),
    onPlay: (Track) -> Unit,
    onPlayNext: ((Track) -> Unit)?,
    onAdd: ((Track) -> Unit)?,
    onDownloadTrack: ((Track) -> Unit)? = null,
    onRemoveDownloadTrack: ((String) -> Unit)? = null,
    onOpen: ((String) -> Unit)? = null,
    onShare: ((Track) -> Unit)? = null,
) {
    if (values.isEmpty()) {
        item { MessagePanel(emptyTitle, emptyMessage) }
        return
    }
    item { OrchardSectionHeader(title) }
    itemsIndexed(values, key = { index, track -> "${track.id}_$index" }) { _, track ->
        TrackRow(
            track = track,
            onPlay = { onPlay(track) },
            onPlayNext = onPlayNext?.let { action -> { action(track) } },
            onAddToQueue = onAdd?.let { action -> { action(track) } },
            onDownload = onDownloadTrack?.let { action -> { action(track) } },
            onRemoveDownload = onRemoveDownloadTrack?.let { action -> { action(track.id) } },
            isDownloaded = downloadedTrackIds.contains(track.id),
            isDownloading = downloadingTrackIds.contains(track.id),
            onShare = onShare?.let { action -> { action(track) } },
            onViewAlbum = onOpen?.takeIf { track.albumId.isNotBlank() }?.let { nav -> { nav(track.albumId) } },
            onViewArtist = onOpen?.takeIf { track.artistId.isNotBlank() }?.let { nav -> { nav(track.artistId) } },
        )
    }
}

private fun LazyListScope.collections(
    title: String,
    values: List<CatalogItem>,
    emptyTitle: String,
    emptyMessage: String,
    onOpen: (String) -> Unit,
) {
    if (values.isEmpty()) {
        item {
            MessagePanel(emptyTitle, emptyMessage)
        }
        return
    }

    item {
        OrchardSectionHeader(title)
    }

    val rows = values.chunked(2)

    items(
        items = rows,
        key = { row ->
            row.joinToString("|") { item -> item.stableId }
        },
    ) { row ->
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            row.forEach { catalogItem ->
                Box(
                    modifier = Modifier.weight(1f),
                ) {
                    CatalogCard(
                        catalogItem,
                        { onOpen(catalogItem.stableId) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            if (row.size < 2) {
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}

private fun LazyListScope.tracks(
    title: String,
    values: List<Track>,
    emptyTitle: String,
    emptyMessage: String,
    downloadedTrackIds: Set<String>,
    downloadingTrackIds: Set<String>,
    onPlay: (Track) -> Unit,
    onPlayNext: ((Track) -> Unit)?,
    onAdd: ((Track) -> Unit)?,
    onDownloadTrack: ((Track) -> Unit)? = null,
    onRemoveDownloadTrack: ((String) -> Unit)? = null,
    onShare: ((Track) -> Unit)? = null,
    onOpen: ((String) -> Unit)? = null,
) {
    if (values.isEmpty()) {
        item { MessagePanel(emptyTitle, emptyMessage) }
        return
    }
    item { OrchardSectionHeader(title) }
    itemsIndexed(values, key = { index, track -> "${track.id}_$index" }) { _, track ->
        val isDownloaded = downloadedTrackIds.contains(track.id)
        val isDownloading = downloadingTrackIds.contains(track.id)
        TrackRow(
            track = track,
            onPlay = { onPlay(track) },
            onPlayNext = onPlayNext?.let { action -> { action(track) } },
            onAddToQueue = onAdd?.let { action -> { action(track) } },
            onDownload = onDownloadTrack?.let { action -> { action(track) } },
            onRemoveDownload = onRemoveDownloadTrack?.let { action -> { action(track.id) } },
            isDownloaded = isDownloaded,
            isDownloading = isDownloading,
            onShare = onShare?.let { action -> { action(track) } },
            onViewAlbum = onOpen?.takeIf { track.albumId.isNotBlank() }?.let { nav -> { nav(track.albumId) } },
            onViewArtist = onOpen?.takeIf { track.artistId.isNotBlank() }?.let { nav -> { nav(track.artistId) } },
        )
    }
}

@Composable
private fun CreatePlaylistDialog(
    onDismiss: () -> Unit,
    onCreate: (String) -> Unit,
) {
    val isCyberpunk = LocalCyberpunkTheme.current
    val accent = LocalAccent.current
    var title by remember { mutableStateOf("") }

    fun submit() {
        val clean = title.trim()
        if (clean.isNotEmpty()) onCreate(clean)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = if (isCyberpunk) VeyraCyber.surface else Color(0xFF13102A),
        shape = if (isCyberpunk) VeyraCyber.shape else RoundedCornerShape(22.dp),
        title = {
            Text(
                text = stringResource(R.string.act_new_playlist),
                color = Color.White,
                fontWeight = FontWeight.Bold,
            )
        },
        text = {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                singleLine = true,
                placeholder = {
                    Text(stringResource(R.string.playlist_name_hint), color = CanopyColors.Muted)
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { submit() }),
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
        },
        confirmButton = {
            Button(
                onClick = ::submit,
                enabled = title.trim().isNotEmpty(),
                shape = if (isCyberpunk) VeyraCyber.shape else CircleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isCyberpunk) VeyraCyber.acidYellow else accent,
                    contentColor = if (isCyberpunk) VeyraCyber.canvas else Color.White,
                ),
            ) {
                Text(stringResource(R.string.act_create), fontWeight = FontWeight.SemiBold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.action_cancel), color = CanopyColors.Muted)
            }
        },
    )
}

