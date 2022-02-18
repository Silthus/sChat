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
package net.silthus.schat.features;

import net.silthus.schat.channel.Channel;
import net.silthus.schat.channel.ChannelRepository;
import net.silthus.schat.chatter.ChatterMock;
import net.silthus.schat.commands.JoinChannelCommand;
import net.silthus.schat.eventbus.EventBus;
import net.silthus.schat.eventbus.EventBusMock;
import net.silthus.schat.events.chatter.ChatterJoinedServerEvent;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static net.silthus.schat.channel.ChannelHelper.ConfiguredSetting.set;
import static net.silthus.schat.channel.ChannelHelper.channelWith;
import static net.silthus.schat.channel.ChannelHelper.randomChannel;
import static net.silthus.schat.channel.ChannelRepository.createInMemoryChannelRepository;
import static net.silthus.schat.channel.ChannelSettings.AUTO_JOIN;
import static net.silthus.schat.channel.ChannelSettings.PROTECTED;
import static net.silthus.schat.chatter.ChatterMock.randomChatter;
import static org.assertj.core.api.Assertions.assertThat;

class AutoJoinChannelsFeatureTest {

    private EventBusMock events;
    private Channel channel;
    private @NotNull ChatterMock chatter;
    private ChannelRepository channelRepository;

    @BeforeEach
    void setUp() {
        channelRepository = createInMemoryChannelRepository();
        AutoJoinChannelsFeature feature = new AutoJoinChannelsFeature(channelRepository);
        events = EventBusMock.eventBusMock();
        feature.bind(events);
        channel = channelWith(AUTO_JOIN, true);
        channelRepository.add(channel);
        chatter = randomChatter();
        JoinChannelCommand.prototype(builder -> builder.eventBus(EventBus.empty()));
    }

    private void triggerJoinEvent() {
        events.post(new ChatterJoinedServerEvent(chatter));
    }

    @Test
    void onJoin_auto_joins_channels() {
        triggerJoinEvent();
        chatter.assertJoinedChannel(channel);
    }

    @Test
    void does_not_join_unconfigured_channels() {
        final Channel channel = randomChannel();
        channelRepository.add(channel);
        triggerJoinEvent();
        chatter.assertNotJoinedChannel(channel);
    }

    @Test
    void does_not_join_protected_channels() {
        final Channel channel = channelWith(set(PROTECTED, true), set(AUTO_JOIN, true));
        channelRepository.add(channel);
        triggerJoinEvent();
        chatter.assertNotJoinedChannel(channel);
    }

    @Test
    void given_no_active_channel_sets_joined_channel_as_active() {
        final Channel channel = randomChannel();
        channelRepository.add(channel);
        triggerJoinEvent();
        assertThat(chatter.activeChannel().isPresent()).isTrue();
    }
}
