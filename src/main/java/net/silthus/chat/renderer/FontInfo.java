/*
 * sChat, a Supercharged Minecraft Chat Plugin
 * Copyright (C) Silthus <https://www.github.com/silthus>
 * Copyright (C) sChat team and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.silthus.chat.renderer;

public enum FontInfo {

    A('A', 5),
    a('a', 5),
    B('B', 5),
    b('b', 5),
    C('C', 5),
    c('c', 5),
    D('D', 5),
    d('d', 5),
    E('E', 5),
    e('e', 5),
    F('F', 5),
    f('f', 4),
    G('G', 5),
    g('g', 5),
    H('H', 5),
    h('h', 5),
    I('I', 3),
    i('i', 1),
    J('J', 5),
    j('j', 5),
    K('K', 5),
    k('k', 4),
    L('L', 5),
    l('l', 1),
    M('M', 5),
    m('m', 5),
    N('N', 5),
    n('n', 5),
    O('O', 5),
    o('o', 5),
    P('P', 5),
    p('p', 5),
    Q('Q', 5),
    q('q', 5),
    R('R', 5),
    r('r', 5),
    S('S', 5),
    s('s', 5),
    T('T', 5),
    t('t', 4),
    U('U', 5),
    u('u', 5),
    V('V', 5),
    v('v', 5),
    W('W', 5),
    w('w', 5),
    X('X', 5),
    x('x', 5),
    Y('Y', 5),
    y('y', 5),
    Z('Z', 5),
    z('z', 5),
    NUM_1('1', 5),
    NUM_2('2', 5),
    NUM_3('3', 5),
    NUM_4('4', 5),
    NUM_5('5', 5),
    NUM_6('6', 5),
    NUM_7('7', 5),
    NUM_8('8', 5),
    NUM_9('9', 5),
    NUM_0('0', 5),
    SMALL_NUM_1('\u2081', 4), // ₁
    SMALL_NUM_2('\u2082', 4), // ₂
    SMALL_NUM_3('\u2083', 4), // ₃
    SMALL_NUM_4('\u2084', 4), // ₄
    SMALL_NUM_5('\u2085', 4), // ₅
    SMALL_NUM_6('\u2086', 4), // ₆
    SMALL_NUM_7('\u2087', 4), // ₇
    SMALL_NUM_8('\u2088', 4), // ₈
    SMALL_NUM_9('\u2089', 4), // ₉
    SMALL_NUM_0('\u2080', 4), // ₀
    EXCLAMATION_POINT('!', 1),
    AT_SYMBOL('@', 6),
    NUM_SIGN('#', 5),
    DOLLAR_SIGN('$', 5),
    PERCENT('%', 5),
    UP_ARROW('^', 5),
    AMPERSAND('&', 5),
    ASTERISK('*', 5),
    LEFT_PARENTHESIS('(', 4),
    RIGHT_PERENTHESIS(')', 4),
    MINUS('-', 5),
    UNDERSCORE('_', 5),
    PLUS_SIGN('+', 5),
    EQUALS_SIGN('=', 5),
    LEFT_CURL_BRACE('{', 4),
    RIGHT_CURL_BRACE('}', 4),
    LEFT_BRACKET('[', 3),
    RIGHT_BRACKET(']', 3),
    COLON(':', 1),
    SEMI_COLON(';', 1),
    DOUBLE_QUOTE('"', 3),
    SINGLE_QUOTE('\'', 1),
    LEFT_ARROW('<', 4),
    RIGHT_ARROW('>', 4),
    QUESTION_MARK('?', 5),
    SLASH('/', 5),
    BACK_SLASH('\\', 5),
    LONG_MINUS('\u2500', 8), // ─
    LONG_LINE('\u2502', 5), // │
    LEFT_LINE_CORNER('\u250C', 8), // ┌
    RIGHT_LINE_CORNER('\u2510', 8), // ┐
    CARET('>', 4),
    DOWN_CARET('\u23F7', 5), // ⏷
    DOUBLE_ARROW_RIGHT('\u21D2', 7), // ⇒
    LINE('|', 1),
    TILDE('~', 5),
    TICK('`', 2),
    PERIOD('.', 1),
    COMMA(',', 1),
    SPACE(' ', 3),
    DEFAULT('a', 4);

    private final char character;
    private final int length;

    FontInfo(char character, int length) {
        this.character = character;
        this.length = length;
    }

    public char getCharacter() {
        return this.character;
    }

    public int getLength() {
        return this.length;
    }

    public int getBoldLength() {
        if (this == FontInfo.SPACE) return this.getLength();
        return this.length + 1;
    }

    public String toString() {
        return String.valueOf(getCharacter());
    }

    public static FontInfo getFontInfo(char c) {
        for (FontInfo dFI : FontInfo.values()) {
            if (dFI.getCharacter() == c) return dFI;
        }
        return FontInfo.DEFAULT;
    }
}
