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

package org.jetbrains.plugins.ideavim.action.motion.leftright

import com.maddyhome.idea.vim.command.VimStateMachine
import org.jetbrains.plugins.ideavim.VimTestCase

class MotionRightMatchCharActionTest : VimTestCase() {
  fun `test move and repeat`() {
    doTest(
      "fx;",
      "hello ${c}x hello x hello",
      "hello x hello ${c}x hello",
      VimStateMachine.Mode.COMMAND,
      VimStateMachine.SubMode.NONE
    )
  }

  fun `test move and repeat twice`() {
    doTest(
      "fx;;",
      "${c}hello x hello x hello x hello",
      "hello x hello x hello ${c}x hello",
      VimStateMachine.Mode.COMMAND,
      VimStateMachine.SubMode.NONE
    )
  }

  fun `test move and repeat two`() {
    doTest(
      "fx2;",
      "${c}hello x hello x hello x hello",
      "hello x hello x hello ${c}x hello",
      VimStateMachine.Mode.COMMAND,
      VimStateMachine.SubMode.NONE
    )
  }

  fun `test move and repeat three`() {
    doTest(
      "fx3;",
      "${c}hello x hello x hello x hello x hello",
      "hello x hello x hello x hello ${c}x hello",
      VimStateMachine.Mode.COMMAND,
      VimStateMachine.SubMode.NONE
    )
  }
}
