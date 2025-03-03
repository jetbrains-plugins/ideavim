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

package org.jetbrains.plugins.ideavim.action;

import com.maddyhome.idea.vim.VimPlugin;
import com.maddyhome.idea.vim.api.VimInjectorKt;
import com.maddyhome.idea.vim.group.RegisterGroup;
import com.maddyhome.idea.vim.register.Register;
import org.jetbrains.plugins.ideavim.VimTestCase;
import org.junit.Assert;

import static com.maddyhome.idea.vim.register.RegisterConstants.*;

/**
 * @author ayzen
 */
public class SpecialRegistersTest extends VimTestCase {

  private static final String DUMMY_TEXT = "text";

  @Override
  protected void setUp() throws Exception {
    super.setUp();

    final RegisterGroup registerGroup = VimPlugin.getRegister();
    registerGroup.setKeys('a', VimInjectorKt.getInjector().getParser().stringToKeys(DUMMY_TEXT));
    registerGroup.setKeys(SMALL_DELETION_REGISTER, VimInjectorKt.getInjector().getParser().stringToKeys(DUMMY_TEXT));
    for (char c = '0'; c <= '9'; c++) {
      registerGroup.setKeys(c, VimInjectorKt.getInjector().getParser().stringToKeys(DUMMY_TEXT));
    }
  }

  // VIM-581
  public void testSmallDelete() {
    typeTextInFile(VimInjectorKt.getInjector().getParser().parseKeys("de"), "one <caret>two three\n");

    assertEquals("two", getRegisterText(SMALL_DELETION_REGISTER));
    // Text smaller than line doesn't go to numbered registers (except special cases)
    assertRegisterNotChanged('1');
  }

  // |d| |%| Special case for small delete
  public void testSmallDeleteWithPercent() {
    typeTextInFile(VimInjectorKt.getInjector().getParser().parseKeys("d%"), "(one<caret> two) three\n");

    assertRegisterChanged('1');
    assertRegisterChanged(SMALL_DELETION_REGISTER);
  }

  // |d| |(| Special case for small delete
  public void testSmallDeleteTillPrevSentence() {
    typeTextInFile(VimInjectorKt.getInjector().getParser().parseKeys("d("), "One. Two<caret>. Three.\n");

    assertRegisterChanged('1');
    assertRegisterChanged(SMALL_DELETION_REGISTER);
  }

  // |d| |)| Special case for small delete
  public void testSmallDeleteTillNextSentence() {
    typeTextInFile(VimInjectorKt.getInjector().getParser().parseKeys("d)"), "One. <caret>Two. Three.\n");

    assertRegisterChanged('1');
    assertRegisterChanged(SMALL_DELETION_REGISTER);
  }

  // |d| |`| Special case for small delete
  public void testSmallDeleteWithMark() {
    typeTextInFile(VimInjectorKt.getInjector().getParser().parseKeys("ma" + "b" + "d`a"), "one two<caret> three\n");

    assertRegisterChanged('1');
    assertRegisterChanged(SMALL_DELETION_REGISTER);
  }

  // |d| |/| Special case for small delete
  public void testSmallDeleteWithSearch() {
    typeTextInFile(VimInjectorKt.getInjector().getParser().parseKeys("d/" + "o" + "<Enter>"), "one <caret>two three\n");

    assertRegisterChanged('1');
    assertRegisterChanged(SMALL_DELETION_REGISTER);
  }

  // |d| |?| Special case for small delete
  public void testSmallDeleteWithBackSearch() {
    typeTextInFile(VimInjectorKt.getInjector().getParser().parseKeys("d?" + "t" + "<Enter>"), "one two<caret> three\n");

    assertRegisterChanged('1');
    assertRegisterChanged(SMALL_DELETION_REGISTER);
  }

  // |d| |n| Special case for small delete
  public void testSmallDeleteWithSearchRepeat() {
    typeTextInFile(VimInjectorKt.getInjector().getParser().parseKeys("/" + "t" + "<Enter>" + "dn"), "<caret>one two three\n");

    assertRegisterChanged('1');
    assertRegisterChanged(SMALL_DELETION_REGISTER);
  }

  // |d| |N| Special case for small delete
  public void testSmallDeleteWithBackSearchRepeat() {
    typeTextInFile(VimInjectorKt.getInjector().getParser().parseKeys("/" + "t" + "<Enter>" + "dN"), "one tw<caret>o three\n");

    assertRegisterChanged('1');
    assertRegisterChanged(SMALL_DELETION_REGISTER);
  }

