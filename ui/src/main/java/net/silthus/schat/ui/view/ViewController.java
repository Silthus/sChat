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
package net.silthus.schat.ui.view;

import net.kyori.adventure.text.Component;
import net.silthus.schat.eventbus.EventBus;
import net.silthus.schat.eventbus.EventListener;
import net.silthus.schat.events.message.SendChannelMessageEvent;
import net.silthus.schat.message.Message;
import net.silthus.schat.ui.format.Format;
import net.silthus.schat.ui.placeholder.Replacements;

import static net.silthus.schat.channel.ChannelSettings.PRIVATE;

public class ViewController implements EventListener {

    private final Replacements replacements;

    public ViewController(Replacements replacements) {
        this.replacements = replacements;
    }

    @Override
    public void bind(EventBus bus) {
        bus.on(SendChannelMessageEvent.class, this::handleMessage);
    }

    private void handleMessage(SendChannelMessageEvent event) {
        if (event.channel().is(PRIVATE))
            return;
        final Component formatted = formatMessage(event);
        event.message().set(Message.FORMATTED, replacements.applyTo(event.message(), formatted));
    }

    private Component formatMessage(SendChannelMessageEvent event) {
        return event.channel().get(Format.MESSAGE_FORMAT)
            .format(View.empty(), event.message());
    }
}
