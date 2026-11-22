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

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.CloudOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import dev.sfg.veyra.mobile.ui.scroll.OrchardLazyColumn as LazyColumn
import dev.sfg.veyra.mobile.ui.scroll.OrchardLazyRow as LazyRow
import dev.sfg.veyra.mobile.auth.AuthState
import dev.sfg.veyra.mobile.download.DownloadItem
import dev.sfg.veyra.mobile.download.DownloadStatus
import dev.sfg.veyra.mobile.model.Album
import dev.sfg.veyra.mobile.model.Artist
import dev.sfg.veyra.mobile.model.BuiltInHomeSection
import dev.sfg.veyra.mobile.model.CatalogItem
import dev.sfg.veyra.mobile.model.CatalogSection
import dev.sfg.veyra.mobile.model.LibraryFilter
import dev.sfg.veyra.mobile.model.LibrarySnapshot
import dev.sfg.veyra.mobile.model.LoadState
import dev.sfg.veyra.mobile.model.OrchardSettings
import dev.sfg.veyra.mobile.model.Playlist
import dev.sfg.veyra.mobile.model.Track
import dev.sfg.veyra.mobile.ui.components.ArtworkTile
import dev.sfg.veyra.mobile.ui.components.CatalogSectionBottomSheet
import dev.sfg.veyra.mobile.ui.components.CyberCutCompactShape
import dev.sfg.veyra.mobile.ui.components.CyberCutHeroShape
import dev.sfg.veyra.mobile.ui.components.CyberScrambleText
import dev.sfg.veyra.mobile.ui.components.ExplicitBadge
import dev.sfg.veyra.mobile.ui.components.HomeSectionShimmer
import dev.sfg.veyra.mobile.ui.components.LocalCyberpunkTheme
import dev.sfg.veyra.mobile.ui.components.MessagePanel
import dev.sfg.veyra.mobile.ui.components.OrchardChromeHeight
import dev.sfg.veyra.mobile.ui.components.StatusPillBadge
import dev.sfg.veyra.mobile.ui.components.StatusPillSize
import dev.sfg.veyra.mobile.ui.components.StatusPillTone
import dev.sfg.veyra.mobile.ui.components.VeyraGlyphIcon
import dev.sfg.veyra.mobile.ui.components.VeyraIconKind
import dev.sfg.veyra.mobile.ui.components.VeyraLiveEqualizerBars
import dev.sfg.veyra.mobile.ui.components.VeyraMark
import dev.sfg.veyra.mobile.ui.components.VeyraWordmark
import dev.sfg.veyra.mobile.ui.components.cyberCornerBrackets
import dev.sfg.veyra.mobile.ui.components.cyberGlitchBurst
import dev.sfg.veyra.mobile.ui.components.cyberScanBeam
import dev.sfg.veyra.mobile.ui.components.liquidAuroraTouchRipple
import dev.sfg.veyra.mobile.ui.components.liquidSpecularSheen
import androidx.compose.foundation.border
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import dev.sfg.veyra.connect.R
import dev.sfg.veyra.mobile.ui.glass.LocalGlass
import dev.sfg.veyra.mobile.ui.theme.CanopyColors
import dev.sfg.veyra.mobile.ui.theme.LocalAccent
import dev.sfg.veyra.mobile.ui.theme.LocalReducedMotion
import dev.sfg.veyra.mobile.ui.theme.VeyraCyber
import dev.sfg.veyra.mobile.ui.theme.VeyraRadius
import dev.sfg.veyra.mobile.ui.tv.tvFocusable
import java.util.Calendar

private data class QuickGridItem(
    val id: String,
    val title: String,
    val artworkUrl: String,
    val icon: ImageVector? = null,
    val gradient: Brush? = null,
    val onClick: () -> Unit,
    val onPlay: () -> Unit,
)

data class HomeSectionSheetState(
    val title: String,
    val initialItems: List<CatalogItem>,
    val browseId: String = "",
    val params: String = "",
)

