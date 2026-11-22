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

import android.content.Context
import android.os.Build
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import dev.sfg.veyra.mobile.ui.scroll.orchardVerticalScroll as verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.rounded.Login
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.rounded.Devices
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Sync
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import androidx.compose.ui.res.stringResource
import dev.sfg.veyra.connect.BuildConfig
import dev.sfg.veyra.connect.R
import dev.sfg.veyra.mobile.MobileChangelog
import dev.sfg.veyra.mobile.MobileUpdateMetadata
import dev.sfg.veyra.mobile.UpdateState
import dev.sfg.veyra.mobile.auth.AuthState
import dev.sfg.veyra.mobile.discord.DiscordAuthState
import dev.sfg.veyra.mobile.discord.GatewayConnectionState
import dev.sfg.veyra.mobile.i18n.AppLanguage
import dev.sfg.veyra.mobile.i18n.AppLocale
import dev.sfg.veyra.mobile.lastfm.LastfmState
import dev.sfg.veyra.mobile.listenbrainz.ListenBrainzState
import dev.sfg.veyra.mobile.model.AudioQuality
import dev.sfg.veyra.mobile.model.MobilePlayerMode
import dev.sfg.veyra.mobile.model.OrchardSettings
import androidx.compose.animation.core.snap
import dev.sfg.veyra.mobile.ui.components.LocalCyberpunkTheme
import dev.sfg.veyra.mobile.ui.components.OrchardChromeHeight
import dev.sfg.veyra.mobile.ui.components.ReleaseNotesDialog
import dev.sfg.veyra.mobile.ui.components.UpdateDialog
import dev.sfg.veyra.mobile.ui.glass.GlassStyle
import dev.sfg.veyra.mobile.ui.glass.GlassTone
import dev.sfg.veyra.mobile.ui.glass.LocalGlass
import dev.sfg.veyra.mobile.ui.glass.glassPane
import dev.sfg.veyra.mobile.ui.theme.CanopyColors
import dev.sfg.veyra.mobile.ui.theme.LocalAccent
import dev.sfg.veyra.mobile.ui.theme.LocalReducedMotion
import dev.sfg.veyra.mobile.ui.theme.VeyraCyber
import dev.sfg.veyra.mobile.ui.theme.VeyraRadius

