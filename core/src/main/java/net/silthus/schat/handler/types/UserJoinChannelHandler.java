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

package net.silthus.schat.handler.types;

import net.silthus.schat.User;
import net.silthus.schat.channel.Channel;
import net.silthus.schat.chatter.Chatter;

public class UserJoinChannelHandler extends JoinChannelHandler.Default {

    private final User user;

    public UserJoinChannelHandler(User user) {
        this.user = user;
    }

    @Override
    public void joinChannel(Chatter chatter, Channel channel) {
        if (!channel.get(Channel.PUBLIC) && !user.hasPermission("schat.channel." + channel.getKey() + ".join"))
            throw new Channel.AccessDenied();
        super.joinChannel(chatter, channel);
    }
}