@Composable
fun HomeScreen(
    settings: OrchardSettings,
    state: LoadState<List<CatalogSection>>,
    library: LibrarySnapshot,
    auth: AuthState = AuthState.SignedOut,
    downloads: List<DownloadItem> = emptyList(),
    downloadedTrackIds: Set<String> = emptySet(),
    isOffline: Boolean = false,
    activeTrackId: String? = null,
    activeArtistName: String? = null,
    isPlaying: Boolean = false,
    onRefresh: () -> Unit,
    onSearch: () -> Unit,
    onLibrary: (LibraryFilter) -> Unit,
    onDevices: () -> Unit,
    onPlay: (Track) -> Unit,
    onOpenDetail: (String) -> Unit,
    onEditLayout: () -> Unit = {},
    onToggleLike: (Track) -> Unit,
    onPlayNext: ((Track) -> Unit)? = null,
    onAddToQueue: ((Track) -> Unit)? = null,
    onAddToPlaylist: ((Track) -> Unit)? = null,
    onShare: ((Track) -> Unit)? = null,
    onOpenProfile: () -> Unit = {},
    onFetchSectionItems: (suspend (String, String) -> List<CatalogItem>)? = null,
    onPlayItem: ((CatalogItem) -> Unit)? = null,
    onPlayCollection: ((String, String) -> Unit)? = null,
) {
    if (dev.sfg.veyra.mobile.ui.foldable.isFoldableOrWideLayout()) {
        dev.sfg.veyra.mobile.ui.foldable.FoldableHomeScreen(
            settings = settings,
            state = state,
            library = library,
            auth = auth,
            downloads = downloads,
            downloadedTrackIds = downloadedTrackIds,
            isOffline = isOffline,
            onRefresh = onRefresh,
            onSearch = onSearch,
            onLibrary = onLibrary,
            onDevices = onDevices,
            onPlay = onPlay,
            onOpenDetail = onOpenDetail,
            onEditLayout = onEditLayout,
            onToggleLike = onToggleLike,
            onPlayNext = onPlayNext,
            onAddToQueue = onAddToQueue,
            onAddToPlaylist = onAddToPlaylist,
            onShare = onShare,
            onOpenProfile = onOpenProfile,
            onFetchSectionItems = onFetchSectionItems,
            onPlayItem = onPlayItem,
            onPlayCollection = onPlayCollection,
        )
        return
    }

    val glass = LocalGlass.current.enabled
    val context = LocalContext.current
    var activeSectionSheet by remember { mutableStateOf<HomeSectionSheetState?>(null) }

    val playCatalogItem: (CatalogItem) -> Unit = { item ->
        if (onPlayItem != null) {
            onPlayItem(item)
        } else if (onPlayCollection != null) {
            when (item) {
                is CatalogItem.Song -> onPlay(item.track)
                is CatalogItem.Collection -> onPlayCollection(item.playlist.id, item.title)
                is CatalogItem.Record -> onPlayCollection(item.album.id, item.title)
                is CatalogItem.Performer -> onPlayCollection(item.artist.id, item.title)
                is CatalogItem.Category -> onOpenDetail(item.stableId)
            }
        } else {
            when (item) {
                is CatalogItem.Song -> onPlay(item.track)
                else -> onOpenDetail(item.stableId)
            }
        }
    }

    val openCatalogItem: (CatalogItem) -> Unit = { item ->
        when (item) {
            is CatalogItem.Song -> onPlay(item.track)
            else -> onOpenDetail(item.stableId)
        }
    }

    activeSectionSheet?.let { sheet ->
        CatalogSectionBottomSheet(
            title = sheet.title,
            initialItems = sheet.initialItems,
            browseId = sheet.browseId,
            params = sheet.params,
            onFetchFullItems = onFetchSectionItems,
            onPlay = onPlay,
            onPlayItem = playCatalogItem,
            onOpen = onOpenDetail,
            onDismiss = { activeSectionSheet = null },
        )
    }

    val completedDownloads =
        remember(downloads) {
            downloads.filter { it.status == DownloadStatus.COMPLETED && it.filePath.isNotBlank() }
        }
    val downloadedTracks: List<Track> =
        remember(completedDownloads) { completedDownloads.map { it.track } }
    val effectiveOffline = isOffline || state is LoadState.Error

    val offlinePlaylistItems: List<CatalogItem.Collection> =
        remember(downloadedTracks, downloadedTrackIds, library.savedPlaylists) {
            buildList {
                if (downloadedTracks.isNotEmpty()) {
                    add(
                        CatalogItem.Collection(
                            Playlist(
                                id = "offline_downloads",
                                title = context.getString(R.string.home_downloaded_music),
                                author = "Veyra Music",
                                artworkUrl =
                                    downloadedTracks
                                        .firstOrNull { it.artworkUrl.isNotBlank() }
                                        ?.artworkUrl
                                        .orEmpty(),
                                description = context.getString(R.string.home_downloaded_music_desc),
                                tracks = downloadedTracks,
                            )
                        )
                    )
                }
                library.savedPlaylists.forEach { playlist ->
                    val matching = playlist.tracks.filter { it.id in downloadedTrackIds }
                    if (matching.isNotEmpty()) {
                        add(CatalogItem.Collection(playlist.copy(tracks = matching)))
                    }
                }
            }
        }

    val offlineArtistItems: List<CatalogItem.Performer> =
        remember(downloadedTracks, library.savedArtists) {
            val grouped = mutableMapOf<String, MutableList<Track>>()
            downloadedTracks.forEach { track ->
                if (track.artist.isNotBlank()) {
                    grouped.getOrPut(track.artist) { mutableListOf() }.add(track)
                }
            }
            grouped.map { (artistName, artistTracks) ->
                val artistId =
                    artistTracks.firstOrNull { it.artistId.isNotBlank() }?.artistId ?: artistName
                val artworkUrl =
                    library.savedArtists
                        .firstOrNull { it.name.equals(artistName, ignoreCase = true) }
                        ?.artworkUrl
                        ?: artistTracks
                            .firstOrNull { it.artworkUrl.isNotBlank() }
                            ?.artworkUrl
                            .orEmpty()
                CatalogItem.Performer(
                    Artist(
                        id = artistId,
                        name = artistName,
                        artworkUrl = artworkUrl,
                        subtitle =
                            context.getString(R.string.home_downloaded_songs_count, artistTracks.size),
                    )
                )
            }
        }

    val offlineAlbumItems: List<CatalogItem.Record> =
        remember(downloadedTracks) {
            val grouped = mutableMapOf<String, MutableList<Track>>()
            downloadedTracks.forEach { track ->
                if (track.album.isNotBlank()) {
                    grouped.getOrPut(track.album) { mutableListOf() }.add(track)
                }
            }
            grouped.map { (albumTitle, albumTracks) ->
                val albumId =
                    albumTracks.firstOrNull { it.albumId.isNotBlank() }?.albumId ?: albumTitle
                val artist = albumTracks.firstOrNull()?.artist.orEmpty()
                val artworkUrl =
                    albumTracks.firstOrNull { it.artworkUrl.isNotBlank() }?.artworkUrl.orEmpty()
                CatalogItem.Record(
                    Album(
                        id = albumId,
                        title = albumTitle,
                        artist = artist,
                        artworkUrl = artworkUrl,
                        year = "",
                        tracks = albumTracks,
                    )
                )
            }
        }

    // Hero items for the featured carousel (Apple Music / ArchiveTune spotlight)
    val featuredHeroItems = remember(state, library) {
        val fromState = if (state is LoadState.Content) {
            state.value.flatMap { it.items }.filter { it.artworkUrl.isNotBlank() }.distinctBy { it.stableId }
        } else emptyList()
        val fromPlaylists = library.savedPlaylists.map { CatalogItem.Collection(it) }
        val combined = (fromState + fromPlaylists).distinctBy { it.stableId }.take(5)
        combined
    }

    // Spotify-style 6-grid quick access items
    val quickGridItems = remember(library, state) {
        buildList {
            // 1. Playlists
            library.savedPlaylists.take(6).forEach { playlist ->
                add(
                    QuickGridItem(
                        id = "pl_${playlist.id}",
                        title = playlist.title,
                        artworkUrl = playlist.artworkUrl,
                        onClick = { onOpenDetail(playlist.id) },
                        onPlay = { playCatalogItem(CatalogItem.Collection(playlist)) },
                    )
                )
            }
            // 2. Saved Albums
            if (size < 6) {
                library.savedAlbums.take(6 - size).forEach { album ->
                    add(
                        QuickGridItem(
                            id = "alb_${album.id}",
                            title = album.title,
                            artworkUrl = album.artworkUrl,
                            onClick = { onOpenDetail(album.id) },
                            onPlay = { playCatalogItem(CatalogItem.Record(album)) },
                        )
                    )
                }
            }
            // 3. Catalog items fallback
            if (size < 6 && state is LoadState.Content) {
                val stateItems = state.value.flatMap { it.items }.filter { it.artworkUrl.isNotBlank() }
                stateItems.distinctBy { it.stableId }.take(6 - size).forEach { item ->
                    add(
                        QuickGridItem(
                            id = "st_${item.stableId}",
                            title = item.title,
                            artworkUrl = item.artworkUrl,
                            onClick = { openCatalogItem(item) },
                            onPlay = { playCatalogItem(item) },
                        )
                    )
                }
            }
        }.take(6)
    }

    val isCyberpunkHome = LocalCyberpunkTheme.current
    val listState = rememberLazyListState()
    val showStickyHeader by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex > 0 || listState.firstVisibleItemScrollOffset > 130
        }
    }
    Box(modifier = Modifier.fillMaxSize()) {
        if (!isCyberpunkHome) {
            val reduceHomeMotion = LocalReducedMotion.current
            val homeOrbPhase = if (!reduceHomeMotion) {
                val inf = rememberInfiniteTransition(label = "HomeAuroraOrbs")
                val v by inf.animateFloat(
                    initialValue = 0f,
                    targetValue = 1f,
                    animationSpec = infiniteRepeatable(tween(14_000, easing = FastOutSlowInEasing), RepeatMode.Reverse),
                    label = "HomeOrbPhase",
                )
                v
            } else 0f
            // Aurora top atmospheric dual-orb mesh glow
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(380.dp)
                    .drawBehind {
                        val c1 = Offset(size.width * (0.24f + 0.16f * homeOrbPhase), size.height * 0.24f)
                        val c2 = Offset(size.width * (0.78f - 0.14f * homeOrbPhase), size.height * 0.44f)
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(Color(0xFF7C3AED).copy(alpha = 0.38f), Color.Transparent),
                                center = c1,
                                radius = size.width * 0.65f,
                            ),
                            center = c1,
                            radius = size.width * 0.65f,
                        )
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(Color(0xFF38BDF8).copy(alpha = 0.26f), Color.Transparent),
                                center = c2,
                                radius = size.width * 0.56f,
                            ),
                            center = c2,
                            radius = size.width * 0.56f,
                        )
                    }
                    .background(
                        Brush.verticalGradient(
                            0f to Color(0xFF5B21B6).copy(alpha = 0.28f),
                            0.50f to Color(0xFF1E40AF).copy(alpha = 0.14f),
                            1f to Color.Transparent,
                        ),
                    ),
            )
        }

        AnimatedVisibility(
            visible = showStickyHeader,
            enter = fadeIn(tween(180)) + slideInVertically(tween(200)) { -it / 2 },
            exit = fadeOut(tween(140)) + slideOutVertically(tween(150)) { -it / 2 },
            modifier = Modifier
                .align(Alignment.TopCenter)
                .zIndex(12f),
        ) {
            HomeStickyCompactBar(
                auth = auth,
                isPlaying = isPlaying,
                onSearch = onSearch,
                onProfile = onOpenProfile,
            )
        }

        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = OrchardChromeHeight),
        ) {
            // Cabecera superior oficial de Veyra Music
            item {
                HomeHeader(
                    auth = auth,
                    onSearch = onSearch,
                    onProfile = onOpenProfile,
                    onLibrary = onLibrary,
                )
            }

            // Quick-Access Grid: Aurora Editorial Staggered Grid vs Cyberpunk Brutalist Grid
            if (quickGridItems.isNotEmpty() && !effectiveOffline) {
                item {
                    if (isCyberpunkHome) {
                        SpotifyQuickGrid(items = quickGridItems)
                    } else {
                        AuroraEditorialGrid(items = quickGridItems)
                    }
                    Spacer(Modifier.height(18.dp))
                }
            }

        // Featured Hero Carousel Banner (Apple Music / ArchiveTune spotlight)
        if (!effectiveOffline && featuredHeroItems.isNotEmpty()) {
            item {
                FeaturedHeroCarousel(
                    items = featuredHeroItems,
                    onPlay = { item -> playCatalogItem(item) },
                    onClick = { item -> openCatalogItem(item) },
                )
                Spacer(Modifier.height(18.dp))
            }
        }

        // Offline Mode Banner
        if (effectiveOffline) {
            item {
                val isCyberOffline = LocalCyberpunkTheme.current
                Surface(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp),
                    shape = if (isCyberOffline) VeyraCyber.shape else RoundedCornerShape(16.dp),
                    color = if (isCyberOffline) VeyraCyber.surface else Color.White.copy(alpha = 0.09f),
                    border = BorderStroke(
                        if (isCyberOffline) VeyraCyber.borderWidth else 1.dp,
                        if (isCyberOffline) VeyraCyber.acidYellow else Color.White.copy(alpha = 0.24f),
                    ),
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            Icons.Rounded.CloudOff,
                            contentDescription = null,
                            tint = LocalAccent.current,
                            modifier = Modifier.size(22.dp),
                        )
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                stringResource(R.string.home_offline_mode),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = CanopyColors.Text,
                            )
                            Text(
                                if (downloadedTracks.isNotEmpty())
                                    stringResource(
                                        R.string.home_showing_downloaded,
                                        downloadedTracks.size,
                                    )
                                else stringResource(R.string.home_no_internet),
                                style = MaterialTheme.typography.bodySmall,
                                color = CanopyColors.Muted,
                            )
                        }
                    }
                }
                Spacer(Modifier.height(14.dp))
            }
        }

        val layoutConfig =
            if (effectiveOffline) settings.homeLayoutOffline else settings.homeLayoutOnline

        layoutConfig.forEach { config ->
            if (!config.enabled) return@forEach

            when (config.section) {
                BuiltInHomeSection.YOUR_PLAYLISTS -> {
                    if (effectiveOffline) return@forEach
                    val playlistItems =
                        library.savedPlaylists
                            .map { CatalogItem.Collection(it) }
                            .ifEmpty { extractItemsOfKind<CatalogItem.Collection>(state) }
                            .distinctBy { it.stableId }
                    if (playlistItems.isNotEmpty()) {
                        item {
                            val sectionTitle = stringResource(R.string.library_your_playlists)
                            HomeSectionHeader(
                                title = sectionTitle,
                                onSeeAll = {
                                    activeSectionSheet = HomeSectionSheetState(
                                        title = sectionTitle,
                                        initialItems = playlistItems,
                                    )
                                },
                            )
                        }
                        item {
                            LazyRow(
                                contentPadding = PaddingValues(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(14.dp),
                            ) {
                                itemsIndexed(
                                    playlistItems,
                                    key = { index, item -> "pl_${item.stableId}_$index" },
                                ) { _, item ->
                                    if (glass) {
                                        GlassJumpBackInCard(
                                            item = item,
                                            onClick = { openCatalogItem(item) },
                                            onPlay = { playCatalogItem(item) },
                                        )
                                    } else {
                                        PlaylistCard(
                                            item = item,
                                            onClick = { openCatalogItem(item) },
                                        )
                                    }
                                }
                            }
                        }
                        item { Spacer(Modifier.height(20.dp)) }
                    }
                }
                BuiltInHomeSection.SUBSCRIBED_ARTISTS -> {
                    if (effectiveOffline) return@forEach
                    val artistItems =
                        library.savedArtists
                            .map { CatalogItem.Performer(it) }
                            .ifEmpty { extractItemsOfKind<CatalogItem.Performer>(state) }
                            .distinctBy { it.stableId }
                    if (artistItems.isNotEmpty()) {
                        item {
                            val sectionTitle = stringResource(R.string.home_keep_listening)
                            HomeSectionHeader(
                                title = sectionTitle,
                                subtitle = stringResource(R.string.home_subscribed_artists_desc),
                                onSeeAll = {
                                    activeSectionSheet = HomeSectionSheetState(
                                        title = sectionTitle,
                                        initialItems = artistItems,
                                    )
                                },
                            )
                        }
                        item {
                            LazyRow(
                                contentPadding = PaddingValues(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                            ) {
                                itemsIndexed(
                                    artistItems,
                                    key = { index, item -> "art_${item.stableId}_$index" },
                                ) { _, item ->
                                    CircularArtistCard(
                                        item = item,
                                        isActiveArtist = !activeArtistName.isNullOrBlank() &&
                                            activeArtistName.contains(item.title, ignoreCase = true),
                                        isPlaying = isPlaying,
                                        onClick = { openCatalogItem(item) },
                                    )
                                }
                            }
                        }
                        item { Spacer(Modifier.height(20.dp)) }
                    }
                }
                BuiltInHomeSection.TOP_SONGS -> {
                    if (effectiveOffline) return@forEach
                    val songTracks =
                        library.mostPlayed
                            .ifEmpty { library.likedTracks }
                            .ifEmpty { library.recentlyPlayed }
                            .ifEmpty {
                                extractItemsOfKind<CatalogItem.Song>(state).map { it.track }
                            }
                            .distinctBy { it.id }
                    if (songTracks.isNotEmpty()) {
                        item {
                            val sectionTitle = stringResource(R.string.home_latest_songs)
                            HomeSectionHeader(
                                title = sectionTitle,
                                subtitle = stringResource(R.string.home_latest_songs_desc),
                                onSeeAll = {
                                    activeSectionSheet = HomeSectionSheetState(
                                        title = sectionTitle,
                                        initialItems = songTracks.map { CatalogItem.Song(it) },
                                    )
                                },
                            )
                        }
                        itemsIndexed(
                            songTracks.take(5),
                            key = { index, track -> "top_${track.id}_$index" },
                        ) { index, track ->
                            val liked = library.likedTracks.any { it.id == track.id }
                            RankedSongRow(
                                rank = index + 1,
                                track = track,
                                liked = liked,
                                isActive = !activeTrackId.isNullOrBlank() && track.id == activeTrackId,
                                isPlaying = isPlaying,
                                onPlay = { onPlay(track) },
                                onToggleLike = { onToggleLike(track) },
                                onPlayNext = onPlayNext,
                                onAddToQueue = onAddToQueue,
                                onAddToPlaylist = onAddToPlaylist,
                                onShare = onShare,
                            )
                        }
                        item { Spacer(Modifier.height(20.dp)) }
                    }
                }
                BuiltInHomeSection.RECOMMENDATIONS -> {
                    if (effectiveOffline) return@forEach
                    when (state) {
                        is LoadState.Content -> {
                            state.value.forEachIndexed { sectionIndex, section ->
                                item(key = "head:${section.id}") {
                                    HomeSectionHeader(
                                        title = section.title,
                                        onSeeAll = {
                                            activeSectionSheet = HomeSectionSheetState(
                                                title = section.title,
                                                initialItems = section.items,
                                                browseId = section.browseId,
                                                params = section.params,
                                            )
                                        },
                                    )
                                }
                                item(key = "rail:${section.id}") {
                                    LazyRow(
                                        contentPadding = PaddingValues(horizontal = 16.dp),
                                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                                    ) {
                                        itemsIndexed(
                                            section.items,
                                            key = { index, item ->
                                                "${section.id}_${item.stableId}_$index"
                                            },
                                        ) { _, item ->
                                            if (glass) {
                                                when (sectionIndex % 3) {
                                                    0 -> GlassSquircleCard(
                                                        item = item,
                                                        onClick = { openCatalogItem(item) },
                                                        onPlay = { playCatalogItem(item) },
                                                    )
                                                    1 -> GlassJumpBackInCard(
                                                        item = item,
                                                        onClick = { openCatalogItem(item) },
                                                        onPlay = { playCatalogItem(item) },
                                                    )
                                                    else -> GlassRecentlyPlayedCard(
                                                        item = item,
                                                        onClick = { openCatalogItem(item) },
                                                    )
                                                }
                                            } else {
                                                PlaylistCard(
                                                    item = item,
                                                    onClick = {
                                                        openCatalogItem(item)
                                                    },
                                                )
                                            }
                                        }
                                    }
                                }
                                item(key = "gap:${section.id}") {
                                    Spacer(Modifier.height(20.dp))
                                }
                            }
                        }
                        LoadState.Loading -> {
                            item { HomeSectionShimmer(stringResource(R.string.home_recommendations)) }
                        }
                        is LoadState.Empty -> {
                            item {
                                MessagePanel(
                                    stringResource(R.string.home_library_waiting),
                                    state.message,
                                    stringResource(R.string.home_refresh_action),
                                    onRefresh,
                                )
                            }
                        }
                        else -> Unit
                    }
                }
                BuiltInHomeSection.DOWNLOADED_PLAYLISTS -> {
                    if (!effectiveOffline) return@forEach
                    if (offlinePlaylistItems.isNotEmpty()) {
                        item {
                            val sectionTitle = stringResource(R.string.home_downloaded_playlists)
                            HomeSectionHeader(
                                title = sectionTitle,
                                onSeeAll = {
                                    activeSectionSheet = HomeSectionSheetState(
                                        title = sectionTitle,
                                        initialItems = offlinePlaylistItems,
                                    )
                                },
                            )
                        }
                        item {
                            LazyRow(
                                contentPadding = PaddingValues(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(14.dp),
                            ) {
                                itemsIndexed(
                                    offlinePlaylistItems,
                                    key = { index, item -> "off_pl_${item.stableId}_$index" },
                                ) { _, item ->
                                    if (glass) {
                                        GlassJumpBackInCard(
                                            item = item,
                                            onClick = { openCatalogItem(item) },
                                            onPlay = { playCatalogItem(item) },
                                        )
                                    } else {
                                        PlaylistCard(
                                            item = item,
                                            onClick = { openCatalogItem(item) },
                                        )
                                    }
                                }
                            }
                        }
                        item { Spacer(Modifier.height(20.dp)) }
                    }
                }
                BuiltInHomeSection.DOWNLOADED_ARTISTS -> {
                    if (!effectiveOffline) return@forEach
                    if (offlineArtistItems.isNotEmpty()) {
                        item {
                            val sectionTitle = stringResource(R.string.home_downloaded_artists)
                            HomeSectionHeader(
                                title = sectionTitle,
                                onSeeAll = {
                                    activeSectionSheet = HomeSectionSheetState(
                                        title = sectionTitle,
                                        initialItems = offlineArtistItems,
                                    )
                                },
                            )
                        }
                        item {
                            LazyRow(
                                contentPadding = PaddingValues(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                            ) {
                                itemsIndexed(
                                    offlineArtistItems,
                                    key = { index, item -> "off_art_${item.stableId}_$index" },
                                ) { _, item ->
                                    CircularArtistCard(
                                        item = item,
                                        isActiveArtist = !activeArtistName.isNullOrBlank() &&
                                            activeArtistName.contains(item.title, ignoreCase = true),
                                        isPlaying = isPlaying,
                                        onClick = { openCatalogItem(item) },
                                    )
                                }
                            }
                        }
                        item { Spacer(Modifier.height(20.dp)) }
                    }
                }
                BuiltInHomeSection.DOWNLOADED_ALBUMS -> {
                    if (!effectiveOffline) return@forEach
                    if (offlineAlbumItems.isNotEmpty()) {
                        item {
                            val sectionTitle = stringResource(R.string.home_downloaded_albums)
                            HomeSectionHeader(
                                title = sectionTitle,
                                onSeeAll = {
                                    activeSectionSheet = HomeSectionSheetState(
                                        title = sectionTitle,
                                        initialItems = offlineAlbumItems,
                                    )
                                },
                            )
                        }
                        item {
                            LazyRow(
                                contentPadding = PaddingValues(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(14.dp),
                            ) {
                                itemsIndexed(
                                    offlineAlbumItems,
                                    key = { index, item -> "off_alb_${item.stableId}_$index" },
                                ) { _, item ->
                                    if (glass) {
                                        GlassRecentlyPlayedCard(
                                            item = item,
                                            onClick = { openCatalogItem(item) },
                                        )
                                    } else {
                                        PlaylistCard(
                                            item = item,
                                            onClick = { openCatalogItem(item) },
                                        )
                                    }
                                }
                            }
                        }
                        item { Spacer(Modifier.height(20.dp)) }
                    }
                }
                BuiltInHomeSection.DOWNLOADED_SONGS -> {
                    if (!effectiveOffline) return@forEach
                    if (downloadedTracks.isNotEmpty()) {
                        item {
                            val sectionTitle = stringResource(R.string.home_downloaded_songs)
                            HomeSectionHeader(
                                title = sectionTitle,
                                onSeeAll = {
                                    activeSectionSheet = HomeSectionSheetState(
                                        title = sectionTitle,
                                        initialItems = downloadedTracks.map { CatalogItem.Song(it) },
                                    )
                                },
                            )
                        }
                        itemsIndexed(
                            downloadedTracks,
                            key = { index, track -> "off_trk_${track.id}_$index" },
                        ) { index, track ->
                            val liked = library.likedTracks.any { it.id == track.id }
                            RankedSongRow(
                                rank = index + 1,
                                track = track,
                                liked = liked,
                                isActive = !activeTrackId.isNullOrBlank() && track.id == activeTrackId,
                                isPlaying = isPlaying,
                                onPlay = { onPlay(track) },
                                onToggleLike = { onToggleLike(track) },
                                onPlayNext = onPlayNext,
                                onAddToQueue = onAddToQueue,
                                onAddToPlaylist = onAddToPlaylist,
                                onShare = onShare,
                            )
                        }
                        item { Spacer(Modifier.height(20.dp)) }
                    } else {
                        item {
                            MessagePanel(
                                title = stringResource(R.string.home_no_downloaded_music),
                                message = stringResource(R.string.home_no_downloaded_music_desc),
                                actionLabel = stringResource(R.string.home_try_reconnecting),
                                onAction = onRefresh,
                            )
                        }
                    }
                }
            }
        }

        item {
            Box(
                Modifier.fillMaxWidth().padding(vertical = 24.dp),
                contentAlignment = Alignment.Center,
            ) {
                OutlinedButton(onClick = onEditLayout) {
                    Text(stringResource(R.string.home_edit_layout), color = CanopyColors.Text)
                }
            }
        }
    }
    }
}

@Composable
private fun HomeStickyCompactBar(
    auth: AuthState,
    isPlaying: Boolean,
    onSearch: () -> Unit,
    onProfile: () -> Unit,
) {
    val isCyberpunk = LocalCyberpunkTheme.current
    val haptics = LocalHapticFeedback.current
    val avatarUrl = (auth as? AuthState.SignedIn)?.avatarUrl.orEmpty()
    val firstName = (auth as? AuthState.SignedIn)?.displayName?.trim()?.split(" ")?.firstOrNull().orEmpty()
    val barShape = if (isCyberpunk) CyberCutCompactShape else CircleShape
    val barBg = if (isCyberpunk) {
        Brush.verticalGradient(listOf(Color(0xF0070A12), Color(0xEC090E1A)))
    } else {
        Brush.verticalGradient(listOf(Color(0xE0130F28), Color(0xEC0B091A)))
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(barShape)
            .background(barBg)
            .border(
                width = if (isCyberpunk) 1.5.dp else 1.dp,
                color = if (isCyberpunk) VeyraCyber.cyan.copy(alpha = 0.72f) else Color.White.copy(alpha = 0.24f),
                shape = barShape,
            )
            .padding(horizontal = 14.dp, vertical = 8.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                VeyraMark(modifier = Modifier.size(24.dp))
                Text(
                    text = if (isCyberpunk) "[VEYRA // HOME]" else "Veyra Music",
                    style = if (isCyberpunk) {
                        TextStyle(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    } else {
                        MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    },
                    color = if (isCyberpunk) VeyraCyber.cyan else Color.White,
                )
                if (isPlaying) {
                    VeyraLiveEqualizerBars(
                        playing = true,
                        modifier = Modifier.size(width = 13.dp, height = 11.dp),
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(barShape)
                        .background(if (isCyberpunk) VeyraCyber.surface else Color.White.copy(alpha = 0.10f))
                        .clickable {
                            haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            onSearch()
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    VeyraGlyphIcon(
                        kind = VeyraIconKind.SEARCH,
                        tint = if (isCyberpunk) VeyraCyber.cyan else Color.White,
                        contentDescription = stringResource(R.string.nav_search),
                        modifier = Modifier.size(17.dp),
                    )
                }
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(barShape)
                        .background(if (isCyberpunk) VeyraCyber.surface else Color.White.copy(alpha = 0.10f))
                        .clickable {
                            haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            onProfile()
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    if (avatarUrl.isNotBlank()) {
                        ArtworkTile(
                            url = avatarUrl,
                            description = firstName,
                            modifier = Modifier.fillMaxSize(),
                            radius = if (isCyberpunk) 2 else 999,
                        )
                    } else {
                        VeyraGlyphIcon(
                            kind = VeyraIconKind.SETTINGS,
                            tint = if (isCyberpunk) VeyraCyber.acidYellow else LocalAccent.current,
                            contentDescription = stringResource(R.string.nav_profile),
                            modifier = Modifier.size(17.dp),
                        )
                    }
                }
            }
        }
    }
}

/**
 * Cabecera editorial con identidad dual: Liquid Aurora fluida vs HUD modular Cyberpunk.
 */
@Composable
private fun HomeHeader(
    auth: AuthState,
    onSearch: () -> Unit,
    onProfile: () -> Unit = {},
    onLibrary: (LibraryFilter) -> Unit = {},
) {
    val hour = remember { Calendar.getInstance().get(Calendar.HOUR_OF_DAY) }
    val greeting = when {
        hour < 12 -> stringResource(R.string.home_greeting_morning)
        hour < 17 -> stringResource(R.string.home_greeting_afternoon)
        else -> stringResource(R.string.home_greeting_evening)
    }
    val listenerFallback = stringResource(R.string.home_listener)
    val firstName = remember(auth, listenerFallback) {
        (auth as? AuthState.SignedIn)?.displayName?.trim()?.split(" ")?.firstOrNull()?.ifBlank { listenerFallback }
            ?: listenerFallback
    }
    val avatarUrl = (auth as? AuthState.SignedIn)?.avatarUrl.orEmpty()
    val haptics = LocalHapticFeedback.current
    val isCyberpunk = LocalCyberpunkTheme.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                VeyraMark(
                    modifier = Modifier.size(38.dp),
                )
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text(
                            text = "Veyra Music",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = (-0.3).sp,
                            ),
                            color = CanopyColors.Text,
                        )
                        StatusPillBadge(
                            label = if (isCyberpunk) "CYBERPUNK // HUD" else "LIQUID AURORA",
                            status = if (isCyberpunk) StatusPillTone.ACTIVE else StatusPillTone.SYNC,
                            pulse = true,
                            size = StatusPillSize.SM,
                        )
                    }
                }
            }

            val headerBtnShape = if (isCyberpunk) CyberCutCompactShape else CircleShape
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Surface(
                    onClick = {
                        haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onSearch()
                    },
                    color = if (isCyberpunk) VeyraCyber.surface else Color.White.copy(alpha = 0.11f),
                    shape = headerBtnShape,
                    border = BorderStroke(
                        if (isCyberpunk) 1.5.dp else 1.dp,
                        if (isCyberpunk) VeyraCyber.cyan.copy(alpha = 0.72f) else Color.White.copy(alpha = 0.26f),
                    ),
                    modifier = Modifier.size(48.dp),
                ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                        VeyraGlyphIcon(
                            kind = VeyraIconKind.SEARCH,
                            tint = if (isCyberpunk) VeyraCyber.cyan else Color.White,
                            contentDescription = stringResource(R.string.nav_search),
                            modifier = Modifier.size(20.dp),
                        )
                    }
                }

                Surface(
                    onClick = {
                        haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onProfile()
                    },
                    color = if (isCyberpunk) VeyraCyber.surface else Color.White.copy(alpha = 0.11f),
                    shape = headerBtnShape,
                    border = BorderStroke(
                        if (isCyberpunk) 1.5.dp else 1.dp,
                        if (isCyberpunk) VeyraCyber.acidYellow else Color.White.copy(alpha = 0.26f),
                    ),
                    modifier = Modifier.size(48.dp),
                ) {
                    if (avatarUrl.isNotBlank()) {
                        ArtworkTile(
                            url = avatarUrl,
                            description = firstName,
                            modifier = Modifier.fillMaxSize(),
                            radius = if (isCyberpunk) 4 else 999,
                        )
                    } else {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                            VeyraGlyphIcon(
                                kind = VeyraIconKind.SETTINGS,
                                tint = if (isCyberpunk) VeyraCyber.acidYellow else LocalAccent.current,
                                contentDescription = stringResource(R.string.nav_profile),
                                modifier = Modifier.size(21.dp),
                            )
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(14.dp))

        Text(
            text = stringResource(R.string.home_greeting_user, greeting, firstName),
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.4).sp,
            ),
            color = CanopyColors.Text,
        )

        Spacer(Modifier.height(12.dp))

        if (isCyberpunk) {
            // Cyberpunk HUD search bar with bevel cut corners and tactical GO badge
            Surface(
                onClick = {
                    haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onSearch()
                },
                shape = CyberCutHeroShape,
                color = Color(0xFF090E1A),
                border = BorderStroke(1.5.dp, VeyraCyber.cyan.copy(alpha = 0.60f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .cyberCornerBrackets(VeyraCyber.cyan, bracketLength = 8.dp, strokeWidth = 1.5.dp),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        VeyraGlyphIcon(
                            kind = VeyraIconKind.SEARCH,
                            tint = VeyraCyber.cyan,
                            modifier = Modifier.size(18.dp),
                        )
                        Spacer(Modifier.width(10.dp))
                        Text(
                            text = stringResource(R.string.nav_search),
                            style = MaterialTheme.typography.bodyMedium,
                            color = VeyraCyber.textMuted,
                        )
                    }
                    Box(
                        modifier = Modifier
                            .background(VeyraCyber.acidYellow, CyberCutCompactShape)
                            .padding(horizontal = 8.dp, vertical = 3.dp),
                    ) {
                        Text(
                            text = "CMD",
                            style = TextStyle(
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 10.sp,
                            ),
                            color = VeyraCyber.ink,
                        )
                    }
                }
            }
        } else {
            // Aurora sliding liquid-bubble quick-filter rail
            val quickFilters = listOf(
                null to stringResource(R.string.action_see_all),
                LibraryFilter.PLAYLISTS to stringResource(R.string.library_filter_playlists),
                LibraryFilter.ARTISTS to stringResource(R.string.library_filter_artists),
                LibraryFilter.ALBUMS to stringResource(R.string.library_filter_albums),
                LibraryFilter.SONGS to stringResource(R.string.library_filter_songs),
            )
            var selectedIdx by remember { mutableIntStateOf(0) }
            val chipBounds = remember { mutableStateMapOf<Int, Pair<Int, Int>>() }
            val density = LocalDensity.current
            val reduceMotion = LocalReducedMotion.current
            val activeBounds = chipBounds[selectedIdx]
            val targetBubbleX = with(density) { (activeBounds?.first ?: 0).toDp() }
            val targetBubbleW = with(density) { (activeBounds?.second ?: 0).toDp() }
            val bubbleX by animateDpAsState(
                targetValue = targetBubbleX,
                animationSpec = if (reduceMotion) snap() else spring(dampingRatio = 0.78f, stiffness = 420f),
                label = "AuroraFilterX",
            )
            val bubbleW by animateDpAsState(
                targetValue = targetBubbleW,
                animationSpec = if (reduceMotion) snap() else spring(dampingRatio = 0.78f, stiffness = 420f),
                label = "AuroraFilterW",
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
            ) {
                if (bubbleW > 0.dp) {
                    Box(
                        modifier = Modifier
                            .offset(x = bubbleX)
                            .width(bubbleW)
                            .height(36.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.horizontalGradient(
                                    listOf(
                                        Color(0xFF7C3AED).copy(alpha = 0.85f),
                                        Color(0xFF38BDF8).copy(alpha = 0.70f),
                                    ),
                                ),
                            )
                            .border(1.dp, Color.White.copy(alpha = 0.48f), CircleShape),
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    quickFilters.forEachIndexed { idx, (filter, labelText) ->
                        val isSelected = idx == selectedIdx
                        Box(
                            modifier = Modifier
                                .height(36.dp)
                                .onGloballyPositioned { coords ->
                                    chipBounds[idx] = coords.positionInParent().x.toInt() to coords.size.width
                                }
                                .clip(CircleShape)
                                .then(
                                    if (!isSelected) {
                                        Modifier
                                            .background(Color.White.copy(alpha = 0.08f), CircleShape)
                                            .border(1.dp, Color.White.copy(alpha = 0.18f), CircleShape)
                                    } else Modifier
                                )
                                .clickable {
                                    selectedIdx = idx
                                    haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    if (filter != null) onLibrary(filter)
                                }
                                .padding(horizontal = 14.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = labelText,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                                ),
                                color = if (isSelected) Color.White else Color.White.copy(alpha = 0.84f),
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Aurora staggered 2-column editorial grid with full-bleed artwork and glass badges.
 */
@Composable
private fun AuroraEditorialGrid(items: List<QuickGridItem>) {
    val left = items.filterIndexed { i, _ -> i % 2 == 0 }
    val right = items.filterIndexed { i, _ -> i % 2 == 1 }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            left.forEachIndexed { idx, item ->
                AuroraEditorialCard(
                    item = item,
                    height = if (idx % 2 == 0) 178.dp else 142.dp,
                )
            }
        }
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            right.forEachIndexed { idx, item ->
                AuroraEditorialCard(
                    item = item,
                    height = if (idx % 2 == 0) 142.dp else 178.dp,
                )
            }
        }
    }
}

@Composable
private fun AuroraEditorialCard(
    item: QuickGridItem,
    height: androidx.compose.ui.unit.Dp,
) {
    val shape = RoundedCornerShape(22.dp)
    val haptics = LocalHapticFeedback.current
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .tvFocusable(shape = shape) {
                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                item.onClick()
            }
            .clip(shape)
            .liquidAuroraTouchRipple()
            .liquidSpecularSheen()
            .border(
                BorderStroke(
                    1.1.dp,
                    Brush.verticalGradient(
                        listOf(
                            Color.White.copy(alpha = 0.44f),
                            Color.White.copy(alpha = 0.10f),
                            Color(0xFF38BDF8).copy(alpha = 0.30f),
                        ),
                    ),
                ),
                shape,
            ),
    ) {
        if (item.artworkUrl.isNotBlank()) {
            ArtworkTile(
                url = item.artworkUrl,
                description = item.title,
                modifier = Modifier.fillMaxSize(),
                radius = 0,
            )
        } else if (item.gradient != null) {
            Box(Modifier.fillMaxSize().background(item.gradient))
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        0f to Color.Transparent,
                        0.45f to Color.Black.copy(alpha = 0.18f),
                        1f to Color.Black.copy(alpha = 0.86f),
                    ),
                ),
        )
        Row(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = item.title,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                ),
                color = Color.White,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f).padding(end = 8.dp),
            )
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(listOf(Color(0xFF8B5CF6), Color(0xFF38BDF8))),
                    )
                    .border(1.dp, Color.White.copy(alpha = 0.52f), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                VeyraGlyphIcon(
                    kind = VeyraIconKind.PLAY,
                    tint = Color.White,
                    filled = true,
                    modifier = Modifier.size(14.dp),
                )
            }
        }
    }
}

