/*
 * IdeaVim - Vim emulator for IDEs based on the IntelliJ platform
 * Copyright (C) 2003-2022 The IdeaVim authors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package org.jetbrains.plugins.ideavim.action.motion.select

import com.maddyhome.idea.vim.command.VimStateMachine
import org.jetbrains.plugins.ideavim.SkipNeovimReason
import org.jetbrains.plugins.ideavim.TestWithoutNeovim
import org.jetbrains.plugins.ideavim.VimTestCase

class SelectEnableCharacterModeActionHandlerTest : VimTestCase() {
  @TestWithoutNeovim(SkipNeovimReason.SELECT_MODE)
  fun `test entering select mode`() {
    doTest(
      listOf("gh"),
      """
                A Discovery

                ${c}I found it in a legendary land
                all rocks and lavender and tufted grass,
                where it was settled on some sodden sand
                hard by the torrent of a mountain pass.
      """.trimIndent(),
      """
                A Discovery

                ${s}I$c$se found it in a legendary land
                all rocks and lavender and tufted grass,
                where it was settled on some sodden sand
                hard by the torrent of a mountain pass.
      """.trimIndent(),
      VimStateMachine.Mode.SELECT,
      VimStateMachine.SubMode.VISUAL_CHARACTER
    )
  }

  @TestWithoutNeovim(SkipNeovimReason.SELECT_MODE)
  fun `test entering select mode at the end of file`() {
    doTest(
      listOf("gh"),
      """
                A Discovery

                I found it in a legendary land
                all rocks and lavender and tufted grass,
                where it was settled on some sodden sand
                hard by the torrent of a mountain pass$c.
      """.trimIndent(),
      """
                A Discovery

                I found it in a legendary land
                all rocks and lavender and tufted grass,
                where it was settled on some sodden sand
                hard by the torrent of a mountain pass$s.$c$se
      """.trimIndent(),
      VimStateMachine.Mode.SELECT,
      VimStateMachine.SubMode.VISUAL_CHARACTER
    )
  }

  fun `test entering select mode on empty line`() {
    doTest(
      "gh",
      """
                A Discovery
                $c
                I found it in a legendary land
                all rocks and lavender and tufted grass,
                where it was settled on some sodden sand
                hard by the torrent of a mountain pass.
      """.trimIndent(),
      """
                A Discovery
                $s$c$se
                I found it in a legendary land
                all rocks and lavender and tufted grass,
                where it was settled on some sodden sand
                hard by the torrent of a mountain pass.
      """.trimIndent(),
      VimStateMachine.Mode.SELECT,
      VimStateMachine.SubMode.VISUAL_CHARACTER
    )
  }

  @TestWithoutNeovim(SkipNeovimReason.SELECT_MODE)
  fun `test entering select mode multicaret`() {
    doTest(
      listOf("gh"),
      """
                A Discovery
                $c
                ${c}I found it in a legendary land
                all rocks and lavender and tufted grass,
                where it was ${c}settled on some sodden sand
                hard by the torrent of a mountain pass.
      """.trimIndent(),
      """
                A Discovery
                $s$c$se
                ${s}I$c$se found it in a legendary land
                all rocks and lavender and tufted grass,
                where it was ${s}s$c${se}ettled on some sodden sand
                hard by the torrent of a mountain pass.
      """.trimIndent(),
      VimStateMachine.Mode.SELECT,
      VimStateMachine.SubMode.VISUAL_CHARACTER
    )
  }
}
