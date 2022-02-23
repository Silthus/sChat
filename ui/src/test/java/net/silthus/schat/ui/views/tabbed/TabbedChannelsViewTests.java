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
package net.silthus.schat.ui.views.tabbed;

import lombok.SneakyThrows;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.flattener.ComponentFlattener;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.silthus.schat.channel.Channel;
import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.chatter.ChatterMock;
import net.silthus.schat.commands.CreatePrivateChannelCommand;
import net.silthus.schat.commands.SendMessageCommand;
import net.silthus.schat.eventbus.EventBus;
import net.silthus.schat.identity.Identity;
import net.silthus.schat.message.Message;
import net.silthus.schat.ui.View;
import net.silthus.schat.ui.ViewConfig;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static net.kyori.adventure.text.Component.empty;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.RED;
import static net.kyori.adventure.text.format.TextDecoration.UNDERLINED;
import static net.silthus.schat.AssertionHelper.assertNPE;
import static net.silthus.schat.channel.Channel.DISPLAY_NAME;
import static net.silthus.schat.channel.Channel.createChannel;
import static net.silthus.schat.channel.ChannelHelper.ConfiguredSetting.set;
import static net.silthus.schat.channel.ChannelHelper.channelWith;
import static net.silthus.schat.channel.ChannelHelper.randomChannel;
import static net.silthus.schat.channel.ChannelRepository.createInMemoryChannelRepository;
import static net.silthus.schat.channel.ChannelSettings.FORCED;
import static net.silthus.schat.channel.ChannelSettings.PRIORITY;
import static net.silthus.schat.chatter.ChatterMock.chatterMock;
import static net.silthus.schat.commands.CreatePrivateChannelCommand.createPrivateChannel;
import static net.silthus.schat.commands.SendPrivateMessageCommand.sendPrivateMessage;
import static net.silthus.schat.identity.Identity.identity;
import static net.silthus.schat.message.Message.FORMATTED;
import static net.silthus.schat.message.Message.message;
import static net.silthus.schat.message.MessageHelper.randomMessage;
import static net.silthus.schat.ui.format.Format.ACTIVE_TAB_FORMAT;
import static net.silthus.schat.ui.format.Format.MESSAGE_FORMAT;
import static net.silthus.schat.ui.views.Views.tabbedChannels;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class TabbedChannelsViewTests {

    private static final @NotNull MiniMessage COMPONENT_SERIALIZER = MiniMessage.miniMessage();
    private static final @NotNull PlainTextComponentSerializer PLAIN_TEXT_SERIALIZER = PlainTextComponentSerializer.plainText()
        .toBuilder().flattener(ComponentFlattener.textOnly()).build();
    private Chatter chatter;
    private View view;

    @BeforeEach
    void setUp() {
        chatter = chatterMock(Identity.identity("Player"));
        view = tabbedChannels(chatter, new ViewConfig());
        CreatePrivateChannelCommand.prototype(builder -> builder.channelRepository(createInMemoryChannelRepository()));
        SendMessageCommand.prototype(builder -> builder.eventBus(EventBus.empty()));
    }

    @NotNull
    private String msgText(Message message) {
        return COMPONENT_SERIALIZER.serialize(message.text());
    }

    private void sendMessage(String text) {
        sendMessage(message(text).create());
    }

    @SneakyThrows
    @NotNull
    private Message sendMessage(Message message) {
        chatter.sendMessage(message);
        Thread.sleep(1L); // required to order messages by time
        return message;
    }

    private void sendMessageWithSource(String source, String text) {
        sendMessage(message(text).source(identity(source)).create());
    }

    private void assertTextRenders(String expected) {
        assertEquals(expected, PLAIN_TEXT_SERIALIZER.serialize(view.render()).trim());
    }

    private void assertTextContains(String... expected) {
        assertThat(PLAIN_TEXT_SERIALIZER.serialize(view.render()).trim())
            .contains(expected);
    }

    private void assertTextDoesNotContain(String... unexpected) {
        assertThat(PLAIN_TEXT_SERIALIZER.serialize(view.render()).trim())
            .doesNotContain(unexpected);
    }

    private void assertViewRenders(String expected) {
        assertEquals(expected, COMPONENT_SERIALIZER.serialize(view.render()).trim());
    }

    private void assertViewDoesNotContain(String... unexpected) {
        assertThat(COMPONENT_SERIALIZER.serialize(view.render()).trim()).doesNotContain(unexpected);
    }

    private void assertViewContains(String... expected) {
        assertThat(COMPONENT_SERIALIZER.serialize(view.render()).trim()).contains(expected);
    }

    @Nested
    class given_null_chatter {

        @Test
        @SuppressWarnings("ConstantConditions")
        void throws_npe() {
            assertNPE(() -> tabbedChannels(null, new ViewConfig()));
        }
    }

    @Nested
    class given_single_message {

        @Test
        void renders_message_text() {
            final Message message = sendMessage(randomMessage());
            assertTextContains(msgText(message));
        }
    }

    @Nested
    class given_single_message_with_source {

        @BeforeEach
        void setUp() {
            sendMessageWithSource("Bob", "Hi");
        }

        @Test
        void renders_source_name_with_message_text() {
            assertTextContains("Bob: Hi");
        }

        @Nested
        class and_custom_message_source_format {

            @Test
            void uses_format() {
                final ViewConfig config = new ViewConfig();
                config.format().set(MESSAGE_FORMAT, (view, msg) ->
                    text("<")
                        .append(msg.getOrDefault(Message.SOURCE, Identity.nil()).displayName())
                        .append(text("> "))
                        .append(msg.getOrDefault(Message.TEXT, Component.empty())));
                view = tabbedChannels(chatter, config);
                assertTextContains("<Bob> Hi");
            }
        }
    }

    @Nested
    class given_two_messages {

        @Test
        void renders_both_messages() {
            sendMessage("Hey");
            sendMessageWithSource("Silthus", "Yo");
            assertViewRenders("""
                Hey
                <yellow>Silthus</yellow><gray>: Yo</gray>
                | <red><lang:schat.view.no-channels></red> |"""
            );
        }
    }

    @Nested
    class given_message_with_formatted_setting {
        @BeforeEach
        void setUp() {
            final Message message = randomMessage();
            message.set(FORMATTED, text("FORMATTED"));
            sendMessage(message);
        }

        @Test
        void renders_formatted_message() {
            assertViewContains("FORMATTED");
        }
    }

    @Nested
    class given_single_channel {
        private Channel channel;

        @BeforeEach
        void setUp() {
            channel = createChannel("test");
            chatter.join(channel);
        }

        @Test
        void renders_channel_name() {
            assertTextRenders("| ❌test |");
        }

        @Nested
        class when_it_is_active {
            @BeforeEach
            void setUp() {
                chatter.activeChannel(channel);
            }

            @Test
            void underlines_channel() {
                assertViewRenders("| <red><hover:show_text:\"<lang:schat.hover.leave-channel:\\\"<gray>test\\\">\"><click:run_command:\"/channel leave test\">❌</red><green><underlined>test</click></hover></underlined></green> |");
            }

            @Nested
            class and_different_format_is_used {
                @BeforeEach
                void setUp() {
                    view = tabbedChannels(chatter, new ViewConfig());
                    channel.set(ACTIVE_TAB_FORMAT, (view, type) -> type.getOrDefault(DISPLAY_NAME, empty())
                        .color(RED)
                        .decorate(UNDERLINED));
                }

                @Test
                void uses_custom_format() {
                    assertViewRenders("| <red><hover:show_text:\"<lang:schat.hover.leave-channel:\\\"<gray>test\\\">\"><click:run_command:\"/channel leave test\">❌<underlined>test</click></hover></underlined></red> |");
                }
            }
        }

        @Nested
        class when_it_is_inactive {
            @BeforeEach
            void setUp() {
                chatter.activeChannel(randomChannel());
            }

            @Test
            void then_channel_click_executes_join_command() {
                assertViewContains("<click:run_command:\"/channel join test\">test</click>");
            }
        }

        @Nested
        class given_it_is_forced {
            @BeforeEach
            void setUp() {
                channel.set(FORCED, true);
            }

            @Test
            void does_not_render_leave_symbol() {
                assertTextRenders("| test |");
            }
        }
    }

    @Nested
    class given_private_channel {
        private ChatterMock target;

        @BeforeEach
        void setUp() {
            target = chatterMock(Identity.identity("target"));
            chatter.activeChannel(createPrivateChannel(chatter, target).channel());
        }

        @Test
        void renders_partner_name() {
            assertTextRenders("| ❌target |");
        }

        @Test
        void does_not_display_system_messages() {
            sendMessage("System");
            assertTextRenders("| ❌target |");
        }

        @Test
        void displays_private_messages() throws InterruptedException {
            sendPrivateMessage(chatter, target, text("Hi"));
            Thread.sleep(1L);
            sendPrivateMessage(target, chatter, text("Hey back"));
            assertViewContains("""
                <yellow><lang:schat.chat.message.you></yellow><gray>: Hi</gray>
                <aqua>target</aqua><gray>: Hey back</gray>""");
        }
    }

    @Nested
    class given_two_channels {

        private @NotNull Channel channelOne;
        private @NotNull Channel channelTwo;

        @BeforeEach
        void setUp() {
            channelOne = createChannel("one");
            channelTwo = createChannel("two");
            chatter.join(channelOne);
            chatter.join(channelTwo);
        }

        @Test
        void renders_both_seperated_by_a_divider() {
            assertTextRenders("| ❌one | ❌two |");
        }

        @Nested
        class given_both_channels_received_messages {
            @BeforeEach
            void setUp() {
                sendMessage("System");
                message("one").source(identity("Bob")).to(channelOne).type(Message.Type.CHAT).send();
                message("two").source(identity("Bob")).to(channelTwo).type(Message.Type.CHAT).send();
            }

            @Test
            void when_no_channel_is_active_then_only_system_messages_are_displayed() {
                assertTextContains("System");
                assertTextDoesNotContain("Bob: one", "Bob: two");
            }

            @Nested
            class given_channel_one_is_active {
                @BeforeEach
                void setUp() {
                    chatter.activeChannel(channelOne);
                }

                @Test
                void then_message_one_is_displayed() {
                    assertTextRenders("""
                        System
                        Bob: one
                        | ❌one | ❌two |""");
                }

                @Test
                void then_message_two_is_not_displayed() {
                    assertTextDoesNotContain("Bob: two");
                }
            }
        }

        @Nested
        class with_different_priorities {
            @BeforeEach
            void setUp() {
                chatter.join(channelWith("zzz", PRIORITY, 1));
                chatter.join(createChannel("test"));
            }

            @Test
            void renders_higher_priority_channel_first() {
                assertTextRenders("| ❌zzz | ❌one | ❌test | ❌two |");
            }

            @Nested
            class and_private_channel {
                @BeforeEach
                void setUp() {
                    createPrivateChannel(chatter, chatterMock(Identity.identity("target")));
                }

                @Test
                void renders_private_channel_last() {
                    assertTextRenders("| ❌zzz | ❌one | ❌test | ❌two | ❌target |");
                }
            }
        }

        @Nested
        class with_custom_channel_join_config_format {

            @BeforeEach
            void setUp() {
                final ViewConfig config = new ViewConfig();
                config.channelJoinConfig(JoinConfiguration.builder().separator(text(" - ")).build());
                view = tabbedChannels(chatter, config);
            }

            @Test
            void uses_custom_format() {
                assertTextRenders("❌one - ❌two");
            }
        }
    }

    @Nested
    class given_messages_and_channels {
        @BeforeEach
        void setUp() {
            chatter.join(createChannel("aaa"));
            chatter.activeChannel(channelWith(
                    "zzz",
                    set(PRIORITY, 10),
                    set(MESSAGE_FORMAT, (v, message) -> message.get(Message.SOURCE)
                        .orElse(Identity.nil())
                        .displayName()
                        .append(text(": ").append(message.getOrDefault(Message.TEXT, empty()))))
                )
            );
            sendMessage("No Source!");
            sendMessageWithSource("Player", "Hey");
            sendMessageWithSource("Player2", "Hello");
        }

        @Test
        void renders_full_view() {
            assertViewRenders("""
                No Source!
                Player: Hey
                Player2: Hello
                | <red><hover:show_text:"<lang:schat.hover.leave-channel:\\"<gray>zzz\\">"><click:run_command:"/channel leave zzz">❌</red><green><underlined>zzz</click></hover></underlined></green> | <red><hover:show_text:"<lang:schat.hover.leave-channel:\\"<gray>aaa\\">"><click:run_command:"/channel leave aaa">❌</click></red><gray><click:run_command:"/channel join aaa">aaa</click></hover></gray> |""");
        }
    }

    @Nested
    class given_rendered_view {
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