@Composable
fun SettingsScreen(
    settings: OrchardSettings,
    auth: AuthState,
    discordAuth: DiscordAuthState = DiscordAuthState.SignedOut,
    discordConnection: GatewayConnectionState = GatewayConnectionState.Disconnected,
    lastfmState: LastfmState = LastfmState.SignedOut,
    listenBrainzState: ListenBrainzState = ListenBrainzState.SignedOut,
    updateState: UpdateState = UpdateState.Idle,
    onSettings: (OrchardSettings) -> Unit,
    onSignIn: () -> Unit,
    onSwitchAccount: () -> Unit,
    onSignOut: () -> Unit,
    onConnectDiscord: () -> Unit = {},
    onDisconnectDiscord: () -> Unit = {},
    onConnectLastfm: () -> Unit = {},
    onCompleteLastfm: () -> Unit = {},
    onDisconnectLastfm: () -> Unit = {},
    onConnectListenBrainz: (String) -> Unit = {},
    onDisconnectListenBrainz: () -> Unit = {},
    onConnectSpotify: () -> Unit = {},
    qobuzStatus: dev.sfg.veyra.mobile.qobuz.QobuzStatus = dev.sfg.veyra.mobile.qobuz.QobuzStatus(),
    onConnectQobuz: () -> Unit = {},
    onDisconnectQobuz: () -> Unit = {},
    onQobuzEnabledChange: (Boolean) -> Unit = {},
    onQobuzQualityChange: (dev.sfg.veyra.mobile.qobuz.QobuzQuality) -> Unit = {},
    onDevices: () -> Unit,
    onWelcome: () -> Unit = {},
    onCheckForUpdates: () -> Unit = {},
    onInstallUpdate: (MobileUpdateMetadata) -> Unit = {},
    onHomeLayout: () -> Unit = {},
    /**
     * Separate from [onSettings] because switching Autoplay off also strips the tracks it added,
     * which only the view model can do.
     */
    onAutoplayEnabled: ((Boolean) -> Unit)? = null,
    cacheSizeBytes: Long = 0L,
    isClearingCache: Boolean = false,
    onClearCache: () -> Unit = {},
    onRefreshCacheSize: () -> Unit = {},
) {
    var showNotesDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        onRefreshCacheSize()
    }

    val isGlass = LocalGlass.current.enabled
    val settingsAccent = lerp(LocalAccent.current, Color.White, 0.35f)
    CompositionLocalProvider(LocalAccent provides settingsAccent) {
        Column(Modifier.fillMaxSize().background(Color.Transparent)) {
            Text(
                stringResource(R.string.settings_title),
                style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                color = CanopyColors.Text,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 20.dp),
            )
            Box(Modifier.fillMaxWidth().height(0.5.dp).background(CanopyColors.Rule))
            Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal = 4.dp)) {
                // Account leads: it is the only row whose state the user cannot infer at a glance.
                Spacer(Modifier.height(20.dp))
                AccountCard(auth, onSignIn, onSwitchAccount, onSignOut)

                SectionLabel(stringResource(R.string.settings_section_language))
                SettingsPanel {
                    LanguageRow()
                }

                SectionLabel(stringResource(R.string.settings_section_audio))
                SettingsPanel {
                    QualityRow(settings.audioQuality) { onSettings(settings.copy(audioQuality = it)) }
                    PanelDivider()
                    EqualizerRow(settings, onSettings)
                    PanelDivider()
                    ToggleRow(
                        title = stringResource(R.string.settings_show_bitrate),
                        subtitle = stringResource(R.string.settings_show_bitrate_subtitle),
                        checked = settings.showBitrate,
                        onChecked = { onSettings(settings.copy(showBitrate = it)) },
                    )
                    PanelDivider()
                    ToggleRow(
                        title = stringResource(R.string.settings_volume_norm),
                        subtitle = stringResource(R.string.settings_volume_norm_subtitle),
                        checked = settings.volumeNormalizationEnabled,
                        onChecked = { onSettings(settings.copy(volumeNormalizationEnabled = it)) },
                    )
                    PanelDivider()
                    ToggleRow(
                        title = stringResource(R.string.settings_autoplay),
                        subtitle = stringResource(R.string.settings_autoplay_subtitle),
                        checked = settings.autoplayEnabled,
                        onChecked = { enabled ->
                            onAutoplayEnabled?.invoke(enabled) ?: onSettings(settings.copy(autoplayEnabled = enabled))
                        },
                    )
                    PanelDivider()
                    ToggleRow(
                        title = stringResource(R.string.settings_auto_download_on_play),
                        subtitle = stringResource(R.string.settings_auto_download_on_play_subtitle),
                        checked = settings.autoDownloadOnPlay,
                        onChecked = { onSettings(settings.copy(autoDownloadOnPlay = it)) },
                    )
                    PanelDivider()
                    CrossfadeRow(settings, onSettings)
                    PanelDivider()
                    CacheSizeRow(settings, onSettings)
                    PanelDivider()
                    ToggleRow(
                        title = "Limitador Anti-Clipping",
                        subtitle = "Evita distorsión digital al subir preamp o bajos en el EQ",
                        checked = settings.equalizerConfig.limiterEnabled,
                        onChecked = {
                            onSettings(settings.copy(equalizerConfig = settings.equalizerConfig.copy(limiterEnabled = it)))
                        },
                    )
                    PanelDivider()
                    ToggleRow(
                        title = "Crossfeed Binaural",
                        subtitle = "Amplía la escena sonora para sesiones largas con audífonos",
                        checked = settings.equalizerConfig.crossfeedEnabled,
                        onChecked = {
                            onSettings(settings.copy(equalizerConfig = settings.equalizerConfig.copy(crossfeedEnabled = it)))
                        },
                    )
                    PanelDivider()
                    ToggleRow(
                        title = "EQ automático por salida",
                        subtitle = "Guarda y cambia el preset según Bluetooth, USB/Cable o Altavoz",
                        checked = settings.autoEqByOutputEnabled,
                        onChecked = { onSettings(settings.copy(autoEqByOutputEnabled = it)) },
                    )
                    PanelDivider()
                    ClearCacheRow(
                        cacheSizeBytes = cacheSizeBytes,
                        isClearing = isClearingCache,
                        onClear = onClearCache,
                    )
                }

                SectionLabel(stringResource(R.string.settings_section_integrations))
                SettingsPanel {
                    DiscordSettingsCard(
                        settings = settings,
                        discordAuth = discordAuth,
                        discordConnection = discordConnection,
                        onSettings = onSettings,
                        onConnect = onConnectDiscord,
                        onDisconnect = onDisconnectDiscord,
                    )
                    PanelDivider()
                    LastfmSettingsCard(
                        state = lastfmState,
                        onConnect = onConnectLastfm,
                        onComplete = onCompleteLastfm,
                        onDisconnect = onDisconnectLastfm,
                    )
                    PanelDivider()
                    ListenBrainzSettingsCard(
                        state = listenBrainzState,
                        onConnect = onConnectListenBrainz,
                        onDisconnect = onDisconnectListenBrainz,
                    )
                    PanelDivider()
                    SpotifySettingsCard(
                        settings = settings,
                        onSettings = onSettings,
                        onConnectSpotify = onConnectSpotify,
                    )
                    PanelDivider()
                    QobuzSettingsCard(
                        status = qobuzStatus,
                        onConnect = onConnectQobuz,
                        onDisconnect = onDisconnectQobuz,
                        onEnabledChange = onQobuzEnabledChange,
                        onQualityChange = onQobuzQualityChange,
                    )
                    PanelDivider()
                    VeyraAccountSettingsCard(
                        settings = settings,
                        onSettings = onSettings,
                    )
                    PanelDivider()
                    SourcesHealthRow(
                        ytSignedIn = auth is AuthState.SignedIn,
                        qobuzConnected = qobuzStatus.isConnected,
                        spotifyConnected = settings.spotifySpdc.isNotBlank(),
                        supabaseSync = settings.bestMixSupabaseSync,
                    )
                    PanelDivider()
                    SettingsBackupRow(
                        settings = settings,
                        onImport = onSettings,
                    )
                }

                SectionLabel(stringResource(R.string.settings_section_appearance))
                SettingsPanel {
                    PlayerModeRow(settings.playerMode) { onSettings(settings.copy(playerMode = it)) }
                    PanelDivider()
                    ActionRow(
                        title = stringResource(R.string.settings_home_layout),
                        subtitle = stringResource(R.string.settings_home_layout_subtitle),
                        onClick = onHomeLayout,
                    )
                    PanelDivider()
                    ToggleRow(
                        title = stringResource(R.string.settings_player_gestures),
                        subtitle = stringResource(R.string.settings_player_gestures_subtitle),
                        checked = settings.playerGesturesEnabled,
                        onChecked = { onSettings(settings.copy(playerGesturesEnabled = it)) },
                    )
                    PanelDivider()
                    ToggleRow(
                        title = "Doble toque para saltar (±10s)",
                        subtitle = "Toca dos veces a la izquierda o derecha de la carátula",
                        checked = settings.doubleTapSeekEnabled,
                        onChecked = { onSettings(settings.copy(doubleTapSeekEnabled = it)) },
                    )
                    PanelDivider()
                    ToggleRow(
                        title = stringResource(R.string.settings_system_colors),
                        subtitle = stringResource(R.string.settings_system_colors_subtitle),
                        checked = settings.useSystemColors,
                        onChecked = { onSettings(settings.copy(useSystemColors = it)) },
                    )
                    PanelDivider()
                    ToggleRow(
                        title = stringResource(R.string.settings_animated_artwork),
                        subtitle = stringResource(R.string.settings_animated_artwork_subtitle),
                        checked = settings.animatedArtwork,
                        onChecked = { onSettings(settings.copy(animatedArtwork = it)) },
                    )
                    PanelDivider()
                    ToggleRow(
                        title = stringResource(R.string.settings_download_artwork),
                        subtitle = stringResource(R.string.settings_download_artwork_subtitle),
                        checked = settings.downloadAnimatedArtwork,
                        onChecked = { onSettings(settings.copy(downloadAnimatedArtwork = it)) },
                    )
                    PanelDivider()
                    ToggleRow(
                        title = stringResource(R.string.settings_animated_bg),
                        subtitle = stringResource(R.string.settings_animated_bg_subtitle),
                        checked = settings.animatedBackground,
                        onChecked = { onSettings(settings.copy(animatedBackground = it)) },
                    )
                    PanelDivider()
                    ToggleRow(
                        title = stringResource(R.string.settings_aura_sync),
                        subtitle = stringResource(R.string.settings_aura_sync_subtitle),
                        checked = settings.auraSyncEnabled,
                        onChecked = { onSettings(settings.copy(auraSyncEnabled = it)) },
                    )
                }

                SectionLabel(stringResource(R.string.settings_section_devices))
                SettingsPanel {
                    ActionRow(
                        title = stringResource(R.string.settings_manage_devices),
                        subtitle = stringResource(R.string.settings_manage_devices_subtitle),
                        onClick = onDevices,
                    )
                    PanelDivider()
                    ChromecastSettingsRow()
                }

                SectionLabel(stringResource(R.string.settings_section_updates))
                SettingsPanel {
                    val availableUpdate = (updateState as? UpdateState.Available)?.metadata
                    if (availableUpdate != null) {
                        ActionRow(
                            title = stringResource(R.string.settings_update_available, availableUpdate.version),
                            subtitle = if (availableUpdate.codename.isNotBlank()) {
                                "\"${availableUpdate.codename}\" • ${stringResource(R.string.settings_tap_to_install)}"
                            } else {
                                stringResource(R.string.settings_tap_to_install)
                            },
                            onClick = { onInstallUpdate(availableUpdate) },
                        )
                        PanelDivider()
                        ActionRow(
                            title = stringResource(R.string.settings_release_notes),
                            subtitle = stringResource(R.string.settings_release_notes_version, availableUpdate.version),
                            onClick = { showNotesDialog = true },
                        )
                    } else {
                        val cleanVersionName = BuildConfig.VERSION_NAME.replace("-debug", "").replace("debug", "").trim()
                        ActionRow(
                            title = stringResource(R.string.settings_release_notes),
                            subtitle = if (!BuildConfig.UPDATER_ENABLED) {
                                stringResource(R.string.settings_updates_disabled, cleanVersionName)
                            } else if (settings.betaChannelEnabled) {
                                stringResource(
                                    R.string.upd_beta_status_format,
                                    cleanVersionName,
                                    stringResource(R.string.widget_ready),
                                )
                            } else {
                                stringResource(R.string.settings_up_to_date, cleanVersionName)
                            },
                            onClick = { showNotesDialog = true },
                        )
                        if (BuildConfig.UPDATER_ENABLED) {
                            PanelDivider()
                            ActionRow(
                                title = stringResource(R.string.settings_check_updates),
                                subtitle = stringResource(R.string.settings_check_updates_subtitle),
                                onClick = onCheckForUpdates,
                            )
                        }
                    }
                    if (BuildConfig.UPDATER_ENABLED) {
                        PanelDivider()
                        ToggleRow(
                            title = stringResource(R.string.settings_beta_channel),
                            subtitle = stringResource(R.string.settings_beta_channel_subtitle),
                            checked = settings.betaChannelEnabled,
                            onChecked = {
                                val updated = settings.copy(betaChannelEnabled = it)
                                onSettings(updated)
                            },
                        )
                    }
                }

                SectionLabel(stringResource(R.string.settings_section_guide))
                SettingsPanel {
                    ActionRow(
                        title = stringResource(R.string.settings_welcome_guide),
                        subtitle = stringResource(R.string.settings_welcome_guide_subtitle),
                        onClick = onWelcome,
                    )
                }

                val cleanVersionName = BuildConfig.VERSION_NAME.replace("-debug", "").replace("debug", "").trim()
                val versionText = if (BuildConfig.CODENAME.isNotBlank()) {
                    stringResource(
                        R.string.upd_version_footer_codename,
                        cleanVersionName,
                        BuildConfig.CODENAME,
                    )
                } else {
                    stringResource(R.string.upd_version_footer, cleanVersionName)
                }
                Text(
                    versionText,
                    color = CanopyColors.Eyebrow,
                    style = MaterialTheme.typography.labelMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.height(OrchardChromeHeight))
            }
        }
    }

    if (showNotesDialog) {
        val available = (updateState as? UpdateState.Available)?.metadata
        if (available != null) {
            UpdateDialog(
                state = updateState,
                onInstall = {
                    showNotesDialog = false
                    onInstallUpdate(it)
                },
                onDismiss = { showNotesDialog = false },
            )
        } else {
            ReleaseNotesDialog(
                version = BuildConfig.VERSION_NAME,
                codename = BuildConfig.CODENAME,
                releaseNotes = MobileChangelog.CURRENT_RELEASE_NOTES,
                onDismiss = { showNotesDialog = false },
            )
        }
    }
}