/**
 * Modular 2-column quick access grid con jerarquía visual Cyberpunk de 3 colores.
 */
@Composable
private fun SpotifyQuickGrid(
    items: List<QuickGridItem>,
) {
    val rows = remember(items) { items.chunked(2) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        rows.forEachIndexed { rowIdx, rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                rowItems.forEachIndexed { colIdx, item ->
                    val globalIndex = rowIdx * 2 + colIdx
                    Box(modifier = Modifier.weight(1f)) {
                        QuickGridCard(
                            item = item,
                            index = globalIndex,
                        )
                    }
                }
                if (rowItems.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

/**
 * Tarjeta rápida modular: solo la protagonista destaca con corchetes cian;
 * el resto usa borde acero limpio + índice monoespaciado para evitar saturación visual.
 */
@Composable
private fun QuickGridCard(
    item: QuickGridItem,
    index: Int = 0,
) {
    val isCyberpunk = LocalCyberpunkTheme.current
    val isFeatured = isCyberpunk && index == 0
    val shape = if (isCyberpunk) CyberCutCompactShape else VeyraRadius.lgShape
    val cardBrush = if (isCyberpunk) {
        Brush.linearGradient(
            listOf(
                if (isFeatured) Color(0xFF10192D) else Color(0xFF0A0F1D),
                Color(0xFF070A12),
            ),
        )
    } else {
        Brush.verticalGradient(colors = listOf(Color.White.copy(alpha = 0.14f), Color.White.copy(alpha = 0.06f)))
    }
    val haptics = LocalHapticFeedback.current
    Surface(
        shape = shape,
        color = Color.Transparent,
        border = BorderStroke(
            if (isFeatured) 1.5.dp else 1.dp,
            when {
                isFeatured -> VeyraCyber.acidYellow.copy(alpha = 0.85f)
                isCyberpunk -> Color.White.copy(alpha = 0.16f)
                else -> Color.White.copy(alpha = 0.24f)
            },
        ),
        shadowElevation = 0.dp,
        modifier = Modifier
            .fillMaxWidth()
            .height(58.dp)
            .tvFocusable(shape = shape) {
                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                item.onClick()
            }
            .clip(shape)
            .background(cardBrush)
            .then(
                if (isFeatured) Modifier.cyberCornerBrackets(VeyraCyber.cyan, bracketLength = 7.dp, strokeWidth = 1.5.dp)
                else Modifier
            ),
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(58.dp)
                    .clip(if (isCyberpunk) CyberCutCompactShape else RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp))
                    .then(
                        if (item.gradient != null) Modifier.background(item.gradient)
                        else Modifier
                    )
            ) {
                if (item.artworkUrl.isNotBlank()) {
                    ArtworkTile(item.artworkUrl, item.title, Modifier.fillMaxSize(), 0)
                } else if (item.icon != null) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                        Icon(
                            item.icon,
                            contentDescription = item.title,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp),
                        )
                    }
                }
            }
            Spacer(Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                if (isCyberpunk) {
                    Text(
                        text = "0${index + 1}",
                        style = TextStyle(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                        ),
                        color = if (isFeatured) VeyraCyber.acidYellow else VeyraCyber.cyan.copy(alpha = 0.78f),
                    )
                }
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.5.sp,
                        lineHeight = 15.sp,
                    ),
                    color = CanopyColors.Text,
                    maxLines = if (isCyberpunk) 1 else 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}


