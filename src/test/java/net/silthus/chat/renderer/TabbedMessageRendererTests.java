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

package net.silthus.chat.renderer;

import be.seeseemelk.mockbukkit.entity.PlayerMock;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.silthus.chat.ChatTarget;
import net.silthus.chat.Format;
import net.silthus.chat.Message;
import net.silthus.chat.TestBase;
import net.silthus.chat.conversations.Channel;
import net.silthus.chat.identities.Chatter;
import org.bukkit.ChatColor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static net.kyori.adventure.text.Component.newline;
import static net.silthus.chat.Constants.View.CHANNEL_DIVIDER;
import static org.assertj.core.api.Assertions.assertThat;

public class TabbedMessageRendererTests extends TestBase {

    private TabbedMessageRenderer view;
    private Chatter chatter;

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();

        view = new TabbedMessageRenderer();
        chatter = Chatter.of(server.addPlayer());
        chatter.setActiveConversation(ChatTarget.channel("test"));
    }

    @Test
    void footer() {
        Component footer = view.footer(chatter);
        assertThat(footer).isEqualTo(view.conversationTabs(chatter));
    }

    @Test
    void clearChat_renders100BlankLines() {

        Component component = view.clearChat();
        assertThat(component.children())
                .hasSize(100)
                .allMatch(c -> c.equals(newline()));
    }

    @Test
    void channels_renders_noChannelInfo() {

        Chatter chatter = Chatter.of(new PlayerMock(server, "test"));
        assertThat(chatter.getConversations()).isEmpty();

        Component component = view.conversationTabs(chatter);
        String text = getStripedText(component);
        assertThat(text).isEqualTo(CHANNEL_DIVIDER + " No Channels selected. Use /ch join <channel> to join a channel.");
    }

    @Test
    void channels_renders_subscribedChannels() {
        addChannels();

        String text = getStripedText(view.conversationTabs(chatter));
        assertThat(text)
                .contains(CHANNEL_DIVIDER + " test " + CHANNEL_DIVIDER)
                .contains(CHANNEL_DIVIDER + " foobar " + CHANNEL_DIVIDER);
    }

    @Test
    void channels_renders_activeChannelUnderlined() {
        addChannels();
        chatter.setActiveConversation(ChatTarget.channel("active"));

        String text = getText(view.conversationTabs(chatter));
        assertThat(text)
                .contains(ChatColor.GREEN + "" + ChatColor.UNDERLINE + "active")
                .doesNotContain(ChatColor.GREEN + "" + ChatColor.UNDERLINE + "test")
                .contains(ChatColor.GRAY + "" + "test");
    }

    @Test
    void supports_channelName_placeholders() {

        Channel channel = createChannel("foo", config -> config.name("<player_name>"));
        chatter.subscribe(channel);
        chatter.setActiveConversation(channel);

        String text = getText(view.conversationTabs(chatter));

        assertThat(text).contains(toText(chatter.getDisplayName()));
    }

    @Test
    void renders_onlyUniqueMessages() {

        Message message = Message.message("test").format(Format.noFormat()).build();

        Component component = view.renderMessages(List.of(message, message));
        assertThat(toText(component)).containsOnlyOnce("test");
    }

    @Test
    void ordersMessages_byTimestamp() {
        Collection<Message> messages = randomMessages(10);
        String sortedMessages = messages.stream().sorted().map(this::toText).collect(Collectors.joining("\n"));
        Component component = view.renderMessages(messages);
        assertThat(toText(component)).isEqualTo(sortedMessages);
    }

    private String getStripedText(Component component) {
        return ChatColor.stripColor(getText(component));
    }

    private String getText(Component component) {
        return LegacyComponentSerializer.legacySection().serialize(component);
    }

    private void addChannels() {
        chatter.subscribe(ChatTarget.channel("test"));
        chatter.subscribe(ChatTarget.channel("foobar"));
    }
}