/** Signed-in identity and the sign in / out affordance, given more weight than a plain row. */
@Composable
private fun AccountCard(
    auth: AuthState,
    onSignIn: () -> Unit,
    onSwitchAccount: () -> Unit,
    onSignOut: () -> Unit,
) {
    val isCyberpunk = LocalCyberpunkTheme.current
    val isLiquidAurora = dev.sfg.veyra.mobile.ui.components.LocalLiquidAuroraTheme.current
    val cardShape = if (isCyberpunk) VeyraCyber.shape else SettingsPanelShape
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp)
            .then(
                if (isCyberpunk) {
                    Modifier
                        .clip(cardShape)
                        .background(VeyraCyber.surface)
                        .border(VeyraCyber.borderWidth, VeyraCyber.cyan, cardShape)
                } else {
                    Modifier
                        .clip(cardShape)
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color.White.copy(alpha = if (isLiquidAurora) 0.14f else 0.11f),
                                    Color.White.copy(alpha = 0.05f),
                                ),
                            ),
                        )
                        .border(
                            1.1.dp,
                            Brush.verticalGradient(
                                listOf(
                                    Color.White.copy(alpha = 0.42f),
                                    Color.White.copy(alpha = 0.10f),
                                ),
                            ),
                            cardShape,
                        )
                }
            ),
    ) {
        Column {
            Row(
                Modifier.fillMaxWidth().padding(18.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                val avatarUrl = (auth as? AuthState.SignedIn)?.avatarUrl.orEmpty()
                val isCyberAvatar = LocalCyberpunkTheme.current
                val avatarShape = if (isCyberAvatar) VeyraCyber.shape else CircleShape
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(avatarShape)
                        .background(if (isCyberAvatar) VeyraCyber.ink else Color.White.copy(alpha = 0.10f))
                        .border(
                            if (isCyberAvatar) VeyraCyber.borderWidth else 1.dp,
                            if (isCyberAvatar) VeyraCyber.cyan else Color.White.copy(alpha = 0.28f),
                            avatarShape,
                        )
                        .padding(2.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    if (avatarUrl.isNotBlank()) {
                        AsyncImage(
                            model = avatarUrl,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize().clip(CircleShape),
                        )
                    } else {
                        Icon(
                            Icons.Rounded.Person,
                            contentDescription = null,
                            tint = LocalAccent.current,
                            modifier = Modifier.size(26.dp),
                        )
                    }
                }
                Spacer(Modifier.size(16.dp))
                Column(Modifier.weight(1f)) {
                    when (auth) {
                        AuthState.Restoring -> AccountText(
                            stringResource(R.string.acct_restoring_session),
                            stringResource(R.string.acct_one_moment),
                        )
                        AuthState.Authorizing -> AccountText(
                            stringResource(R.string.acct_completing_sign_in),
                            stringResource(R.string.acct_one_moment),
                        )
                        AuthState.SignedOut -> AccountText(
                            stringResource(R.string.acct_no_session),
                            stringResource(R.string.acct_no_session_desc),
                        )
                        is AuthState.SignedIn -> AccountText(
                            auth.displayName,
                            stringResource(R.string.acct_signed_in_label),
                        )
                        is AuthState.Error -> AccountText(
                            stringResource(R.string.acct_sign_in_error),
                            auth.message,
                            titleColor = CanopyColors.Danger,
                        )
                    }
                }
            }

            when (auth) {
                AuthState.SignedOut -> {
                    PanelDivider()
                    AccountButton(
                        label = stringResource(R.string.action_sign_in),
                        onClick = onSignIn,
                        primary = true,
                        icon = Icons.AutoMirrored.Rounded.Login,
                    )
                }
                is AuthState.Error -> {
                    PanelDivider()
                    AccountButton(
                        label = stringResource(R.string.action_retry),
                        onClick = onSignIn,
                        primary = true,
                        icon = Icons.Rounded.Sync,
                    )
                }
                is AuthState.SignedIn -> {
                    PanelDivider()
                    AccountButton(
                        label = stringResource(R.string.action_switch_account),
                        onClick = onSwitchAccount,
                        primary = true,
                        icon = Icons.Rounded.Sync,
                    )
                    PanelDivider()
                    AccountButton(
                        label = stringResource(R.string.action_sign_out),
                        onClick = onSignOut,
                        primary = false,
                        icon = Icons.AutoMirrored.Rounded.Logout,
                    )
                }
                else -> Unit
            }
        }
    }
}