/**
 * Featured Mix Hero Carousel con personalidad diferenciada Liquid Aurora vs Cyberpunk HUD.
 */
@Composable
private fun FeaturedHeroCarousel(
    items: List<CatalogItem>,
    onPlay: (CatalogItem) -> Unit,
    onClick: (CatalogItem) -> Unit,
) {
    if (items.isEmpty()) return
    val pageCount = items.size.coerceAtMost(5)
    val pagerState = rememberPagerState(pageCount = { pageCount })
    val isCyberpunk = LocalCyberpunkTheme.current
    val shape = if (isCyberpunk) CyberCutHeroShape else RoundedCornerShape(28.dp)
    var playBurst by remember { mutableIntStateOf(0) }

    Column(modifier = Modifier.fillMaxWidth()) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(if (isCyberpunk) 258.dp else 272.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
            pageSpacing = 12.dp,
        ) { page ->
            val item = items[page]
            val subtitle = catalogSubtitle(item).ifBlank { stringResource(R.string.home_curated_mix_desc) }
            val pageBias = if (pagerState.currentPage == page) pagerState.currentPageOffsetFraction else 0f

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .tvFocusable(shape = shape) { onClick(item) }
                    .clip(shape)
                    .then(
                        if (isCyberpunk) {
                            Modifier
                                .cyberScanBeam(pagerState.currentPage, VeyraCyber.cyan)
                                .cyberCornerBrackets(VeyraCyber.cyan, bracketLength = 16.dp, strokeWidth = 2.dp)
                                .border(BorderStroke(1.dp, Color.White.copy(alpha = 0.16f)), shape)
                        } else {
                            Modifier
                                .liquidSpecularSheen(scrollBias = pageBias)
                                .border(BorderStroke(1.1.dp, Color.White.copy(alpha = 0.28f)), shape)
                        }
                    )
            ) {
                // Background Artwork
                ArtworkTile(
                    url = item.artworkUrl,
                    description = item.title,
                    modifier = Modifier.fillMaxSize(),
                    radius = 0,
                )

                // Dark gradient scrim
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                0f to Color.Black.copy(alpha = 0.24f),
                                0.38f to Color.Black.copy(alpha = 0.42f),
                                1f to Color.Black.copy(alpha = if (isCyberpunk) 0.92f else 0.88f),
                            )
                        )
                )

                // Hero Content
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.SpaceBetween,
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        StatusPillBadge(
                            label = catalogItemBadge(item).uppercase(),
                            status = if (isCyberpunk) StatusPillTone.ACTIVE else StatusPillTone.SYNC,
                            size = StatusPillSize.SM,
                        )
                        if (isCyberpunk) {
                            CyberScrambleText(
                                text = "[0${page + 1} // SPOTLIGHT]",
                                style = TextStyle(
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 10.sp,
                                    letterSpacing = 0.6.sp,
                                ),
                                fontWeight = FontWeight.Bold,
                                color = VeyraCyber.cyan,
                            )
                        }
                    }

                    // Bottom info and play button
                    Column {
                        Text(
                            text = item.title,
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontSize = if (isCyberpunk) 24.sp else 26.sp,
                                fontWeight = FontWeight.Bold,
                            ),
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Spacer(Modifier.height(3.dp))
                        Text(
                            text = subtitle,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontStyle = if (isCyberpunk) FontStyle.Normal else FontStyle.Italic,
                            ),
                            color = Color.White.copy(alpha = 0.82f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Spacer(Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            val heroHaptics = LocalHapticFeedback.current
                            val heroPlayShape = if (isCyberpunk) CyberCutCompactShape else CircleShape
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = if (isCyberpunk) Modifier.cyberGlitchBurst(playBurst) else Modifier,
                            ) {
                                if (isCyberpunk) {
                                    Box(
                                        modifier = Modifier
                                            .size(48.dp)
                                            .offset(4.dp, 4.dp)
                                            .background(VeyraCyber.cyan, heroPlayShape),
                                    )
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .size(58.dp)
                                            .background(
                                                Brush.radialGradient(
                                                    listOf(
                                                        Color(0xFF8B5CF6).copy(alpha = 0.45f),
                                                        Color(0xFF38BDF8).copy(alpha = 0.20f),
                                                        Color.Transparent,
                                                    ),
                                                ),
                                                CircleShape,
                                            ),
                                    )
                                }
                                Surface(
                                    onClick = {
                                        if (isCyberpunk) playBurst++
                                        heroHaptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                        onPlay(item)
                                    },
                                    shape = heroPlayShape,
                                    color = Color.Transparent,
                                    border = BorderStroke(
                                        if (isCyberpunk) VeyraCyber.borderWidth else 1.2.dp,
                                        if (isCyberpunk) VeyraCyber.ink else Color.White.copy(alpha = 0.58f),
                                    ),
                                    shadowElevation = 0.dp,
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(heroPlayShape)
                                        .background(
                                            if (isCyberpunk) {
                                                Brush.verticalGradient(listOf(VeyraCyber.acidYellow, VeyraCyber.acidYellow))
                                            } else {
                                                Brush.linearGradient(listOf(Color(0xFF8B5CF6), Color(0xFF38BDF8)))
                                            },
                                        ),
                                ) {
                                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                                        VeyraGlyphIcon(
                                            kind = VeyraIconKind.PLAY,
                                            tint = if (isCyberpunk) VeyraCyber.ink else Color.White,
                                            filled = true,
                                            contentDescription = stringResource(R.string.action_play),
                                            modifier = Modifier.size(24.dp),
                                        )
                                    }
                                }
                            }

                            // Center Pager Dots
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                modifier = Modifier.padding(end = 8.dp),
                            ) {
                                repeat(pageCount) { index ->
                                    val isSelected = pagerState.currentPage == index
                                    val dotShape = if (isCyberpunk) CyberCutCompactShape else CircleShape
                                    Box(
                                        modifier = Modifier
                                            .height(5.dp)
                                            .width(if (isSelected) 16.dp else 5.dp)
                                            .clip(dotShape)
                                            .background(
                                                when {
                                                    isCyberpunk && isSelected -> VeyraCyber.acidYellow
                                                    isSelected -> Color.White
                                                    else -> Color.White.copy(alpha = 0.35f)
                                                },
                                            )
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

/**
 * Artwork card for "Made for you" section with bottom-right floating play button.
 */
@Composable
private fun GlassSquircleCard(
    item: CatalogItem,
    onClick: () -> Unit,
    onPlay: () -> Unit,
) {
    val isCyberpunk = LocalCyberpunkTheme.current
    val shape = if (isCyberpunk) VeyraCyber.shape else VeyraRadius.mdShape
    Box(
        modifier = Modifier
            .width(140.dp)
            .aspectRatio(1f)
            .tvFocusable(shape = shape, onClick = onClick)
            .clip(shape)
            .border(
                BorderStroke(
                    if (isCyberpunk) VeyraCyber.borderWidth else 1.dp,
                    if (isCyberpunk) VeyraCyber.cyan.copy(alpha = 0.70f) else CanopyColors.Rule,
                ),
                shape,
            )
    ) {
        ArtworkTile(item.artworkUrl, item.title, Modifier.fillMaxSize(), 0)

        // Gradient scrim at bottom
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        0f to Color.Transparent,
                        0.40f to Color.Black.copy(alpha = 0.30f),
                        1f to Color.Black.copy(alpha = 0.85f),
                    )
                )
        )

        Row(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = item.title,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    lineHeight = 16.sp,
                ),
                color = Color.White,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f).padding(end = 6.dp),
            )

            // Mini circular / cyber play button
            Surface(
                onClick = onPlay,
                shape = if (isCyberpunk) CyberCutCompactShape else CircleShape,
                color = if (isCyberpunk) VeyraCyber.acidYellow else Color.White.copy(alpha = 0.94f),
                border = BorderStroke(
                    if (isCyberpunk) 1.5.dp else 1.dp,
                    if (isCyberpunk) VeyraCyber.cyan else Color.White.copy(alpha = 0.45f),
                ),
                shadowElevation = 0.dp,
                modifier = Modifier.size(32.dp),
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    VeyraGlyphIcon(
                        kind = VeyraIconKind.PLAY,
                        tint = if (isCyberpunk) VeyraCyber.ink else Color(0xFF101318),
                        filled = true,
                        contentDescription = stringResource(R.string.home_play_item_cd, item.title),
                        modifier = Modifier.size(16.dp),
                    )
                }
            }
        }
    }
}

