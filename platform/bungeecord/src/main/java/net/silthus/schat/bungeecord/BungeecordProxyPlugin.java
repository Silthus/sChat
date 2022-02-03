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

package net.silthus.schat.bungeecord;

import lombok.Getter;
import net.silthus.schat.bungeecord.adapter.BungeecordEventBus;
import net.silthus.schat.bungeecord.adapter.BungeecordMessengerGateway;
import net.silthus.schat.bungeecord.adapter.BungeecordSenderFactory;
import net.silthus.schat.eventbus.EventBus;
import net.silthus.schat.messaging.MessengerGatewayProvider;
import net.silthus.schat.platform.config.adapter.ConfigurationAdapter;
import net.silthus.schat.platform.plugin.AbstractSChatProxyPlugin;
import net.silthus.schat.platform.sender.Sender;

import static net.silthus.schat.bungeecord.adapter.BungeecordMessengerGateway.GATEWAY_TYPE;

@Getter
public final class BungeecordProxyPlugin extends AbstractSChatProxyPlugin {

    private final BungeecordBootstrap bootstrap;
    private BungeecordSenderFactory senderFactory;

    BungeecordProxyPlugin(BungeecordBootstrap bootstrap) {
        this.bootstrap = bootstrap;
    }

    @Override
    protected EventBus createEventBus() {
        return new BungeecordEventBus(getBootstrap().getProxy());
    }

    @Override
    public Sender getConsole() {
        return senderFactory.wrap(getBootstrap().getProxy().getConsole());
    }

    @Override
    protected void setupSenderFactory() {
        this.senderFactory = new BungeecordSenderFactory(bootstrap.getLoader());
    }

    @Override
    protected ConfigurationAdapter createConfigurationAdapter() {
        return null;
    }

    @Override
    protected void registerMessengerGateway(MessengerGatewayProvider.Registry registry) {
        registry.register(GATEWAY_TYPE, in -> new BungeecordMessengerGateway(getBootstrap()));
    }

    @Override
    protected void registerListeners() {

    }
}
