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

package net.silthus.schat.commands;

import java.util.function.Consumer;
import java.util.function.Function;
import lombok.Getter;
import lombok.Setter;
import net.silthus.schat.channel.Channel;
import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.command.Command;
import net.silthus.schat.command.Result;

public class SetActiveChannelCommand implements Command {

    @Getter
    @Setter
    private static Function<SetActiveChannelCommand.Builder, SetActiveChannelCommand.Builder> prototype = builder -> builder;

    public static SetActiveChannelCommand.Builder setActiveChannel(Chatter chatter, Channel channel) {
        return getPrototype().apply(new Builder(chatter, channel));
    }

    private final Chatter chatter;
    private final Channel channel;
    private final JoinChannelCommand joinChannelCommand;

    protected SetActiveChannelCommand(Builder builder) {
        this.chatter = builder.chatter;
        this.channel = builder.channel;
        this.joinChannelCommand = builder.joinChannelCommand.create();
    }

    @Override
    public Result execute() throws Error {
        final Result joinChannelResult = joinChannelCommand.execute();
        if (joinChannelResult.wasSuccessful())
            return setActiveChannelAndUpdateView();
        else
            return joinChannelResult;
    }

    private Result setActiveChannelAndUpdateView() {
        chatter.setActiveChannel(channel);
        chatter.updateView();
        return Result.success();
    }

    public static class Builder implements Command.Builder<SetActiveChannelCommand> {
        private final Chatter chatter;
        private final Channel channel;
        private final JoinChannelCommand.Builder joinChannelCommand;

        public Builder(Chatter chatter, Channel channel) {
            this.chatter = chatter;
            this.channel = channel;
            this.joinChannelCommand = JoinChannelCommand.joinChannel(chatter, channel);
        }

        public Builder joinChannelCmd(Consumer<JoinChannelCommand.Builder> builder) {
            builder.accept(joinChannelCommand);
            return this;
        }

        public SetActiveChannelCommand create() {
            return new SetActiveChannelCommand(this);
        }
    }
}