@Composable
private fun AccountText(title: String, subtitle: String, titleColor: Color = CanopyColors.Text) {
    Text(
        title,
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        color = titleColor,
    )
    Text(
        subtitle,
        style = MaterialTheme.typography.bodyMedium,
        color = CanopyColors.Muted,
    )
}

@Composable
private fun AccountButton(
    label: String,
    onClick: () -> Unit,
    primary: Boolean,
    icon: ImageVector? = null,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 4.dp),
    ) {
        IntegrationAction(
            label = label,
            onClick = onClick,
            destructive = !primary,
            icon = icon,
            accentColor = if (primary) LocalAccent.current else null,
        )
    }
}

/** Section heading styled cleanly for Liquid Aurora or Cyberpunk HUD. */
@Composable
private fun SectionLabel(value: String) {
    val isCyberpunk = LocalCyberpunkTheme.current
    if (isCyberpunk) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(start = 16.dp, top = 24.dp, bottom = 8.dp)
                .clip(VeyraCyber.shape)
                .background(VeyraCyber.acidYellow)
                .border(1.5.dp, VeyraCyber.ink, VeyraCyber.shape)
                .padding(horizontal = 10.dp, vertical = 4.dp),
        ) {
            Text(
                text = "// ${value.uppercase()}",
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Black),
                color = VeyraCyber.ink,
            )
        }
    } else {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(start = 18.dp, top = 24.dp, bottom = 8.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.08f))
                .border(1.dp, Color.White.copy(alpha = 0.24f), CircleShape)
                .padding(horizontal = 12.dp, vertical = 5.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(7.dp)
                    .clip(CircleShape)
                    .background(Brush.radialGradient(listOf(Color(0xFF38BDF8), Color(0xFF8B5CF6)))),
            )
            Spacer(Modifier.width(8.dp))
            Text(
                value,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = Color.White,
            )
        }
    }
}

