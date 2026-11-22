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

package dev.sfg.veyra.mobile.playback

import java.nio.ByteBuffer
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.ln
import kotlin.math.max
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * Medidor de espectro PCM en tiempo real con ganancia adaptativa (AGC) sin asignaciones por frame.
 * Lee muestras del buffer sin alterar su posición ni los samples de audio.
 */
object LiveAudioSpectrumMeter {

    const val BAND_COUNT = 24
    private const val ANALYSIS_BANDS = 12
    private const val SAMPLE_WINDOW = 64
    private const val ACTIVE_CONSUMER_WINDOW_NS = 500_000_000L
    private const val MIN_TAP_INTERVAL_NS = 22_000_000L
    private const val LIVE_SIGNAL_TIMEOUT_NS = 380_000_000L

    private val lock = Any()
    private val monoSamples = FloatArray(SAMPLE_WINDOW)
    private val coarseBands = FloatArray(ANALYSIS_BANDS)
    private val smoothedBands = FloatArray(BAND_COUNT)

    private val centerFrequencies = floatArrayOf(
        55f, 95f, 160f, 260f, 420f, 680f,
        1100f, 1800f, 2900f, 4600f, 7200f, 11000f,
    )
    private val goertzelCoeffs = FloatArray(ANALYSIS_BANDS)
    private var cachedSampleRate = 0

    // Ganancia adaptativa para que tanto temas suaves como electrónicos marquen el ritmo
    private var agcPeak = 0.25f
    private var smoothedBass = 0f
    private var smoothedOverall = 0f

    @Volatile
    private var lastConsumerReadNs = 0L

    @Volatile
    private var lastPcmWriteNs = 0L

    private var lastTapComputeNs = 0L

