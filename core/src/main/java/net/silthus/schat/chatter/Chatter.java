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

package net.silthus.schat.chatter;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.silthus.schat.channel.Channel;
import net.silthus.schat.identity.Identified;
import net.silthus.schat.identity.Identity;
import net.silthus.schat.message.Message;
import net.silthus.schat.message.MessageTarget;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

@Getter
@Setter
@EqualsAndHashCode(of = {"identity"})
public abstract class Chatter implements MessageTarget, Identified, Permissable {

    private final Identity identity;
    private final Set<Channel> channels = new HashSet<>();
    private final Set<Message> messages = new HashSet<>();
    private @Nullable Channel activeChannel;

    protected Chatter(@NonNull Identity identity) {
        this.identity = identity;
    }

    public final void setActiveChannel(@Nullable Channel activeChannel) {
        if (activeChannel != null)
            join(activeChannel);
        this.activeChannel = activeChannel;
    }

    public final @NotNull Optional<Channel> getActiveChannel() {
        return Optional.ofNullable(activeChannel);
    }

    public final boolean isActiveChannel(@Nullable Channel channel) {
        return activeChannel != null && activeChannel.equals(channel);
    }

    public final @NotNull @Unmodifiable List<Channel> getChannels() {
        return List.copyOf(channels);
    }

    public final void join(@NonNull Channel channel) {
        channel.addTarget(this);
        addChannel(channel);
    }

    public boolean isJoined(@Nullable Channel channel) {
        if (channel == null) return false;
        return channels.contains(channel);
    }

    protected final void addChannel(Channel channel) {
        this.channels.add(channel);
    }

    public final @NotNull @Unmodifiable List<Message> getMessages() {
        return List.copyOf(messages);
    }

    public abstract boolean hasPermission(String permission);

    @Override
    public final void sendMessage(@NonNull Message message) {
        messages.add(message);
        processMessage(message);
    }

    protected abstract void processMessage(Message message);
}
