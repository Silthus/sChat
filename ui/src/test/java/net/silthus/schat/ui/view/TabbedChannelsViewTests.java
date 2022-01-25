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

package net.silthus.schat.ui.view;

import lombok.SneakyThrows;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.flattener.ComponentFlattener;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.silthus.schat.channel.Channel;
import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.identity.Identity;
import net.silthus.schat.message.Message;
import net.silthus.schat.view.View;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static net.kyori.adventure.text.format.NamedTextColor.GREEN;
import static net.silthus.schat.AssertionHelper.assertNPE;
import static net.silthus.schat.channel.Channel.PRIORITY;
import static net.silthus.schat.channel.Channel.createChannel;
import static net.silthus.schat.channel.ChannelHelper.ConfiguredSetting.set;
import static net.silthus.schat.channel.ChannelHelper.channelWith;
import static net.silthus.schat.chatter.ChatterMock.randomChatter;
import static net.silthus.schat.message.MessageHelper.randomMessage;
import static net.silthus.schat.ui.model.ChatterViewModel.of;
import static net.silthus.schat.ui.view.Views.tabbedChannels;
import static net.silthus.schat.view.View.ACTIVE_CHANNEL_FORMAT;
import static net.silthus.schat.view.View.CHANNEL_JOIN_CONFIG;
import static net.silthus.schat.view.View.MESSAGE_FORMAT;
import static net.silthus.schat.view.View.VIEW_HEIGHT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class TabbedChannelsViewTests {

    private static final @NotNull MiniMessage COMPONENT_SERIALIZER = MiniMessage.get();
    private static final @NotNull PlainTextComponentSerializer PLAIN_TEXT_SERIALIZER = PlainTextComponentSerializer.plainText()
        .toBuilder().flattener(ComponentFlattener.textOnly()).build();
    private Chatter chatter;
    private View view;

    @BeforeEach
    void setUp() {
        chatter = randomChatter();
        view = tabbedChannels(of(chatter));
    }

    @NotNull
    private String text(Message message) {
        return COMPONENT_SERIALIZER.serialize(message.text());
    }

    @SneakyThrows
    @NotNull
    private Message addMessage(Message message) {
        chatter.sendMessage(message);
        Thread.sleep(1L); // required to order messages by time
        return message;
    }

    private void addMessage(String text) {
        addMessage(Message.message(text));
    }

    private void addMessageWithSource(String source, String text) {
        addMessage(Message.message(text).source(Identity.identity(source)));
    }

    private void assertTextRenders(String expected) {
        assertEquals(expected, PLAIN_TEXT_SERIALIZER.serialize(view.render()).trim());
    }

    private void assertViewRenders(String expected) {
        assertEquals(expected, COMPONENT_SERIALIZER.serialize(view.render()).trim());
    }

    @Nested class given_null_chatter {

        @Test
        @SuppressWarnings("ConstantConditions")
        void throws_npe() {
            assertNPE(() -> tabbedChannels(null));
        }
    }

    @Nested class given_no_messages_and_no_channels {

        @Test
        void renders_view_with_only_empty_lines() {
            assertEquals("\n".repeat(Math.max(0, view.get(VIEW_HEIGHT))), COMPONENT_SERIALIZER.serialize(view.render()));
        }
    }

    @Nested class given_single_message {

        @Test
        void renders_message_text() {
            final Message message = addMessage(randomMessage());
            assertTextRenders(text(message));
        }
    }

    @Nested class given_single_message_with_source {

        @BeforeEach
        void setUp() {
            addMessageWithSource("Bob", "Hi");
        }

        @Test
        void renders_source_name_with_message_text() {
            assertTextRenders("Bob: Hi");
        }

        @Nested class and_custom_message_source_format {

            @Test
            void uses_format() {
                view = tabbedChannels(of(chatter))
                    .set(MESSAGE_FORMAT, msg -> Component.text("<").append(msg.source().getDisplayName()).append(Component.text("> ").append(msg.text())));
                assertTextRenders("<Bob> Hi");
            }
        }
    }

    @Nested class given_two_messages {

        @Test
        void renders_both_messages() {
            addMessage("Hey");
            addMessageWithSource("Silthus", "Yo");
            assertTextRenders("""
            Hey
            Silthus: Yo"""
            );
        }
    }

    @Nested class given_single_channel {

        private Channel channel;

        @BeforeEach
        void setUp() {
            channel = createChannel("test");
            chatter.join(channel);
        }

        @Test
        void renders_channel_name() {
            assertTextRenders("| test |");
        }

        @Nested class when_it_is_active {

            @BeforeEach
            void setUp() {
                chatter.setActiveChannel(channel);
            }

            @Test
            void underlines_channel() {
                assertViewRenders("| <green><underlined>test</green></underlined> |");
            }

            @Nested class and_different_format_is_used {
                @BeforeEach
                void setUp() {
                    view = tabbedChannels(of(chatter))
                        .set(ACTIVE_CHANNEL_FORMAT, component -> ACTIVE_CHANNEL_FORMAT.getDefaultValue().format(component).color(GREEN));
                }

                @Test
                void uses_custom_format() {
                    assertViewRenders("| <green><underlined>test</green></underlined> |");
                }
            }
        }
    }

    @Nested class given_two_channels {

        @BeforeEach
        void setUp() {
            chatter.join(createChannel("one"));
            chatter.join(createChannel("two"));
        }

        @Test
        void renders_both_seperated_by_a_divider() {
            assertTextRenders("| one | two |");
        }

        @Nested class with_different_priorities {
            @BeforeEach
            void setUp() {
                chatter.join(channelWith("zzz", PRIORITY, 1));
                chatter.join(createChannel("test"));
            }

            @Test
            void renders_higher_priority_channel_first() {
                assertTextRenders("| zzz | one | test | two |");
            }
        }

        @Nested class with_custom_channel_join_config_format {

            @BeforeEach
            void setUp() {
                view = tabbedChannels(of(chatter))
                    .set(CHANNEL_JOIN_CONFIG, JoinConfiguration.builder().separator(Component.text(" - ")).build());
            }

            @Test
            void uses_custom_format() {
                assertTextRenders("one - two");
            }
        }
    }

    @Nested class given_messages_and_channels {
        @BeforeEach
        void setUp() {
            chatter.join(createChannel("aaa"));
            chatter.setActiveChannel(channelWith("zzz", set(PRIORITY, 10)));
            addMessage("No Source!");
            addMessageWithSource("Player", "Hey");
            addMessageWithSource("Player2", "Hello");
        }

        @Test
        void renders_full_view() {
            assertViewRenders("""
                No Source!
                Player: Hey
                Player2: Hello
                | <green><underlined>zzz</green></underlined> | <gray>aaa</gray> |""");
        }
    }

    @Nested class given_rendered_view {
        private Component render;

        @BeforeEach
        void setUp() {
            render = view.render();
        }

        @Test
        void isRenderedView_returns_true() {
            assertThat(view.isRenderedView(render)).isTrue();
        }
    }
}