  // |d| |{| Special case for small delete
  public void testSmallDeleteTillPrevParagraph() {
    typeTextInFile(VimInjectorKt.getInjector().getParser().parseKeys("d{"), "one<caret> two three");

    assertRegisterChanged('1');
    assertRegisterChanged(SMALL_DELETION_REGISTER);
  }

  // |d| |}| Special case for small delete
  public void testSmallDeleteTillNextParagraph() {
    typeTextInFile(VimInjectorKt.getInjector().getParser().parseKeys("d}"), "one<caret> two three");

    assertRegisterChanged('1');
    assertRegisterChanged(SMALL_DELETION_REGISTER);
  }

  public void testSmallDeleteInRegister() {
    typeTextInFile(VimInjectorKt.getInjector().getParser().parseKeys("\"ade"), "one <caret>two three\n");

    // Small deletes (less than a line) with register specified go to that register and to numbered registers
    assertRegisterChanged('a');
    assertRegisterNotChanged('1');
    assertRegisterNotChanged(SMALL_DELETION_REGISTER);
  }

  public void testLineDelete() {
    typeTextInFile(VimInjectorKt.getInjector().getParser().parseKeys("dd"), "one <caret>two three\n");

    assertRegisterChanged('1');
    assertRegisterNotChanged(SMALL_DELETION_REGISTER);
  }

  public void testLineDeleteInRegister() {
    typeTextInFile(VimInjectorKt.getInjector().getParser().parseKeys("\"add"), "one <caret>two three\n");

    assertRegisterChanged('a');
    assertRegisterNotChanged('1');
  }

  public void testNumberedRegistersShifting() {
    configureByText("<caret>one\ntwo\nthree\nfour\nfive\nsix\nseven\neight\nnine\nten\n");

    typeText(VimInjectorKt.getInjector().getParser().parseKeys("dd" + "dd"));
    assertEquals("one\n", getRegisterText('2'));
    assertEquals("two\n", getRegisterText('1'));

    typeText(VimInjectorKt.getInjector().getParser().parseKeys("dd" + "dd" + "dd"));
    assertEquals("one\n", getRegisterText('5'));
    assertEquals("four\n", getRegisterText('2'));

    typeText(VimInjectorKt.getInjector().getParser().parseKeys("dd" + "dd" + "dd" + "dd"));
    assertEquals("one\n", getRegisterText('9'));
  }

  public void testSearchRegisterAfterSearch() {
    configureByText("<caret>one\ntwo\nthree\nfour\nfive\nsix\nseven\neight\nnine\nten\n");
    enterSearch("three", true);
    assertEquals("three", getRegisterText(LAST_SEARCH_REGISTER));
  }

  public void testSearchRegisterAfterSubstitute() {
    configureByText("<caret>one\ntwo\nthree\nfour\nfive\nsix\nseven\neight\nnine\nten\n");
    enterCommand("%s/three/3/g");
    assertEquals("three", getRegisterText(LAST_SEARCH_REGISTER));
  }

  public void testSearchRegisterAfterSearchRange() {
    configureByText("<caret>one\ntwo\nthree\nfour\nfive\nsix\nseven\neight\nnine\nten\n");
    enterCommand("/three/d");
    assertEquals("three", getRegisterText(LAST_SEARCH_REGISTER));
  }

  public void testSearchRegisterAfterMultipleSearchRanges() {
    configureByText("<caret>one\ntwo\nthree\nfour\nfive\nsix\nseven\neight\nnine\nten\n");
    enterCommand("/one/;/three/d");
    assertEquals("three", getRegisterText(LAST_SEARCH_REGISTER));
  }

  public void testLastInsertedTextRegister() {
    configureByText("<caret>");

    typeText(VimInjectorKt.getInjector().getParser().parseKeys("i" + "abc" + "<Esc>"));

    assertEquals("abc", getRegisterText('.'));

    assertRegisterChanged(LAST_INSERTED_TEXT_REGISTER);

  }

  private void assertRegisterChanged(char registerName) {
    String registerText = getRegisterText(registerName);
    Assert.assertNotEquals(DUMMY_TEXT, registerText);
  }

  private void assertRegisterNotChanged(char registerName) {
    String registerText = getRegisterText(registerName);
    assertEquals(DUMMY_TEXT, registerText);
  }

  private String getRegisterText(char registerName) {
    final RegisterGroup registerGroup = VimPlugin.getRegister();
    final Register register = registerGroup.getRegister(registerName);
    assertNotNull(register);

    return register.getText();
  }

}
