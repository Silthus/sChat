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

package net.silthus.schat.channel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

final class InMemoryChannelRepository implements ChannelRepository {

    private final Map<String, Channel> channels = new HashMap<>();

    @Override
    public List<Channel> all() {
        return List.copyOf(channels.values());
    }

    @Override
    public Channel get(String key) {
        if (!contains(key))
            throw new ChannelNotFound();
        return channels.get(key);
    }

    @Override
    public void add(Channel channel) {
        if (channels.containsKey(channel.getKey()))
            throw new DuplicateChannel();
        this.channels.put(channel.getKey(), channel);
    }

    @Override
    public boolean contains(String key) {
        return channels.containsKey(key);
    }

}