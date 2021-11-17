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

package net.silthus.chat.scopes;

import net.silthus.chat.ChatTarget;
import net.silthus.chat.Message;
import net.silthus.chat.TestBase;
import net.silthus.chat.conversations.Channel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public final class GlobalScopeTests extends TestBase {

    private GlobalScope scope;

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();

        scope = new GlobalScope();
    }

    @Test
    void doesNotFilterTargets() {
        Channel channel = createChannel("test");
        channel.addTarget(ChatTarget.console());
        assertThat(scope.filterTargets(channel, Message.message("hi").build())).contains(ChatTarget.console());
    }

    @Test
    void addsBungeeCordTarget() {
        Channel channel = createChannel("test", config -> config.scope(scope));
        assertThat(channel.getTargets()).contains(plugin.getBungeecord());
    }
}