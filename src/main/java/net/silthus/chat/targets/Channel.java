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

package net.silthus.chat.targets;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.java.Log;
import net.kyori.adventure.text.Component;
import net.silthus.chat.ChatSource;
import net.silthus.chat.ChatTarget;
import net.silthus.chat.Constants;
import net.silthus.chat.Message;
import net.silthus.chat.config.ChannelConfig;

@Log
@Data
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class Channel extends AbstractConversation implements ChatSource {

    public static Channel channel(String identifier) {
        return new Channel(identifier);
    }

    public static Channel channel(String identifier, ChannelConfig config) {
        return new Channel(identifier, config);
    }

    private final ChannelConfig config;

    private Channel(String identifier) {
        this(identifier, ChannelConfig.defaults());
    }

    private Channel(String identifier, ChannelConfig config) {
        super(identifier);
        this.config = config;
        if (config.name() != null)
            setName(Component.text(config.name()));
        setFormat(config.format());
    }

    public String getPermission() {
        return Constants.Permissions.getChannelPermission(this);
    }

    public String getAutoJoinPermission() {
        return Constants.Permissions.getAutoJoinPermission(this);
    }

    public boolean canJoin(Chatter chatter) {
        if (getConfig().protect()) {
            return chatter.getPlayer().hasPermission(getPermission());
        }
        return true;
    }

    public boolean canSendMessage(Chatter chatter) {
        return canJoin(chatter);
    }

    public boolean canAutoJoin(Chatter chatter) {
        if (!canJoin(chatter)) return false;
        if (canJoin(chatter) && getConfig().autoJoin()) return true;
        return chatter.getPlayer().hasPermission(getAutoJoinPermission());
    }

    @Override
    public Message sendMessage(String message) {

        return Message.message(message).to(this).send();
    }

    @Override
    public void sendMessage(Message message) {
        addReceivedMessage(message);

        Message.MessageBuilder channelMessage = message.copy()
                .conversation(this)
                .targets(getTargets());

        if (getConfig().sendToConsole())
            channelMessage.to(Console.console());

        channelMessage.send();
    }

    public record Subscription(Channel channel, ChatTarget target) {

    }
}
