/*
 * This file is part of sChat, licensed under the MIT License.
 * Copyright (C) Silthus <https://www.github.com/silthus>
 * Copyright (C) sChat team and contributors
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package net.silthus.schat.view;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.silthus.schat.pointer.Configured;
import net.silthus.schat.pointer.Setting;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.TextDecoration.UNDERLINED;
import static net.silthus.schat.pointer.Setting.setting;

public interface View extends Configured.Modifiable<View> {

    Key VIEW_MARKER_KEY = Key.key("schat", "view");
    Component VIEW_MARKER = Component.storageNBT(VIEW_MARKER_KEY.asString(), VIEW_MARKER_KEY);

    Setting<Integer> VIEW_HEIGHT = setting(Integer.class, "format.height", 100); // minecraft chat box height in lines
    Setting<Format> ACTIVE_CHANNEL_FORMAT = setting(Format.class, "format.active_channel", name -> name.decorate(UNDERLINED));
    Setting<JoinConfiguration> CHANNEL_JOIN_CONFIG = setting(JoinConfiguration.class, "format.channel_join_config", JoinConfiguration.builder()
        .prefix(text("| "))
        .separator(text(" | "))
        .suffix(text(" |"))
        .build());
    Setting<Format> MESSAGE_SOURCE_FORMAT = setting(Format.class, "format.message_source", name -> name.append(text(": ")));

    static View empty() {
        return Component::empty;
    }

    Component render();

    default boolean isRenderedView(Component render) {
        return render.contains(VIEW_MARKER) || render.children().contains(VIEW_MARKER);
    }
}