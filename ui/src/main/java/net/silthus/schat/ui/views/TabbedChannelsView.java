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

package net.silthus.schat.ui.views;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.TextComponent;
import net.silthus.schat.channel.Channel;
import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.identity.Identity;
import net.silthus.schat.message.Message;
import net.silthus.schat.pointer.Setting;
import net.silthus.schat.pointer.Settings;
import net.silthus.schat.ui.format.ChannelFormat;
import net.silthus.schat.ui.format.PointeredFormat;
import net.silthus.schat.ui.model.ChatterViewModel;
import net.silthus.schat.ui.view.View;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.Component.join;
import static net.kyori.adventure.text.Component.newline;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.event.ClickEvent.Action.RUN_COMMAND;
import static net.kyori.adventure.text.event.ClickEvent.clickEvent;
import static net.kyori.adventure.text.format.NamedTextColor.GRAY;
import static net.kyori.adventure.text.format.NamedTextColor.GREEN;
import static net.kyori.adventure.text.format.TextDecoration.UNDERLINED;
import static net.silthus.schat.pointer.Setting.setting;
import static net.silthus.schat.pointer.Settings.createSettings;

@Getter
@Accessors(fluent = true)
public final class TabbedChannelsView implements View {

    public static final Setting<JoinConfiguration> CHANNEL_JOIN_CONFIG = setting(JoinConfiguration.class, "channel_join_config", JoinConfiguration.builder()
        .prefix(text("| "))
        .separator(text(" | "))
        .suffix(text(" |"))
        .build());
    public static final Setting<PointeredFormat> MESSAGE_FORMAT = setting(PointeredFormat.class, "message", msg ->
        msg.get(Message.SOURCE)
            .filter(Identity.IS_NOT_NIL)
            .map(identity -> identity.displayName().append(text(": ")))
            .orElse(Component.empty())
            .append(msg.getOrDefault(Message.TEXT, Component.empty())));

    public static final Setting<Settings> ACTIVE_CHANNEL = setting(Settings.class, "active_channel", Settings.settingsBuilder()
        .withStatic(ChannelFormat.COLOR, GREEN)
        .withStatic(ChannelFormat.DECORATION, UNDERLINED)
        .create());
    public static final Setting<Settings> INACTIVE_CHANNEL = setting(Settings.class, "inactive_channel", Settings.settingsBuilder()
        .withStatic(ChannelFormat.COLOR, GRAY)
        .withStatic(ChannelFormat.ON_CLICK, channel ->
            clickEvent(RUN_COMMAND, "/channel join " + channel.getOrDefault(Channel.KEY, null)))
        .create());

    public static final Setting<Settings> FORMAT = setting(Settings.class, "format", Settings.settingsBuilder()
        .withStatic(MESSAGE_FORMAT, MESSAGE_FORMAT.defaultValue())
        .withStatic(ACTIVE_CHANNEL, ACTIVE_CHANNEL.defaultValue())
        .withStatic(INACTIVE_CHANNEL, INACTIVE_CHANNEL.defaultValue())
        .withStatic(CHANNEL_JOIN_CONFIG, CHANNEL_JOIN_CONFIG.defaultValue())
        .create()
    );

    private final ChatterViewModel viewModel;
    private final List<Tab> tabs = new ArrayList<>();
    private final Settings settings = createSettings();

    TabbedChannelsView(Chatter chatter) {
        this.viewModel = ChatterViewModel.of(chatter);
    }

    @Override
    public Component render() {
        return renderBlankLines()
            .append(combineMessagesAndChannels(renderMessages(), renderChannels()))
            .append(VIEW_MARKER);
    }

    private Component renderBlankLines() {
        final int blankLineAmount = Math.max(0, get(VIEW_HEIGHT) - viewModel.messages().size());
        return blankLines(blankLineAmount);
    }

    private Component blankLines(int amount) {
        final TextComponent.Builder builder = text();
        for (int i = 0; i < amount; i++) {
            builder.append(newline());
        }
        return builder.build();
    }

    @NotNull
    private Component combineMessagesAndChannels(Component messages, Component channels) {
        if (viewModel.messages().isEmpty() || viewModel.channels().isEmpty())
            return messages.append(channels);
        else
            return messages.append(newline()).append(channels);
    }

    private Component renderMessages() {
        return join(newline(), getRenderedMessages());
    }

    private Component renderChannels() {
        if (viewModel.channels().isEmpty())
            return Component.empty();
        else
            return join(get(CHANNEL_JOIN_CONFIG), getRenderedChannels());
    }

    private List<Component> getRenderedMessages() {
        final ArrayList<Component> messages = new ArrayList<>();
        for (final Message message : viewModel.messages()) {
            if (isMessageDisplayed(message))
                messages.add(get(MESSAGE_FORMAT).format(message));
        }
        return messages;
    }

    private boolean isMessageDisplayed(Message message) {
        if (viewModel.noActiveChannel() && viewModel.isSystemMessage(message))
            return true;
        if (viewModel.isPrivateChannel())
            return viewModel.isSentToActiveChannel(message) && !viewModel().isSystemMessage(message);
        return viewModel.isSystemMessage(message) || viewModel.isSentToActiveChannel(message);
    }

    private List<Component> getRenderedChannels() {
        final ArrayList<Component> channels = new ArrayList<>();
        for (final Channel channel : viewModel.channels()) {
            if (viewModel.isActiveChannel(channel))
                channels.add(new ChannelFormat(viewModel.chatter(), channel.get(FORMAT).get(ACTIVE_CHANNEL)).format(channel));
            else
                channels.add(new ChannelFormat(viewModel.chatter(), channel.get(FORMAT).get(INACTIVE_CHANNEL)).format(channel));
        }
        return channels;
    }

    public static class Tab {
        public Channel channel() {
            return null;
        }

        public List<Message> messages() {
            return null;
        }
    }
}
