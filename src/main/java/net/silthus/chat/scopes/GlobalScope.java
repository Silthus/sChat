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

package net.silthus.chat.scopes;

import lombok.EqualsAndHashCode;
import net.silthus.chat.Constants;
import net.silthus.chat.SChat;
import net.silthus.chat.Scope;
import net.silthus.chat.annotations.Name;
import net.silthus.chat.conversations.Channel;

@EqualsAndHashCode
@Name(Constants.Scopes.GLOBAL)
public final class GlobalScope implements Scope {

    @Override
    public void onApply(Channel channel) {
        channel.addTarget(SChat.instance().getBungeecord());
    }

    @Override
    public void onRemove(Channel channel) {
        channel.removeTarget(SChat.instance().getBungeecord());
    }
}