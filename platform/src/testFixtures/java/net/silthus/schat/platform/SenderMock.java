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

package net.silthus.schat.platform;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.silthus.schat.chatter.AbstractChatter;
import net.silthus.schat.chatter.MessageHandler;
import net.silthus.schat.chatter.PermissionHandler;
import net.silthus.schat.identity.Identity;
import net.silthus.schat.message.Message;
import net.silthus.schat.message.MessageTarget;
import net.silthus.schat.platform.sender.Sender;

import static net.silthus.schat.IdentityHelper.randomIdentity;

@Getter
@Setter
public final class SenderMock extends AbstractChatter implements Sender {

    public static SenderMock randomSender() {
        return new SenderMock(randomIdentity());
    }

    private @NonNull PermissionHandler permissionHandler = permission -> false;
    private @NonNull MessageTarget messageTarget = message -> {};
    private @NonNull MessageHandler messageHandler = message -> {};

    private SenderMock(Identity identity) {
        super(identity);
    }

    @Override
    public boolean hasPermission(String permission) {
        return permissionHandler.hasPermission(permission);
    }

    @Override
    protected void processMessage(Message message) {
        messageTarget.sendMessage(message);
        sendRawMessage(message.getText());
    }

    @Override
    public void sendRawMessage(Component message) {
        messageHandler.sendMessage(message);
    }
}