private val SettingsPanelShape = RoundedCornerShape(26.dp)

@Composable
private fun SettingsPanel(content: @Composable ColumnScope.() -> Unit) {
    val isGlass = LocalGlass.current.enabled
    val isCyberpunk = LocalCyberpunkTheme.current
    val panelShape = if (isCyberpunk) VeyraCyber.shape else SettingsPanelShape
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp)
            .then(
                if (isCyberpunk) {
                    Modifier
                        .clip(panelShape)
                        .background(VeyraCyber.surface)
                        .border(VeyraCyber.borderWidth, VeyraCyber.cyan, panelShape)
                } else if (isGlass) {
                    Modifier.glassPane(panelShape, GlassTone.PANEL)
                } else {
                    Modifier
                        .clip(panelShape)
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color.White.copy(alpha = 0.14f),
                                    Color.White.copy(alpha = 0.05f),
                                ),
                            ),
                        )
                        .border(
                            1.1.dp,
                            Brush.verticalGradient(
                                listOf(
                                    Color.White.copy(alpha = 0.40f),
                                    Color.White.copy(alpha = 0.08f),
                                ),
                            ),
                            panelShape,
                        )
                }
            ),
    ) {
        Column(content = content)
    }
}

/** Hairline groove between rows. */
@Composable
internal fun PanelDivider() {
    Box(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(1.dp)
            .background(
                Brush.horizontalGradient(
                    listOf(
                        Color.Transparent,
                        Color(0x18FFFFFF),
                        Color.Transparent,
                    )
                )
            ),
    )
}

/** Tactile Neumorphic Switch with dish channel and convex disc. */
@Composable
fun NeumorphicSwitch(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
) {
    val reduceMotion = LocalReducedMotion.current
    val thumbOffset by animateFloatAsState(
        targetValue = if (checked) 22f else 0f,
        animationSpec = if (reduceMotion) snap() else spring(dampingRatio = 0.75f, stiffness = 420f),
        label = "switchThumb",
    )
    val density = LocalDensity.current

    val isCyberpunk = LocalCyberpunkTheme.current
    val switchShape = if (isCyberpunk) VeyraCyber.shape else CircleShape
    Box(
        modifier = modifier
            .width(50.dp)
            .height(28.dp)
            .clip(switchShape)
            .background(
                if (isCyberpunk) {
                    if (checked) VeyraCyber.surfaceAlt else VeyraCyber.ink
                } else {
                    if (checked) Color(0xAA7C3AED) else Color.White.copy(alpha = 0.12f)
                }
            )
            .border(
                if (isCyberpunk) VeyraCyber.borderWidth else 1.dp,
                if (isCyberpunk) {
                    if (checked) VeyraCyber.cyan else VeyraCyber.textMuted.copy(alpha = 0.55f)
                } else {
                    if (checked) Color(0x8838BDF8) else Color(0x38FFFFFF)
                },
                switchShape,
            )
            .then(
                if (onCheckedChange != null) {
                    Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                    ) { onCheckedChange(!checked) }
                } else Modifier
            )
            .padding(2.dp),
        contentAlignment = Alignment.CenterStart,
    ) {
        Box(
            modifier = Modifier
                .offset { IntOffset(with(density) { thumbOffset.dp.roundToPx() }, 0) }
                .size(24.dp)
                .shadow(elevation = if (isCyberpunk) 0.dp else 4.dp, shape = switchShape, spotColor = Color.Black)
                .clip(switchShape)
                .background(
                    if (isCyberpunk) {
                        if (checked) Brush.verticalGradient(listOf(VeyraCyber.acidYellow, VeyraCyber.acidYellow))
                        else Brush.verticalGradient(listOf(VeyraCyber.surface, VeyraCyber.surface))
                    } else if (checked) {
                        Brush.verticalGradient(listOf(Color.White, Color(0xFFE0F2FE)))
                    } else {
                        Brush.verticalGradient(listOf(Color.White.copy(alpha = 0.85f), Color.White.copy(alpha = 0.65f)))
                    }
                )
                .border(
                    if (isCyberpunk) 1.5.dp else 1.dp,
                    if (isCyberpunk) VeyraCyber.ink else Color.White.copy(alpha = 0.55f),
                    switchShape,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier
                    .size(5.dp)
                    .clip(switchShape)
                    .background(
                        if (isCyberpunk) VeyraCyber.ink
                        else if (checked) Color(0xFF7C3AED) else Color(0x6664748B)
                    )
            )
        }
    }
}

