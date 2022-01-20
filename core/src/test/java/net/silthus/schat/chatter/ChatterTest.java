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

package net.silthus.schat.chatter;

import net.silthus.schat.channel.Channel;
import net.silthus.schat.identity.Identified;
import net.silthus.schat.identity.Identity;
import net.silthus.schat.message.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static net.silthus.schat.AssertionHelper.assertNPE;
import static net.silthus.schat.IdentityHelper.randomIdentity;
import static net.silthus.schat.MessageHelper.randomMessage;
import static net.silthus.schat.channel.ChannelHelper.randomChannel;
import static net.silthus.schat.chatter.Chatter.chatter;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

class ChatterTest {
    private Chatter chatter;
    private Identity identity;

    @BeforeEach
    void setUp() {
        identity = randomIdentity();
        chatter = Chatter.createChatter(identity);
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    void given_null_identity_then_create_throws() {
        assertNPE(() -> Chatter.createChatter(null));
    }

    @Test
    void given_valid_identity_uses_identity_properties() {
        assertThat(chatter).extracting(
            Identified::getUniqueId,
            Identified::getName,
            Identified::getDisplayName
        ).contains(
            identity.getUniqueId(),
            identity.getName(),
            identity.getDisplayName()
        );
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    void when_join_is_called_given_null_throws_npe() {
        assertNPE(() -> chatter.join(null));
    }

    @Test
    void when_isJoined_is_called_given_null_returns_false() {
        assertThat(chatter.isJoined(null)).isFalse();
    }

    @Nested class given_valid_channel {
        private Channel channel;

        @BeforeEach
        void setUp() {
            channel = randomChannel();
        }

        private void setActiveChannel(Channel channel) {
            chatter.setActiveChannel(channel);
        }

        private void assertChannelTargetsContains(Chatter chatter) {
            assertThat(channel.getTargets()).containsOnlyOnce(chatter);
        }

        private void assertChatterHasChannel(Channel channel) {
            assertThat(chatter.getChannels()).containsOnlyOnce(channel);
        }

        private void assertJoinSuccess() {
            assertChannelTargetsContains(chatter);
            assertChatterHasChannel(channel);
        }

        @Nested class then_setActiveChannel {
            @Test
            void sets_active_channel() {
                setActiveChannel(channel);
                assertThat(chatter.getActiveChannel())
                    .isPresent().get().isEqualTo(channel);
            }

            @Test
            void given_null_channel_clears_active_channel() {
                setActiveChannel(channel);
                setActiveChannel(null);
                assertThat(chatter.getActiveChannel()).isNotPresent();
            }

            @Nested class given_chatter_without_channel {
                @Test
                void joins_channel() {
                    setActiveChannel(channel);
                    assertJoinSuccess();
                }
            }
        }

        @Nested class when_channel_set_as_active_channel {
            @BeforeEach
            void setUp() {
                setActiveChannel(channel);
            }

            @Test
            void then_isActiveChannel_returns_true() {
                assertThat(chatter.isActiveChannel(channel)).isTrue();
            }
        }

        @Test
        void when_channel_is_not_active_channel_then_isActiveChannel_returns_false() {
            assertThat(chatter.isActiveChannel(channel)).isFalse();
        }

        @Nested class when_join_is_called {
            private void joinChannel() {
                chatter.join(channel);
            }

            @Test
            void then_adds_chatter_to_channel() {
                joinChannel();
                assertChannelTargetsContains(chatter);
            }

            @Test
            void then_adds_channel_to_chatter() {
                joinChannel();
                assertChatterHasChannel(channel);
            }

            @Test
            void twice_then_only_adds_channel_and_chatter_once() {
                joinChannel();
                joinChannel();
                assertJoinSuccess();
            }

            @Test
            void then_isJoined_returns_true() {
                joinChannel();
                assertThat(chatter.isJoined(channel)).isTrue();
            }
        }

        @Test
        void given_chatter_has_not_joined_channel_then_isJoined_returns_false() {
            assertThat(chatter.isJoined(randomChannel())).isFalse();
        }
    }

    @Nested class sendMessage {

        private void sendRandomMessage() {
            chatter.sendMessage(randomMessage());
        }

        @Test
        @SuppressWarnings("ConstantConditions")
        void given_null_message_then_throws_npe() {
            assertNPE(() -> chatter.sendMessage(null));
        }

        @Test
        void given_no_message_handler_then_does_not_throw() {
            assertThatCode(this::sendRandomMessage)
                .doesNotThrowAnyException();
        }

        @Test
        void then_message_is_added() {
            final Message message = randomMessage();
            chatter.sendMessage(message);
            assertThat(chatter.getMessages()).contains(message);
        }

        @Nested class given_valid_message_handler {
            private boolean messageHandlerCalled = false;

            @BeforeEach
            void setUp() {
                chatter = chatter(randomIdentity())
                    .messageHandler(message -> messageHandlerCalled = true)
                    .create();
            }

            @Test
            void then_message_handler_is_called() {
                sendRandomMessage();
                assertThat(messageHandlerCalled).isTrue();
            }
        }
    }

    @Nested class hasPermission {
        @Test
        void given_null_returns_false() {
            assertThat(chatter.hasPermission(null)).isFalse();
        }

        @Test
        void given_no_permission_handler_does_not_throw() {
            assertThatCode(() -> chatter.hasPermission("abc"))
                .doesNotThrowAnyException();
        }

        @Nested class given_permission_handler {

            private boolean permissionHandlerCalled = false;

            @BeforeEach
            void setUp() {
                chatter = chatter(randomIdentity()).permissionHandler(permission -> {
                    permissionHandlerCalled = true;
                    return false;
                }).create();
            }

            @Test
            void then_handler_is_called() {
                chatter.hasPermission("test");
                assertThat(permissionHandlerCalled).isTrue();
            }
        }
    }
}
