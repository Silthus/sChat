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

package net.silthus.schat.velocity.adapter;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.messages.ChannelIdentifier;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import java.nio.charset.StandardCharsets;
import net.silthus.schat.MessengerGateway;
import net.silthus.schat.platform.plugin.scheduler.SchedulerAdapter;
import net.silthus.schat.velocity.VelocityBootstrap;

import static com.velocitypowered.api.event.connection.PluginMessageEvent.ForwardResult.forward;
import static com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier.create;

public final class VelocityMessengerGateway implements MessengerGateway {

    public static final String GATEWAY_TYPE = "pluginmessage";
    public static final ChannelIdentifier CHANNEL = create("schat", "update");

    private final ProxyServer proxy;
    private final SchedulerAdapter scheduler;
    private final VelocityBootstrap bootstrap;

    public VelocityMessengerGateway(VelocityBootstrap bootstrap) {
        this.proxy = bootstrap.proxy();
        this.scheduler = bootstrap.scheduler();
        this.bootstrap = bootstrap;
        this.proxy.getEventManager().register(bootstrap, this);
    }

    @Override
    public void sendOutgoingMessage(String encodedMessage) {
        sendToAllServers(encodedMessage.getBytes(StandardCharsets.UTF_8));
    }

    private void sendToAllServers(byte[] bytes) {
        for (RegisteredServer server : proxy.getAllServers()) {
            scheduler.async().execute(() -> server.sendPluginMessage(CHANNEL, bytes));
        }
    }

    @Subscribe
    public void onIncomingMessage(PluginMessageEvent event) {
        if (!event.getIdentifier().equals(CHANNEL))
            return;
        sendToAllServers(event.getData());
        event.setResult(forward());
    }

    @Override
    public void close() {
        this.proxy.getEventManager().unregisterListener(bootstrap, this);
    }
}