/**
 * Compact card for "Jump back in" section con jerarquía limpia.
 */
@Composable
private fun GlassJumpBackInCard(
    item: CatalogItem,
    onClick: () -> Unit,
    onPlay: () -> Unit,
) {
    val isCyberpunk = LocalCyberpunkTheme.current
    val shape = if (isCyberpunk) CyberCutCompactShape else VeyraRadius.mdShape
    Surface(
        shape = shape,
        color = if (isCyberpunk) Color(0xFF090E1A) else Color.White.copy(alpha = 0.10f),
        border = BorderStroke(
            1.dp,
            if (isCyberpunk) Color.White.copy(alpha = 0.16f) else Color.White.copy(alpha = 0.24f),
        ),
        modifier = Modifier
            .width(220.dp)
            .height(68.dp)
            .tvFocusable(shape = shape, onClick = onClick)
            .then(if (!isCyberpunk) Modifier.liquidAuroraTouchRipple() else Modifier),
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(if (isCyberpunk) CyberCutCompactShape else RoundedCornerShape(10.dp))
                    .border(
                        BorderStroke(1.dp, if (isCyberpunk) VeyraCyber.cyan.copy(alpha = 0.35f) else CanopyColors.Rule),
                        if (isCyberpunk) CyberCutCompactShape else RoundedCornerShape(10.dp),
                    ),
            ) {
                ArtworkTile(item.artworkUrl, item.title, Modifier.fillMaxSize(), if (isCyberpunk) 2 else 10)
            }
            Spacer(Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                    ),
                    color = CanopyColors.Text,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = catalogSubtitle(item),
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                    color = CanopyColors.Muted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Spacer(Modifier.width(4.dp))
            Surface(
                onClick = onPlay,
                shape = if (isCyberpunk) CyberCutCompactShape else CircleShape,
                color = if (isCyberpunk) VeyraCyber.acidYellow else Color.White.copy(alpha = 0.94f),
                border = BorderStroke(
                    if (isCyberpunk) 1.5.dp else 1.dp,
                    if (isCyberpunk) VeyraCyber.cyan else Color.White.copy(alpha = 0.45f),
                ),
                shadowElevation = 0.dp,
                modifier = Modifier.size(32.dp),
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    VeyraGlyphIcon(
                        kind = VeyraIconKind.PLAY,
                        tint = if (isCyberpunk) VeyraCyber.ink else Color(0xFF101318),
                        filled = true,
                        contentDescription = stringResource(R.string.action_play),
                        modifier = Modifier.size(16.dp),
                    )
                }
            }
        }
    }
}