/** Icon in a tinted rounded tile, shared by every row so the left edge lines up. */
@Composable
internal fun RowIcon(icon: ImageVector) {
    Box(
        modifier = Modifier
            .size(38.dp)
            .background(LocalAccent.current.copy(alpha = 0.14f), RoundedCornerShape(11.dp)),
        contentAlignment = Alignment.Center,
    ) {
        Icon(icon, contentDescription = null, tint = LocalAccent.current, modifier = Modifier.size(20.dp))
    }
}

@Composable
private fun ToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onChecked: (Boolean) -> Unit,
) {
    Row(
        Modifier
            .fillMaxWidth()
            .toggleable(value = checked, role = Role.Switch, onValueChange = onChecked)
            .defaultMinSize(minHeight = 64.dp)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f).padding(end = 16.dp)) {
            Text(
                title,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Normal),
                color = CanopyColors.Text,
            )
            Text(subtitle, color = CanopyColors.Muted, style = MaterialTheme.typography.bodyMedium)
        }
        NeumorphicSwitch(
            checked = checked,
            onCheckedChange = null,
        )
    }
}

@Composable
private fun ActionRow(
    title: String,
    subtitle: String,
    onClick: () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val haptic = LocalHapticFeedback.current

    val chevronOffset by animateFloatAsState(
        targetValue = if (isPressed) 3f else 0f,
        animationSpec = spring(stiffness = 500f),
        label = "ActionRowChevronOffset",
    )

    Row(
        Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onClick()
                },
            )
            .defaultMinSize(minHeight = 64.dp)
            .background(if (isPressed) Color.White.copy(alpha = 0.04f) else Color.Transparent)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f).padding(end = 16.dp)) {
            Text(
                title,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Normal),
                color = CanopyColors.Text,
            )
            Text(subtitle, color = CanopyColors.Muted, style = MaterialTheme.typography.bodyMedium)
        }
        Icon(
            Icons.AutoMirrored.Rounded.KeyboardArrowRight,
            contentDescription = null,
            tint = if (isPressed) Color.White else CanopyColors.Muted,
            modifier = Modifier
                .size(22.dp)
                .offset(x = chevronOffset.dp),
        )
    }
}

/** Segmented control reactivo con micro-escala, aura azul y vibración háptica. */
@Composable
private fun <T> SegmentedControl(
    items: List<T>,
    selectedItem: T,
    onSelect: (T) -> Unit,
    label: (T) -> String,
    modifier: Modifier = Modifier,
) {
    val haptic = LocalHapticFeedback.current
    val isCyberpunk = LocalCyberpunkTheme.current
    val pillShape = if (isCyberpunk) VeyraCyber.shape else CircleShape
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(pillShape)
            .background(if (isCyberpunk) VeyraCyber.ink else Color.White.copy(alpha = 0.07f))
            .border(
                if (isCyberpunk) VeyraCyber.borderWidth else 1.dp,
                if (isCyberpunk) VeyraCyber.cyan.copy(alpha = 0.72f) else Color.White.copy(alpha = 0.18f),
                pillShape,
            )
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        items.forEach { item ->
            val isSelected = item == selectedItem
            val animatedScale by animateFloatAsState(
                targetValue = if (isSelected && !isCyberpunk) 1.02f else 1f,
                animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                label = "SegmentedScale",
            )
            val animatedElevation by animateFloatAsState(
                targetValue = if (isSelected && !isCyberpunk) 6f else 0f,
                animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                label = "SegmentedElevation",
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(40.dp)
                    .shadow(
                        elevation = animatedElevation.dp,
                        shape = pillShape,
                        spotColor = Color(0xFF8B5CF6).copy(alpha = 0.55f),
                    )
                    .clip(pillShape)
                    .graphicsLayer {
                        scaleX = animatedScale
                        scaleY = animatedScale
                    }
                    .background(
                        if (isSelected) {
                            if (isCyberpunk) {
                                Brush.verticalGradient(listOf(VeyraCyber.acidYellow, VeyraCyber.acidYellow))
                            } else {
                                Brush.horizontalGradient(
                                    listOf(
                                        Color(0xFF7C3AED).copy(alpha = 0.90f),
                                        Color(0xFF2563EB).copy(alpha = 0.90f),
                                    ),
                                )
                            }
                        } else {
                            Brush.verticalGradient(listOf(Color.Transparent, Color.Transparent))
                        }
                    )
                    .then(
                        if (isSelected && !isCyberpunk) {
                            Modifier.border(1.dp, Color.White.copy(alpha = 0.38f), pillShape)
                        } else Modifier
                    )
                    .clickable {
                        if (!isSelected) {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            onSelect(item)
                        }
                    },
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = label(item),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = if (isSelected) {
                        if (isCyberpunk) VeyraCyber.ink else Color.White
                    } else CanopyColors.Muted,
                )
            }
        }
    }
}

