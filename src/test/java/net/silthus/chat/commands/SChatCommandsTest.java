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

package net.silthus.chat.commands;

import be.seeseemelk.mockbukkit.entity.PlayerMock;
import net.md_5.bungee.api.ChatColor;
import net.silthus.chat.ChatTarget;
import net.silthus.chat.Constants;
import net.silthus.chat.Message;
import net.silthus.chat.TestBase;
import net.silthus.chat.conversations.Channel;
import net.silthus.chat.conversations.DirectConversation;
import net.silthus.chat.identities.Chatter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static net.kyori.adventure.text.Component.text;
import static org.assertj.core.api.Assertions.assertThat;

class SChatCommandsTest extends TestBase {

    private PlayerMock player;

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();

        player = server.addPlayer();
        player.addAttachment(plugin, Constants.PERMISSION_PLAYER_COMMANDS, true);
        player.addAttachment(plugin, Constants.PERMISSION_PLAYER_CHANNEL_COMMANDS, true);
        player.addAttachment(plugin, Constants.PERMISSION_PLAYER_CHANNEL_JOIN, true);
        player.addAttachment(plugin, Constants.PERMISSION_PLAYER_CHANNEL_QUICKMESSAGE, true);
        player.addAttachment(plugin, Constants.PERMISSION_PLAYER_DIRECT_MESSAGE, true);
    }

    @Nested
    class ChannelCommands {

        @Test
        void join_JoinsPlayerToChannel() {
            Channel channel = ChatTarget.channel("test");
            plugin.getChannelRegistry().add(channel);

            player.addAttachment(plugin, channel.getPermission(), true);

            assertThat(player.performCommand("schat channel join test")).isTrue();
            assertThat(channel.getTargets()).contains(Chatter.of(player));
            assertThat(player.nextMessage()).contains(ChatColor.GRAY + "You joined the channel: " + ChatColor.GOLD + "test" + ChatColor.GRAY + ".");
        }

        @Test
        void join_withoutPermission_fails() {
            Channel channel = createChannel("test", cfg -> cfg.protect(true));
            plugin.getChannelRegistry().add(channel);

            assertThat(player.performCommand("ch test")).isTrue();
            assertThat(channel.getTargets()).isEmpty();
            assertThat(player.nextMessage()).contains(ChatColor.RED + "You don't have permission to access the 'test' channel.");
        }

        @Test
        void quickMessage_sendsMessageToChannel() {
            Channel channel = ChatTarget.channel("test");
            plugin.getChannelRegistry().add(channel);

            assertThat(player.performCommand("ch test Hey how are you?")).isTrue();
            assertThat(channel.getLastReceivedMessage()).isNotNull();
            assertThat(toText(channel.getLastReceivedMessage())).contains("Player0[!]&7: Hey how are you?");
        }

        @Test
        void quickMessage_noPermission_fails() {
            Channel channel = createChannel("test", config -> config.protect(true));
            plugin.getChannelRegistry().add(channel);

            assertThat(player.performCommand("ch test Hey how are you?")).isTrue();
            assertThat(channel.getLastReceivedMessage()).isNull();
            assertThat(player.nextMessage()).contains(ChatColor.RED + "You don't have permission to send messages to the 'test' channel.");
        }
    }

    @Nested
    class DirectMessages {
        @Test
        void directMessage_sendsMessageToBothPlayers() {
            PlayerMock player1 = server.addPlayer();
            player.performCommand("tell Player1 Hey whats up?");

            assertLastMessage(player, "Hey whats up?");
            assertLastMessage(player1, "Hey whats up?");
        }

        @Test
        void directMessage_opensDirectConversation() {
            PlayerMock player1 = server.addPlayer();
            player.performCommand("dm Player1 Hi");

            assertThat(Chatter.of(player).getActiveConversation())
                    .isNotNull()
                    .isInstanceOf(DirectConversation.class)
                    .extracting(ChatTarget::getLastReceivedMessage)
                    .extracting(Message::getText)
                    .isEqualTo(text("Hi"));
        }
    }

    private void assertLastMessage(PlayerMock player, String message) {
        assertThat(Chatter.of(player).getLastReceivedMessage())
                .isNotNull()
                .extracting(Message::getText)
                .isEqualTo(text(message));
    }
}