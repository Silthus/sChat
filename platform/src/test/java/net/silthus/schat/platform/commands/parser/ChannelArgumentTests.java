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

package net.silthus.schat.platform.commands.parser;

import cloud.commandframework.exceptions.parsing.NoInputProvidedException;
import net.silthus.schat.channel.Channel;
import net.silthus.schat.channel.ChannelRepository;
import net.silthus.schat.chatter.ChatterRepository;
import net.silthus.schat.platform.commands.ParserTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static net.silthus.schat.channel.Channel.createChannel;
import static net.silthus.schat.channel.ChannelRepository.createInMemoryChannelRepository;
import static net.silthus.schat.chatter.ChatterRepository.createInMemoryChatterRepository;
import static net.silthus.schat.platform.commands.parser.ChannelArgument.ARGUMENT_PARSE_FAILURE_CHANNEL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class ChannelArgumentTests extends ParserTest<Channel> {

    private ChannelRepository channelRepository;

    @BeforeEach
    void setUp() {
        channelRepository = createInMemoryChannelRepository();
        setParser(new ChannelArgument(channelRepository, createInMemoryChatterRepository()));
    }

    private void registerChannelParser() {
        ChannelArgument.registerChannelArgument(getCommandManager(), mock(ChannelRepository.class), mock(ChatterRepository.class));
    }

    @Test
    void given_no_input_then_NoInputProvidedException_is_throw() {
        assertParseFailure(NoInputProvidedException.class);
    }

    @Test
    void given_empty_string_then_NoInputProvidedException_is_thrown() {
        assertParseFailure(NoInputProvidedException.class, "  ");
    }

    @Test
    void given_unknown_channel_then_ChannelNotFound_is_thrown() {
        assertParseFailure(ChannelArgument.ChannelParseException.class, "foobar");
    }

    @Nested class given_channel_in_repository {
        private Channel channel;

        @BeforeEach
        void setUp() {
            channel = createChannel("test");
            channelRepository.add(channel);
        }

        @Test
        void then_parse_returns_channel() {
            assertParseSuccessful("test", channel);
        }
    }
    
    @Nested class registerChannelArgument {

        @Test
        void when_ChannelParser_is_registered_then_captions_are_registered() {
            registerChannelParser();
            assertThat(getCaption(ARGUMENT_PARSE_FAILURE_CHANNEL)).isNotNull();
        }

        @Test
        void when_ChannelParser_is_registered_then_parser_is_registered() {
            registerChannelParser();
            assertThat(getParser(Channel.class)).isPresent();
        }
    }
}