/** In-app language as a segmented control; applies process-wide on selection. */
@Composable
private fun LanguageRow() {
    val context = LocalContext.current
    var language by remember { mutableStateOf(AppLocale.current(context)) }
    Column(Modifier.padding(horizontal = 16.dp, vertical = 14.dp)) {
        Column {
            Text(
                stringResource(R.string.settings_language),
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Normal),
                color = CanopyColors.Text,
            )
            Text(
                stringResource(R.string.settings_language_subtitle),
                color = CanopyColors.Muted,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
        Spacer(Modifier.height(14.dp))
        SegmentedControl(
            items = AppLanguage.entries,
            selectedItem = language,
            onSelect = { option ->
                language = option
                AppLocale.apply(context, option)
            },
            label = { it.label(context) },
        )
    }
}

private fun AppLanguage.label(context: Context): String =
    when (this) {
        AppLanguage.SYSTEM -> context.getString(R.string.language_system)
        AppLanguage.ENGLISH -> context.getString(R.string.language_english)
        AppLanguage.SPANISH -> context.getString(R.string.language_spanish)
    }

/** Audio quality as a single segmented control rather than three loose chips. */
@Composable
private fun QualityRow(value: AudioQuality, onChange: (AudioQuality) -> Unit) {
    val context = LocalContext.current
    Column(Modifier.padding(horizontal = 16.dp, vertical = 14.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier) {
                Text(
                    stringResource(R.string.settings_audio_quality),
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Normal),
                    color = CanopyColors.Text,
                )
                Text(
                    value.description(context),
                    color = CanopyColors.Muted,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
        Spacer(Modifier.height(14.dp))
        SegmentedControl(
            items = AudioQuality.entries,
            selectedItem = value,
            onSelect = onChange,
            label = { it.label(context) },
        )
    }
}

private fun AudioQuality.label(context: Context): String =
    when (this) {
        AudioQuality.DATA_SAVER -> context.getString(R.string.quality_data_saver)
        AudioQuality.NORMAL -> context.getString(R.string.quality_normal)
        AudioQuality.HIGH -> context.getString(R.string.quality_high)
        AudioQuality.MAX -> context.getString(R.string.quality_max)
    }

private fun AudioQuality.description(context: Context): String =
    when (this) {
        AudioQuality.DATA_SAVER -> context.getString(R.string.quality_data_saver_desc)
        AudioQuality.NORMAL -> context.getString(R.string.quality_normal_desc)
        AudioQuality.HIGH -> context.getString(R.string.quality_high_desc)
        AudioQuality.MAX -> context.getString(R.string.quality_max_desc)
    }

/** Selector de paradigma visual: Grove (Dock flotante neumórfico) vs Obsidian (Rack HUD superior). */
@Composable
private fun PlayerModeRow(value: MobilePlayerMode, onChange: (MobilePlayerMode) -> Unit) {
    val context = LocalContext.current
    Column(Modifier.padding(horizontal = 16.dp, vertical = 14.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column {
                Text(
                    stringResource(R.string.settings_player_mode),
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Normal),
                    color = CanopyColors.Text,
                )
                Text(
                    stringResource(R.string.settings_player_mode_subtitle),
                    color = CanopyColors.Muted,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
        Spacer(Modifier.height(14.dp))
        SegmentedControl(
            items = MobilePlayerMode.entries,
            selectedItem = value,
            onSelect = { mode ->
                if (mode != value) onChange(mode)
            },
            label = { it.label(context) },
        )
        Spacer(Modifier.height(10.dp))
        // Live mini-preview card showing the active visual language
        val previewCyber = value == MobilePlayerMode.OBSIDIAN
        val previewShape = if (previewCyber) VeyraCyber.shape else VeyraRadius.mdShape
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(previewShape)
                .background(
                    if (previewCyber) {
                        Brush.horizontalGradient(listOf(VeyraCyber.canvas, VeyraCyber.surface))
                    } else {
                        Brush.horizontalGradient(
                            listOf(
                                Color(0xFF7C3AED).copy(alpha = 0.28f),
                                Color.White.copy(alpha = 0.08f),
                                Color(0xFF38BDF8).copy(alpha = 0.22f),
                            ),
                        )
                    },
                )
                .border(
                    if (previewCyber) VeyraCyber.borderWidth else 1.dp,
                    if (previewCyber) VeyraCyber.cyan else Color.White.copy(alpha = 0.24f),
                    previewShape,
                )
                .padding(horizontal = 12.dp, vertical = 10.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(if (previewCyber) VeyraCyber.shape else RoundedCornerShape(8.dp))
                            .background(if (previewCyber) VeyraCyber.surfaceAlt else Color(0xFF8B5CF6).copy(alpha = 0.35f))
                            .border(
                                if (previewCyber) 1.5.dp else 1.dp,
                                if (previewCyber) VeyraCyber.cyan else Color(0xFF38BDF8).copy(alpha = 0.6f),
                                if (previewCyber) VeyraCyber.shape else RoundedCornerShape(8.dp),
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = if (previewCyber) Icons.Rounded.GraphicEq else Icons.Rounded.MusicNote,
                            contentDescription = null,
                            tint = if (previewCyber) VeyraCyber.cyan else Color(0xFF38BDF8),
                            modifier = Modifier.size(16.dp),
                        )
                    }
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Text(
                            text = value.label(context),
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                            color = if (previewCyber) VeyraCyber.cyan else CanopyColors.Text,
                        )
                        Text(
                            text = value.description(context),
                            style = MaterialTheme.typography.bodySmall,
                            color = CanopyColors.Muted,
                            maxLines = 2,
                        )
                    }
                }
            }
        }
    }
}

