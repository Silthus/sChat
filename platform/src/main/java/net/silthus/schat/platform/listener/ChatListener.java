/*
 * This file is part of sChat, licensed under the MIT License.
 * Copyright (C) Silthus <https://www.github.com/silthus>
 * Copyright (C) sChat team and contributors
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package net.silthus.schat.platform.listener;

import java.util.Optional;
import java.util.UUID;
import lombok.NonNull;
import net.kyori.adventure.text.Component;
import net.silthus.schat.channel.Channel;
import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.chatter.ChatterProvider;
import net.silthus.schat.message.Message;
import net.silthus.schat.usecases.OnChat;

import static net.silthus.schat.message.Message.message;
import static net.silthus.schat.platform.locale.Messages.CANNOT_CHAT_NO_ACTIVE_CHANNEL;

public class ChatListener implements OnChat {

    private final ChatterProvider chatterProvider;

    public ChatListener(ChatterProvider chatterProvider) {
        this.chatterProvider = chatterProvider;
    }

    protected final void onChat(@NonNull UUID chatterId, @NonNull Component text) {
        final Chatter chatter = chatterProvider.get(chatterId);
        try {
            onChat(chatter, text);
        } catch (NoActiveChannel e) {
            CANNOT_CHAT_NO_ACTIVE_CHANNEL.send(chatter);
        }
    }

    @Override
    public final Message onChat(@NonNull Chatter chatter, @NonNull Component text) throws NoActiveChannel {
        final Optional<Channel> channel = chatter.getActiveChannel();
        if (channel.isEmpty())
            throw new NoActiveChannel();
        return message(text).source(chatter).to(channel.get()).type(Message.Type.CHAT).send();
    }
}