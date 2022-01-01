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

package net.silthus.schat.platform.plugin;

import lombok.Getter;
import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.chatter.ChatterRepository;
import net.silthus.schat.chatter.Chatters;
import net.silthus.schat.chatter.checks.JoinChannelPermissionCheck;
import net.silthus.schat.sender.Sender;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.Component.text;
import static net.silthus.schat.chatter.Chatter.JoinChannel.steps;

final class ChatterManager implements Chatters {

    @Getter
    private final ChatterRepository repository;

    ChatterManager(ChatterRepository repository) {
        this.repository = repository;
    }

    @Override
    public Chatter get(Sender sender) {
        if (contains(sender.getUniqueId()))
            return get(sender.getUniqueId());
        return createAndCacheChatterFor(sender);
    }

    @NotNull
    private Chatter createAndCacheChatterFor(Sender sender) {
        final Chatter chatter = createChatterFor(sender);
        add(chatter);
        return chatter;
    }

    private Chatter createChatterFor(Sender sender) {
        return Chatter.chatter(sender.getIdentity())
            .messengerStrategy((message, context) -> sender.sendMessage(text(message.getText())))
            .joinChannel(steps(new JoinChannelPermissionCheck(sender)))
            .create();
    }

}
