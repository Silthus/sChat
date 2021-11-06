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

package net.silthus.chat;

import lombok.NonNull;
import net.silthus.chat.config.ChannelConfig;
import net.silthus.chat.conversations.Channel;
import net.silthus.chat.conversations.DirectConversation;

import java.util.Collection;

public interface Conversation extends ChatTarget {

    static Conversation direct(ChatTarget target1, ChatTarget target2) {
        return new DirectConversation(target1, target2);
    }

    static Channel channel(String identifier) {
        return Channel.channel(identifier);
    }

    static Channel channel(String identifier, ChannelConfig config) {
        return Channel.channel(identifier, config);
    }

    Format getFormat();

    void setFormat(Format format);

    Collection<ChatTarget> getTargets();

    void addTarget(@NonNull ChatTarget target);

    void removeTarget(@NonNull ChatTarget target);
}