/**
 * Vertical rounded card for "Recently played" section.
 */
@Composable
private fun GlassRecentlyPlayedCard(
    item: CatalogItem,
    onClick: () -> Unit,
) {
    val isCyberpunk = LocalCyberpunkTheme.current
    val shape = if (isCyberpunk) CyberCutCompactShape else RoundedCornerShape(14.dp)
    Column(
        modifier = Modifier
            .width(120.dp)
            .tvFocusable(shape = shape, onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(shape)
                .then(if (!isCyberpunk) Modifier.liquidSpecularSheen() else Modifier)
                .border(
                    BorderStroke(
                        1.dp,
                        if (isCyberpunk) Color.White.copy(alpha = 0.16f) else CanopyColors.Rule,
                    ),
                    shape,
                )
        ) {
            ArtworkTile(item.artworkUrl, item.title, Modifier.fillMaxSize(), if (isCyberpunk) 2 else 14)
        }
        Spacer(Modifier.height(8.dp))
        Text(
            text = item.title,
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.5.sp,
            ),
            color = CanopyColors.Text,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            text = catalogSubtitle(item),
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
            color = CanopyColors.Muted,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

/**
 * Section Header con identidad Liquid Aurora vs HUD modular Cyberpunk.
 */
@Composable
private fun HomeSectionHeader(
    title: String,
    subtitle: String? = null,
    itemCount: Int? = null,
    onSeeAll: (() -> Unit)? = null,
) {
    val isCyberpunk = LocalCyberpunkTheme.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f, fill = false)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    if (isCyberpunk) {
                        Box(
                            modifier = Modifier
                                .width(4.dp)
                                .height(18.dp)
                                .background(VeyraCyber.acidYellow, CyberCutCompactShape),
                        )
                    }
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            letterSpacing = if (isCyberpunk) 0.4.sp else (-0.3).sp,
                        ),
                        color = CanopyColors.Text,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    if (itemCount != null && itemCount > 0) {
                        StatusPillBadge(
                            label = "$itemCount",
                            status = if (isCyberpunk) StatusPillTone.SYNC else StatusPillTone.MUTED,
                            size = StatusPillSize.SM,
                            dot = false,
                        )
                    }
                }
                if (!subtitle.isNullOrBlank()) {
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                        color = CanopyColors.Muted,
                    )
                }
            }
            if (onSeeAll != null) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(if (isCyberpunk) CyberCutCompactShape else CircleShape)
                        .then(
                            if (isCyberpunk) {
                                Modifier
                                    .background(Color(0xFF090E1A), CyberCutCompactShape)
                                    .border(1.dp, VeyraCyber.cyan.copy(alpha = 0.65f), CyberCutCompactShape)
                            } else Modifier
                        )
                        .clickable(onClick = onSeeAll)
                        .padding(horizontal = 10.dp, vertical = 5.dp),
                ) {
                    Text(
                        text = stringResource(R.string.action_see_all),
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp,
                        ),
                        color = if (isCyberpunk) VeyraCyber.cyan else CanopyColors.Muted,
                    )
                    Spacer(Modifier.width(2.dp))
                    Icon(
                        Icons.AutoMirrored.Rounded.ArrowForward,
                        contentDescription = null,
                        tint = if (isCyberpunk) VeyraCyber.cyan else CanopyColors.Muted,
                        modifier = Modifier.size(14.dp),
                    )
                }
            }
        }
        if (isCyberpunk) {
            Spacer(Modifier.height(6.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(VeyraCyber.cyan.copy(alpha = 0.24f)),
            )
        }
    }
}