    fun tapPcm16(buffer: ByteBuffer, channelCount: Int, sampleRate: Int) {
        val nowNs = System.nanoTime()
        if (nowNs - lastConsumerReadNs > ACTIVE_CONSUMER_WINDOW_NS) return
        if (nowNs - lastTapComputeNs < MIN_TAP_INTERVAL_NS) return

        val bytesPerFrame = 2 * channelCount
        val totalFrames = buffer.remaining() / bytesPerFrame
        if (totalFrames < 16 || sampleRate <= 0) return

        lastTapComputeNs = nowNs
        val startPos = buffer.position()
        val stride = max(1, totalFrames / SAMPLE_WINDOW)

        var rmsAcc = 0f
        for (i in 0 until SAMPLE_WINDOW) {
            val frameIdx = (i * stride).coerceAtMost(totalFrames - 1)
            val byteOffset = startPos + frameIdx * bytesPerFrame
            var sum = 0f
            for (ch in 0 until channelCount) {
                sum += buffer.getShort(byteOffset + ch * 2).toFloat() / 32768f
            }
            val mono = sum / channelCount
            monoSamples[i] = mono
            rmsAcc += mono * mono
        }

        val effectiveRate = (sampleRate.toFloat() / stride).coerceAtLeast(1000f)
        val rms = sqrt(rmsAcc / SAMPLE_WINDOW)

        synchronized(lock) {
            if (cachedSampleRate != effectiveRate.toInt()) {
                cachedSampleRate = effectiveRate.toInt()
                for (b in 0 until ANALYSIS_BANDS) {
                    val omega = (2.0 * PI * centerFrequencies[b].coerceAtMost(effectiveRate * 0.45f) / effectiveRate)
                    goertzelCoeffs[b] = (2.0 * cos(omega)).toFloat()
                }
            }

            var frameMaxMag = 0.05f
            for (b in 0 until ANALYSIS_BANDS) {
                val coeff = goertzelCoeffs[b]
                var s1 = 0f
                var s2 = 0f
                for (i in 0 until SAMPLE_WINDOW) {
                    val hann = 0.5f - 0.5f * cos((2.0 * PI * i / (SAMPLE_WINDOW - 1)).toFloat())
                    val s0 = monoSamples[i] * hann + coeff * s1 - s2
                    s2 = s1
                    s1 = s0
                }
                val power = (s1 * s1 + s2 * s2 - coeff * s1 * s2).coerceAtLeast(0f)
                val mag = sqrt(power) / (SAMPLE_WINDOW * 0.35f)
                val tilt = 0.88f + (b / (ANALYSIS_BANDS - 1f)) * 0.72f
                val weighted = mag * tilt
                coarseBands[b] = weighted
                if (weighted > frameMaxMag) frameMaxMag = weighted
            }

            agcPeak = max(frameMaxMag, agcPeak * 0.990f).coerceIn(0.08f, 1.25f)
            val rmsNorm = (ln(1f + (rms / agcPeak) * 5f) / ln(6f)).coerceIn(0f, 1f)

            for (b in 0 until ANALYSIS_BANDS) {
                val ratio = (coarseBands[b] / agcPeak).coerceIn(0f, 1.4f)
                val compressed = (ln(1f + ratio * 6.5f) / ln(7.5f)).coerceIn(0f, 1f)
                // Realza contraste entre golpes y reposo
                coarseBands[b] = compressed.pow(1.22f)
            }

            var bassAcc = 0f
            var allAcc = 0f
            for (i in 0 until BAND_COUNT) {
                val pos = i * (ANALYSIS_BANDS - 1f) / (BAND_COUNT - 1f)
                val lo = pos.toInt().coerceIn(0, ANALYSIS_BANDS - 1)
                val hi = (lo + 1).coerceAtMost(ANALYSIS_BANDS - 1)
                val frac = pos - lo
                val target = (coarseBands[lo] * (1f - frac) + coarseBands[hi] * frac) * 0.84f + rmsNorm * 0.16f
                val prev = smoothedBands[i]
                val alpha = if (target > prev) 0.78f else 0.34f
                val next = (prev + alpha * (target - prev)).coerceIn(0f, 1f)
                smoothedBands[i] = next
                if (i < 6) bassAcc += next
                allAcc += next
            }

            val targetBass = (bassAcc / 6f).pow(1.15f).coerceIn(0f, 1f)
            val targetOverall = (allAcc / BAND_COUNT).coerceIn(0f, 1f)
            smoothedBass += (if (targetBass > smoothedBass) 0.78f else 0.28f) * (targetBass - smoothedBass)
            smoothedOverall += (if (targetOverall > smoothedOverall) 0.72f else 0.28f) * (targetOverall - smoothedOverall)
            lastPcmWriteNs = nowNs
        }
    }

    /**
     * Copia el espectro actual hacia [outBands]. Devuelve true si proviene de audio PCM real.
     */
    fun readSpectrum(outBands: FloatArray, isPlaying: Boolean): Boolean {
        val nowNs = System.nanoTime()
        lastConsumerReadNs = nowNs
        val count = minOf(outBands.size, BAND_COUNT)
        val hasLivePcm = isPlaying && (nowNs - lastPcmWriteNs) < LIVE_SIGNAL_TIMEOUT_NS

        synchronized(lock) {
            if (!isPlaying) {
                for (i in 0 until count) {
                    smoothedBands[i] *= 0.76f
                    outBands[i] = smoothedBands[i]
                }
                smoothedBass *= 0.76f
                smoothedOverall *= 0.76f
                return false
            }
            if (hasLivePcm) {
                for (i in 0 until count) {
                    outBands[i] = smoothedBands[i]
                }
                return true
            }
        }
        return false
    }

    /**
     * Energía rítmica [0f..1f] para Aura Sync y pulsos visuales, o -1f si no hay PCM local activo.
     */
    fun readBeatEnergy(isPlaying: Boolean): Float {
        val nowNs = System.nanoTime()
        lastConsumerReadNs = nowNs
        if (!isPlaying) return 0f
        if (nowNs - lastPcmWriteNs >= LIVE_SIGNAL_TIMEOUT_NS) return -1f
        synchronized(lock) {
            return (smoothedBass * 0.70f + smoothedOverall * 0.30f).coerceIn(0f, 1f)
        }
    }
}
