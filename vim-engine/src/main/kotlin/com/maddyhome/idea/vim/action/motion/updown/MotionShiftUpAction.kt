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

package com.maddyhome.idea.vim.action.motion.updown

import com.maddyhome.idea.vim.api.ExecutionContext
import com.maddyhome.idea.vim.api.VimEditor
import com.maddyhome.idea.vim.api.injector
import com.maddyhome.idea.vim.command.Command
import com.maddyhome.idea.vim.handler.ShiftedArrowKeyHandler

/**
 * @author Alex Plate
 */

class MotionShiftUpAction : ShiftedArrowKeyHandler() {

  override val type: Command.Type = Command.Type.OTHER_READONLY

  override fun motionWithKeyModel(editor: VimEditor, context: ExecutionContext, cmd: Command) {
    editor.forEachCaret { caret ->
      val vertical = injector.motion.getVerticalMotionOffset(editor, caret, -cmd.count)
      val col = injector.engineEditorHelper.prepareLastColumn(caret)
      caret.moveToOffset(vertical)

      injector.engineEditorHelper.updateLastColumn(caret, col)
    }
  }

  override fun motionWithoutKeyModel(editor: VimEditor, context: ExecutionContext, cmd: Command) {
    injector.motion.scrollFullPage(editor, editor.primaryCaret(), -cmd.count)
  }
}
