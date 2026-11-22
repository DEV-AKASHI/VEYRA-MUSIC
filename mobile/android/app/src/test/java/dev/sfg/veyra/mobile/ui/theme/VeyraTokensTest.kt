/*
 * Copyright (C) 2026 AKASHI DEV
 *
 * This file is part of Veyra Music.
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

package dev.sfg.veyra.mobile.ui.theme

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Guardas de los contratos de tokens que consumen los demás agentes. No
 * requieren dispositivo: validan escalas y resolución de reduce motion.
 */
class VeyraTokensTest {

    @Test
    fun `reduced motion collapses durations to zero`() {
        assertEquals(0, VeyraMotion.durationMillis(VeyraMotion.standardMillis, reducedMotion = true))
        assertEquals(
            VeyraMotion.standardMillis,
            VeyraMotion.durationMillis(VeyraMotion.standardMillis, reducedMotion = false),
        )
    }

    @Test
    fun `durations are never negative`() {
        assertEquals(0, VeyraMotion.durationMillis(-120, reducedMotion = false))
    }

    @Test
    fun `animator scale zero disables motion`() {
        assertFalse(VeyraMotion.shouldAnimate(0f))
        assertFalse(VeyraMotion.shouldAnimate(Float.NaN))
        assertTrue(VeyraMotion.shouldAnimate(0.5f))
        assertTrue(VeyraMotion.shouldAnimate(1f))
    }

    @Test
    fun `spacing scale is strictly ordered`() {
        val scale = listOf(
            VeyraSpace.xxs,
            VeyraSpace.xs,
            VeyraSpace.sm,
            VeyraSpace.md,
            VeyraSpace.lg,
            VeyraSpace.xl,
            VeyraSpace.xxl,
        )
        scale.zipWithNext().forEach { (smaller, larger) ->
            assertTrue("$smaller debe ser menor que $larger", smaller < larger)
        }
    }

    @Test
    fun `touch targets respect the accessibility floor`() {
        assertEquals(48f, VeyraSize.touchTarget.value, 0f)
        assertTrue(VeyraSize.buttonMedium.value >= VeyraSize.touchTarget.value)
        assertTrue(VeyraSize.iconButton.value < VeyraSize.touchTarget.value)
    }

    @Test
    fun `cyber accents stay distinct and limited`() {
        assertNotEquals(VeyraCyber.cyan, VeyraCyber.magenta)
        assertNotEquals(VeyraCyber.cyan, VeyraCyber.amber)
        assertNotEquals(VeyraCyber.lime, VeyraCyber.magenta)
        assertTrue(VeyraCyber.borderWidth.value > VeyraSize.hairline.value)
    }

    @Test
    fun `semantic roles keep the obsidian base`() {
        assertEquals(CanopyColors.Chrome, VeyraSemantic.canvas)
        assertEquals(CanopyColors.Surface, VeyraSemantic.surface)
        assertEquals(CanopyColors.Text, VeyraSemantic.textPrimary)
    }
}