private fun MobilePlayerMode.label(context: Context): String =
    when (this) {
        MobilePlayerMode.GROVE -> context.getString(R.string.settings_player_mode_grove)
        MobilePlayerMode.OBSIDIAN -> context.getString(R.string.settings_player_mode_obsidian)
    }

private fun MobilePlayerMode.description(context: Context): String =
    when (this) {
        MobilePlayerMode.GROVE -> context.getString(R.string.settings_player_mode_grove_desc)
        MobilePlayerMode.OBSIDIAN -> context.getString(R.string.settings_player_mode_obsidian_desc)
    }

@Composable
private fun SourcesHealthRow(
    ytSignedIn: Boolean,
    qobuzConnected: Boolean,
    spotifyConnected: Boolean,
    supabaseSync: Boolean,
) {
    Column(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp)) {
        Text(
            text = "Salud de Fuentes",
            style = MaterialTheme.typography.bodyLarge,
            color = CanopyColors.Text,
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = "Estado de sesión y sincronización en tiempo real",
            style = MaterialTheme.typography.bodyMedium,
            color = CanopyColors.Muted,
        )
        Spacer(Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            dev.sfg.veyra.mobile.ui.components.StatusPillBadge(
                label = "YT",
                status = if (ytSignedIn) dev.sfg.veyra.mobile.ui.components.StatusPillTone.ACTIVE else dev.sfg.veyra.mobile.ui.components.StatusPillTone.MUTED,
                size = dev.sfg.veyra.mobile.ui.components.StatusPillSize.SM,
            )
            dev.sfg.veyra.mobile.ui.components.StatusPillBadge(
                label = "QOBUZ",
                status = if (qobuzConnected) dev.sfg.veyra.mobile.ui.components.StatusPillTone.ACTIVE else dev.sfg.veyra.mobile.ui.components.StatusPillTone.MUTED,
                size = dev.sfg.veyra.mobile.ui.components.StatusPillSize.SM,
            )
            dev.sfg.veyra.mobile.ui.components.StatusPillBadge(
                label = "CANVAS",
                status = if (spotifyConnected) dev.sfg.veyra.mobile.ui.components.StatusPillTone.SYNC else dev.sfg.veyra.mobile.ui.components.StatusPillTone.MUTED,
                size = dev.sfg.veyra.mobile.ui.components.StatusPillSize.SM,
            )
            dev.sfg.veyra.mobile.ui.components.StatusPillBadge(
                label = "CLOUD",
                status = if (supabaseSync) dev.sfg.veyra.mobile.ui.components.StatusPillTone.PRIMARY else dev.sfg.veyra.mobile.ui.components.StatusPillTone.MUTED,
                size = dev.sfg.veyra.mobile.ui.components.StatusPillSize.SM,
            )
        }
    }
}

@Composable
private fun SettingsBackupRow(
    settings: OrchardSettings,
    onImport: (OrchardSettings) -> Unit,
) {
    val context = LocalContext.current
    val exportLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.CreateDocument("application/json"),
    ) { uri ->
        if (uri != null) {
            val ok = dev.sfg.veyra.mobile.settings.SettingsBackupHelper.exportToUri(context, uri, settings)
            android.widget.Toast.makeText(
                context,
                if (ok) "Respaldo exportado con éxito" else "No se pudo exportar el respaldo",
                android.widget.Toast.LENGTH_SHORT,
            ).show()
        }
    }
    val importLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.OpenDocument(),
    ) { uri ->
        if (uri != null) {
            val restored = dev.sfg.veyra.mobile.settings.SettingsBackupHelper.importFromUri(context, uri, settings)
            if (restored != null) {
                onImport(restored)
                android.widget.Toast.makeText(context, "Configuración restaurada", android.widget.Toast.LENGTH_SHORT).show()
            } else {
                android.widget.Toast.makeText(context, "Archivo de respaldo inválido", android.widget.Toast.LENGTH_SHORT).show()
            }
        }
    }

    Column(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp)) {
        Text(
            text = "Respaldo de Configuración y EQ",
            style = MaterialTheme.typography.bodyLarge,
            color = CanopyColors.Text,
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = "Exporta o restaura tus temas, Aura Sync y presets de ecualizador",
            style = MaterialTheme.typography.bodyMedium,
            color = CanopyColors.Muted,
        )
        Spacer(Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            dev.sfg.veyra.mobile.ui.components.VeyraButton(
                label = "Exportar JSON",
                onClick = { exportLauncher.launch("veyra-settings-backup.json") },
                variant = dev.sfg.veyra.mobile.ui.components.VeyraButtonVariant.SECONDARY,
                size = dev.sfg.veyra.mobile.ui.components.VeyraButtonSize.SMALL,
                modifier = Modifier.weight(1f),
            )
            dev.sfg.veyra.mobile.ui.components.VeyraButton(
                label = "Importar JSON",
                onClick = { importLauncher.launch(arrayOf("application/json", "*/*")) },
                variant = dev.sfg.veyra.mobile.ui.components.VeyraButtonVariant.PRIMARY,
                size = dev.sfg.veyra.mobile.ui.components.VeyraButtonSize.SMALL,
                modifier = Modifier.weight(1f),
            )
        }
    }
}