@Composable
private fun PlaylistCard(item: CatalogItem, onClick: () -> Unit) {
    val isCyberpunk = LocalCyberpunkTheme.current
    val shape = if (isCyberpunk) CyberCutCompactShape else VeyraRadius.mdShape
    Column(
        modifier = Modifier
            .width(140.dp)
            .tvFocusable(shape = shape, onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(shape)
                .then(
                    if (!isCyberpunk) Modifier.liquidSpecularSheen()
                    else Modifier
                )
                .border(
                    BorderStroke(
                        1.dp,
                        if (isCyberpunk) Color.White.copy(alpha = 0.16f) else CanopyColors.Rule,
                    ),
                    shape,
                ),
        ) {
            ArtworkTile(item.artworkUrl, item.title, Modifier.fillMaxSize(), if (isCyberpunk) 4 else 14)
        }
        Spacer(Modifier.height(8.dp))
        Text(
            text = item.title,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = CanopyColors.Text,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            text = catalogSubtitle(item),
            style = MaterialTheme.typography.bodySmall,
            color = CanopyColors.Muted,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

/**
 * Orbe circular Aurora para artistas con anillo orbital vivo vs marco HUD Cyberpunk.
 */
@Composable
private fun CircularArtistCard(
    item: CatalogItem,
    isActiveArtist: Boolean = false,
    isPlaying: Boolean = false,
    onClick: () -> Unit,
) {
    val isCyberpunk = LocalCyberpunkTheme.current
    val reducedMotion = LocalReducedMotion.current
    val avatarShape = if (isCyberpunk) CyberCutHeroShape else CircleShape
    val ringRotation = if (!isCyberpunk && isActiveArtist && isPlaying && !reducedMotion) {
        val infinite = rememberInfiniteTransition(label = "ArtistOrbitalRing")
        val deg by infinite.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(tween(8_000, easing = LinearEasing)),
            label = "ArtistRingDeg",
        )
        deg
    } else 0f

    Column(
        modifier = Modifier
            .width(88.dp)
            .tvFocusable(shape = avatarShape, onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier.size(82.dp),
            contentAlignment = Alignment.Center,
        ) {
            if (!isCyberpunk) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer { rotationZ = ringRotation }
                        .border(
                            width = if (isActiveArtist) 2.2.dp else 1.2.dp,
                            brush = Brush.sweepGradient(
                                if (isActiveArtist) {
                                    listOf(
                                        Color(0xFF8B5CF6),
                                        Color(0xFFEC4899),
                                        Color(0xFF38BDF8),
                                        Color(0xFF8B5CF6),
                                    )
                                } else {
                                    listOf(
                                        Color.White.copy(alpha = 0.34f),
                                        Color(0xFF38BDF8).copy(alpha = 0.28f),
                                        Color.White.copy(alpha = 0.14f),
                                        Color.White.copy(alpha = 0.34f),
                                    )
                                },
                            ),
                            shape = CircleShape,
                        ),
                )
            }
            Box(
                modifier = Modifier
                    .size(if (isCyberpunk) 80.dp else 74.dp)
                    .clip(avatarShape)
                    .then(
                        if (isCyberpunk) {
                            Modifier
                                .border(
                                    1.dp,
                                    if (isActiveArtist) VeyraCyber.acidYellow else Color.White.copy(alpha = 0.18f),
                                    avatarShape,
                                )
                                .cyberCornerBrackets(
                                    color = if (isActiveArtist) VeyraCyber.acidYellow else VeyraCyber.cyan,
                                    bracketLength = 8.dp,
                                    strokeWidth = 1.5.dp,
                                )
                        } else Modifier
                    ),
            ) {
                ArtworkTile(item.artworkUrl, item.title, Modifier.fillMaxSize(), if (isCyberpunk) 2 else 999)
                if (isActiveArtist && isPlaying) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.42f)),
                        contentAlignment = Alignment.Center,
                    ) {
                        VeyraLiveEqualizerBars(
                            playing = true,
                            modifier = Modifier.size(width = 16.dp, height = 14.dp),
                        )
                    }
                }
            }
        }
        Spacer(Modifier.height(8.dp))
        Text(
            text = item.title,
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
            ),
            color = if (isActiveArtist && isCyberpunk) VeyraCyber.acidYellow else CanopyColors.Text,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
        Text(
            text = catalogSubtitle(item).ifBlank { stringResource(R.string.home_artist_label) },
            style = MaterialTheme.typography.bodySmall.copy(
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Medium,
            ),
            color = CanopyColors.Muted,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

/**
 * Fila de canción con onda táctil Aurora / corchetes Cyberpunk e indicador vivo.
 */
@Composable
private fun RankedSongRow(
    rank: Int? = null,
    track: Track,
    liked: Boolean,
    isActive: Boolean = false,
    isPlaying: Boolean = false,
    onPlay: () -> Unit,
    onToggleLike: () -> Unit,
    onPlayNext: ((Track) -> Unit)?,
    onAddToQueue: ((Track) -> Unit)?,
    onAddToPlaylist: ((Track) -> Unit)?,
    onShare: ((Track) -> Unit)?,
) {
    var popupOpen by remember { mutableStateOf(false) }
    val isCyberpunk = LocalCyberpunkTheme.current

    if (popupOpen) {
        dev.sfg.veyra.mobile.ui.components.TrackActionsPopup(
            track = track,
            onDismiss = { popupOpen = false },
            onPlay = onPlay,
            onPlayNext = onPlayNext?.let { { it(track) } },
            onAddToQueue = onAddToQueue?.let { { it(track) } },
            onAddToPlaylist = onAddToPlaylist?.let { { it(track) } },
            onShare = onShare?.let { { it(track) } },
        )
    }

    val rowShape = if (isCyberpunk) CyberCutCompactShape else RoundedCornerShape(14.dp)
    Surface(
        color = when {
            isCyberpunk && isActive -> Color(0xFF0D1526)
            isCyberpunk -> Color.Transparent
            isActive -> Color.White.copy(alpha = 0.08f)
            else -> Color.Transparent
        },
        border = when {
            isCyberpunk && isActive -> BorderStroke(1.dp, VeyraCyber.acidYellow.copy(alpha = 0.72f))
            !isCyberpunk && isActive -> BorderStroke(1.dp, LocalAccent.current.copy(alpha = 0.45f))
            else -> null
        },
        shape = rowShape,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 2.dp)
            .clip(rowShape)
            .then(
                if (!isCyberpunk) Modifier.liquidAuroraTouchRipple()
                else if (isActive) Modifier.cyberCornerBrackets(VeyraCyber.cyan, bracketLength = 7.dp, strokeWidth = 1.2.dp)
                else Modifier
            )
            .tvFocusable(shape = rowShape, onClick = onPlay),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = if (isActive) 8.dp else 0.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (rank != null) {
                Text(
                    text = if (isCyberpunk) "0$rank" else "$rank",
                    style = if (isCyberpunk) {
                        TextStyle(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    } else {
                        MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    },
                    color = when {
                        isCyberpunk && isActive -> VeyraCyber.acidYellow
                        isCyberpunk -> VeyraCyber.cyan.copy(alpha = 0.80f)
                        isActive -> LocalAccent.current
                        else -> CanopyColors.Muted
                    },
                    modifier = Modifier.width(24.dp),
                )
                Spacer(Modifier.width(6.dp))
            }
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(if (isCyberpunk) CyberCutCompactShape else RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center,
            ) {
                ArtworkTile(track.artworkUrl, track.title, Modifier.fillMaxSize(), if (isCyberpunk) 2 else 12)
                if (isActive) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.48f)),
                        contentAlignment = Alignment.Center,
                    ) {
                        VeyraLiveEqualizerBars(
                            playing = isPlaying,
                            modifier = Modifier.size(width = 14.dp, height = 12.dp),
                        )
                    }
                }
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = track.title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.5.sp,
                        ),
                        color = when {
                            isActive && isCyberpunk -> VeyraCyber.acidYellow
                            isActive -> Color(0xFF38BDF8)
                            else -> CanopyColors.Text
                        },
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false),
                    )
                    if (track.explicit) {
                        Spacer(Modifier.width(6.dp))
                        ExplicitBadge()
                    }
                }
                Spacer(Modifier.height(2.dp))
                Text(
                    text = track.artist.ifBlank { stringResource(R.string.home_unknown_artist) },
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                    color = CanopyColors.Muted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            IconButton(
                onClick = onToggleLike,
                modifier = Modifier.size(36.dp),
            ) {
                VeyraGlyphIcon(
                    kind = if (liked) VeyraIconKind.HEART_FILLED else VeyraIconKind.HEART_OUTLINE,
                    tint = when {
                        liked && isCyberpunk -> VeyraCyber.magenta
                        liked -> CanopyColors.Favorite
                        else -> CanopyColors.Muted
                    },
                    filled = liked,
                    contentDescription = if (liked) stringResource(R.string.home_unlike_cd)
                    else stringResource(R.string.home_like_cd),
                    modifier = Modifier.size(18.dp),
                )
            }
            IconButton(
                onClick = { popupOpen = true },
                modifier = Modifier.size(36.dp),
            ) {
                VeyraGlyphIcon(
                    kind = VeyraIconKind.MORE,
                    tint = CanopyColors.Muted,
                    contentDescription = stringResource(R.string.home_options_cd),
                    modifier = Modifier.size(18.dp),
                )
            }
        }
    }
}

