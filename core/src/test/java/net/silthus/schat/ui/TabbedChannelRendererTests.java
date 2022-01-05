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

package net.silthus.schat.ui;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.silthus.schat.channel.Channel;
import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.identity.Identity;
import net.silthus.schat.message.Message;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TabbedChannelRendererTests {

    private static final String CHANNEL_KEY = "test";
    private static final String MESSAGE = "Hi";
    private static final String SOURCE = "Player";

    private Chatter chatter;
    private TabbedChannelRenderer formatter;

    @BeforeEach
    void setUp() {
        chatter = Chatter.createChatter();
        formatter = new TabbedChannelRenderer();
    }

    private void addChannel(String channel) {
        chatter.join(Channel.createChannel(channel));
    }

    private void setActiveChannel() {
        chatter.setActiveChannel(Channel.createChannel(CHANNEL_KEY));
    }

    private void sendMessage() {
        sendMessage(MESSAGE);
    }

    private void sendMessage(String text) {
        chatter.sendMessage(Message.message(text));
    }

    private void sendMessageWithSource() {
        final Chatter chatterSource = Chatter.chatter(Identity.identity(SOURCE)).create();
        this.chatter.sendMessage(Message.message(chatterSource, MESSAGE));
    }

    private Component format() {
        return formatter.render(chatter);
    }

    @NotNull
    private String serialize(final Component format) {
        return MiniMessage.miniMessage().serialize(format);
    }

    private void assertFormatContains(String... expected) {
        assertThat(serialize(format())).contains(expected);
    }

    private void assertFormatDoesNotContain(String... expected) {
        assertThat(serialize(format())).doesNotContain(expected);
    }

    @Test
    void givenNoChannels_printsNoAvailableChannels() {
        assertFormatContains("No joined channels!");
    }

    @Test
    void givenTwoChannels_listsChannels() {
        addChannel("one");
        addChannel("two");

        assertFormatContains("one", "two");
    }

    @Test
    void givenChannel_has_clickLink() {
        addChannel("test");

        assertFormatContains("/channel join test");
    }

    @Test
    void givenMessages_listsMessages() {
        sendMessage();
        sendMessageWithSource();

        assertFormatContains("Hi\n", "Player: Hi");
    }

    @Test
    void givenActiveChannel_underlinesChannel() {
        setActiveChannel();

        assertFormatContains("<underlined>", "</underlined>");
    }

    @Test
    void renders_blank_lines() {
        assertFormatContains("\n\n\n\n\n\n\n\n\n\n\n");
    }

    @Test
    void given_more_then_100_messages_renders_last_100() throws InterruptedException {
        sendMessage("one");
        Thread.sleep(1L);
        for (int i = 0; i < 100; i++) {
            sendMessage();
        }
        assertFormatDoesNotContain("one");
    }
}
