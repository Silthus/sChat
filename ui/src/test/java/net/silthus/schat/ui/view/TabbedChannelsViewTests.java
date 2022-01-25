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

import lombok.SneakyThrows;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.minimessage.MiniMessage;
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
import static net.silthus.schat.view.View.MESSAGE_SOURCE_FORMAT;
import static net.silthus.schat.view.View.VIEW_HEIGHT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class TabbedChannelsViewTests {

    private static final @NotNull MiniMessage COMPONENT_SERIALIZER = MiniMessage.miniMessage();
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
            assertViewRenders(text(message));
        }
    }

    @Nested class given_single_message_with_source {

        @BeforeEach
        void setUp() {
            addMessageWithSource("Bob", "Hi");
        }

        @Test
        void renders_source_name_with_message_text() {
            assertViewRenders("Bob: Hi");
        }

        @Nested class and_custom_message_source_format {

            @Test
            void uses_format() {
                view = tabbedChannels(of(chatter))
                    .set(MESSAGE_SOURCE_FORMAT, component -> Component.text("<").append(component).append(Component.text("> ")));
                assertViewRenders("<Bob> Hi");
            }
        }
    }

    @Nested class given_two_messages {

        @Test
        void renders_both_messages() {
            addMessage("Hey");
            addMessageWithSource("Silthus", "Yo");
            assertViewRenders("""
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
            assertViewRenders("| test |");
        }

        @Nested class when_it_is_active {

            @BeforeEach
            void setUp() {
                chatter.setActiveChannel(channel);
            }

            @Test
            void underlines_channel() {
                assertViewRenders("| <underlined>test</underlined> |");
            }

            @Nested class and_different_format_is_used {
                @BeforeEach
                void setUp() {
                    view = tabbedChannels(of(chatter))
                        .set(ACTIVE_CHANNEL_FORMAT, component -> ACTIVE_CHANNEL_FORMAT.getDefaultValue().format(component).color(GREEN));
                }

                @Test
                void uses_custom_format() {
                    assertViewRenders("| <green><underlined>test</underlined></green> |");
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
            assertViewRenders("| <click:run_command:\"/channel join one\">one</click> | <click:run_command:\"/channel join two\">two</click> |");
        }

        @Nested class with_different_priorities {
            @BeforeEach
            void setUp() {
                chatter.join(channelWith("zzz", PRIORITY, 1));
                chatter.join(createChannel("test"));
            }

            @Test
            void renders_higher_priority_channel_first() {
                assertViewRenders("| zzz | one | test | two |");
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
                assertViewRenders("one - two");
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
                | <green><underlined><click:run_command:"/channel join zzz">zzz</click></underlined></green> | <click:run_command:"/channel join aaa">aaa</click> |""");
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