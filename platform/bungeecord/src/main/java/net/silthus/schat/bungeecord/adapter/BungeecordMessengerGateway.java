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

package net.silthus.schat.bungeecord.adapter;

import java.nio.charset.StandardCharsets;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.silthus.schat.bungeecord.BungeecordBootstrap;
import net.silthus.schat.messaging.MessengerGateway;
import net.silthus.schat.platform.plugin.scheduler.SchedulerAdapter;

public final class BungeecordMessengerGateway implements MessengerGateway, Listener {

    public static final String GATEWAY_TYPE = "pluginmessage";
    public static final String CHANNEL = "schat:update";

    private final ProxyServer proxy;
    private final SchedulerAdapter scheduler;

    public BungeecordMessengerGateway(BungeecordBootstrap bootstrap) {
        this.proxy = bootstrap.getProxy();
        this.scheduler = bootstrap.getScheduler();
        this.proxy.getPluginManager().registerListener(bootstrap.getLoader(), this);
    }

    @Override
    public void sendOutgoingMessage(String encodedMessage) {
        sendToAllServers(encodedMessage.getBytes(StandardCharsets.UTF_8));
    }

    private void sendToAllServers(byte[] bytes) {
        for (ServerInfo server : proxy.getServers().values()) {
            scheduler.async().execute(() -> server.sendData(CHANNEL, bytes));
        }
    }

    @EventHandler
    public void onIncomingMessage(PluginMessageEvent event) {
        if (!event.getTag().equals(CHANNEL))
            return;
        sendToAllServers(event.getData());
    }

    @Override
    public void close() {
        this.proxy.getPluginManager().unregisterListener(this);
    }
}