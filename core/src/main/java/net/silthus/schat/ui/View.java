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

package net.silthus.schat.ui;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.sender.Sender;

public interface View {

    Key MESSAGE_MARKER_KEY = Key.key("schat", "message");
    Component MESSAGE_MARKER = Component.storageNBT(MESSAGE_MARKER_KEY.asString(), MESSAGE_MARKER_KEY);

    static View chatterView(Sender sender, Chatter chatter, Renderer<Chatter> renderer) {
        return new ChatterView(sender, chatter, renderer);
    }

    void update();

    Component render();
}