private inline fun <reified T : CatalogItem> extractItemsOfKind(
    state: LoadState<List<CatalogSection>>
): List<T> {
    if (state !is LoadState.Content) return emptyList()
    return state.value.flatMap { it.items }.filterIsInstance<T>()
}

@Composable
private fun catalogItemBadge(item: CatalogItem): String =
    when (item) {
        is CatalogItem.Song -> stringResource(R.string.collect_kind_song)
        is CatalogItem.Record -> stringResource(R.string.collect_kind_album)
        is CatalogItem.Performer -> stringResource(R.string.collect_kind_artist)
        is CatalogItem.Collection -> {
            if (item.playlist.title.contains("mix", ignoreCase = true) ||
                item.playlist.title.contains("radio", ignoreCase = true)
            ) {
                "MIX"
            } else {
                stringResource(R.string.collect_kind_playlist)
            }
        }
        is CatalogItem.Category -> stringResource(R.string.collect_kind_category)
    }

@Composable
private fun catalogSubtitle(item: CatalogItem): String =
    when (item) {
        is CatalogItem.Song -> dev.sfg.veyra.mobile.ui.components.localizedArtistName(item.track.artist)
        is CatalogItem.Record -> item.album.artist
        is CatalogItem.Performer ->
            item.artist.subtitle.ifBlank { stringResource(R.string.home_artist_label) }
        is CatalogItem.Collection ->
            item.playlist.author.ifBlank { stringResource(R.string.home_playlist_label) }
        is CatalogItem.Category -> ""
    }

private fun openItem(item: CatalogItem, play: (Track) -> Unit, detail: (String) -> Unit) {
    when (item) {
        is CatalogItem.Song -> play(item.track)
        else -> detail(item.stableId)
    }
}
