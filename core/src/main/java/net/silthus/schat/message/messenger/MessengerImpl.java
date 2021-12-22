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

package net.silthus.schat.message.messenger;

import net.silthus.schat.message.Message;
import net.silthus.schat.message.Messages;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

final class MessengerImpl<T> implements Messenger<T> {

    private final Class<T> type;
    private final Strategy<T> strategy;
    private final Messages messages = new Messages();

    MessengerImpl(final Class<T> type, Strategy<T> strategy) {
        this.type = type;
        this.strategy = strategy;
    }

    @Override
    public @NotNull @Unmodifiable Messages getMessages() {
        return messages.filter(Message.NOT_DELETED);
    }

    @Override
    public void sendMessage(final T target, final Message message) {
        this.messages.add(message);
        strategy.processMessage(target, this, message);
    }
}
