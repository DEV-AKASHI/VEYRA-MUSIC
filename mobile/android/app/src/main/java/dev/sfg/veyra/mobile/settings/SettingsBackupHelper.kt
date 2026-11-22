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

package dev.sfg.veyra.mobile.settings

import android.content.Context
import android.net.Uri
import dev.sfg.veyra.mobile.model.AudioQuality
import dev.sfg.veyra.mobile.model.EqualizerConfig
import dev.sfg.veyra.mobile.model.MobilePlayerMode
import dev.sfg.veyra.mobile.model.OrchardSettings
import org.json.JSONArray
import org.json.JSONObject

/**
 * Exports and restores user preferences and EQ presets as a portable JSON file.
 */
object SettingsBackupHelper {
    fun exportToUri(context: Context, uri: Uri, settings: OrchardSettings): Boolean {
        return runCatching {
            val eq = settings.equalizerConfig
            val root = JSONObject()
                .put("version", 1)
                .put("playerMode", settings.playerMode.name)
                .put("auraSyncEnabled", settings.auraSyncEnabled)
                .put("animatedArtwork", settings.animatedArtwork)
                .put("animatedBackground", settings.animatedBackground)
                .put("frostedGlass", settings.frostedGlass)
                .put("audioQuality", settings.audioQuality.name)
                .put("crossfadeEnabled", settings.crossfadeEnabled)
                .put("crossfadeSeconds", settings.crossfadeSeconds)
                .put("smartCrossfade", settings.smartCrossfade)
                .put("volumeNormalizationEnabled", settings.volumeNormalizationEnabled)
                .put("autoplayEnabled", settings.autoplayEnabled)
                .put("autoEqByOutputEnabled", settings.autoEqByOutputEnabled)
                .put("doubleTapSeekEnabled", settings.doubleTapSeekEnabled)
                .put(
                    "equalizer",
                    JSONObject()
                        .put("enabled", eq.enabled)
                        .put("presetId", eq.presetId)
                        .put("preampDb", eq.preampDb.toDouble())
                        .put("bassBoost", eq.bassBoost.toDouble())
                        .put("limiterEnabled", eq.limiterEnabled)
                        .put("crossfeedEnabled", eq.crossfeedEnabled)
                        .put("gains", JSONArray(eq.gains)),
                )
            context.contentResolver.openOutputStream(uri)?.use { stream ->
                stream.write(root.toString(2).toByteArray(Charsets.UTF_8))
            }
            true
        }.getOrDefault(false)
    }

    fun importFromUri(context: Context, uri: Uri, current: OrchardSettings): OrchardSettings? {
        return runCatching {
            val raw = context.contentResolver.openInputStream(uri)?.use {
                it.readBytes().toString(Charsets.UTF_8)
            } ?: return null
            val root = JSONObject(raw)
            val eqObj = root.optJSONObject("equalizer")
            val gainsArr = eqObj?.optJSONArray("gains")
            val gains = if (gainsArr != null && gainsArr.length() == 10) {
                List(10) { i -> gainsArr.optDouble(i, 0.0).toFloat() }
            } else {
                current.equalizerConfig.gains
            }
            val eq = EqualizerConfig(
                enabled = eqObj?.optBoolean("enabled", current.equalizerConfig.enabled) ?: current.equalizerConfig.enabled,
                presetId = eqObj?.optString("presetId", current.equalizerConfig.presetId) ?: current.equalizerConfig.presetId,
                gains = gains,
                preampDb = eqObj?.optDouble("preampDb", current.equalizerConfig.preampDb.toDouble())?.toFloat()
                    ?: current.equalizerConfig.preampDb,
                bassBoost = eqObj?.optDouble("bassBoost", current.equalizerConfig.bassBoost.toDouble())?.toFloat()
                    ?: current.equalizerConfig.bassBoost,
                limiterEnabled = eqObj?.optBoolean("limiterEnabled", current.equalizerConfig.limiterEnabled)
                    ?: current.equalizerConfig.limiterEnabled,
                crossfeedEnabled = eqObj?.optBoolean("crossfeedEnabled", current.equalizerConfig.crossfeedEnabled)
                    ?: current.equalizerConfig.crossfeedEnabled,
            )
            current.copy(
                playerMode = runCatching {
                    MobilePlayerMode.valueOf(root.optString("playerMode", current.playerMode.name))
                }.getOrDefault(current.playerMode),
                auraSyncEnabled = root.optBoolean("auraSyncEnabled", current.auraSyncEnabled),
                animatedArtwork = root.optBoolean("animatedArtwork", current.animatedArtwork),
                animatedBackground = root.optBoolean("animatedBackground", current.animatedBackground),
                frostedGlass = root.optBoolean("frostedGlass", current.frostedGlass),
                audioQuality = runCatching {
                    AudioQuality.valueOf(root.optString("audioQuality", current.audioQuality.name))
                }.getOrDefault(current.audioQuality),
                crossfadeEnabled = root.optBoolean("crossfadeEnabled", current.crossfadeEnabled),
                crossfadeSeconds = root.optInt("crossfadeSeconds", current.crossfadeSeconds),
                smartCrossfade = root.optBoolean("smartCrossfade", current.smartCrossfade),
                volumeNormalizationEnabled = root.optBoolean("volumeNormalizationEnabled", current.volumeNormalizationEnabled),
                autoplayEnabled = root.optBoolean("autoplayEnabled", current.autoplayEnabled),
                autoEqByOutputEnabled = root.optBoolean("autoEqByOutputEnabled", current.autoEqByOutputEnabled),
                doubleTapSeekEnabled = root.optBoolean("doubleTapSeekEnabled", current.doubleTapSeekEnabled),
                equalizerConfig = eq,
            )
        }.getOrNull()
    }
}
