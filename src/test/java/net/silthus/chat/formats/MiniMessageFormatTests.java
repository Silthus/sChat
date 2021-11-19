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

package net.silthus.chat.formats;

import net.kyori.adventure.text.Component;
import net.silthus.chat.*;
import net.silthus.chat.conversations.Channel;
import org.bukkit.configuration.MemoryConfiguration;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.*;
import static org.assertj.core.api.Assertions.assertThat;

public class MiniMessageFormatTests extends TestBase {

    @Test
    void create() {
        assertThat(toText("<message>", Message.message("test").build()))
                .isEqualTo("test");
    }

    @Test
    void withColor() {
        assertThat(toText("<green><message>", Message.message("test").build()))
                .isEqualTo("&atest");
    }

    @Test
    void withSource() {
        assertThat(toText("<sender_name>: <message>", Message.message(ChatSource.player(server.addPlayer()), "test").build()))
                .isEqualTo("Player0: test");
    }

    @Test
    void withNullSource() {
        assertThat(toText("<sender_name>: <message>", Message.message("test").build()))
                .isEqualTo("N/A: test");
    }

    @Test
    void withChannelName() {
        Message message = Message.message(ChatSource.player(server.addPlayer()), "test")
                .to(ChatTarget.channel("test channel")).build();

        assertThat(toText("[<channel_name>]<sender_name>: <message>", message))
                .isEqualTo("[test channel]Player0: test");
    }

    @Test
    void withVaultPrefix() {
        final Message message = Message.message(ChatSource.player(server.addPlayer()), "test").build();
        final Component result = Formats.miniMessage("<sender_vault_prefix><sender_name>: <message>").applyTo(message);

        assertComponents(result, text("").append(text("[ADMIN]", GRAY)).append(text("Player0: test", GREEN)));
    }

    @Test
    void withVaultSuffix() {
        final Message message = Message.message(ChatSource.player(server.addPlayer()), "test").build();
        final Component result = Formats.miniMessage("<sender_name><sender_vault_suffix>: <message>").applyTo(message);
        assertComponents(result, text("Player0[!]").append(text(": test", GREEN)));
    }

    @Test
    void withLoadFromConfig() {
        final MemoryConfiguration cfg = new MemoryConfiguration();
        cfg.set("format", "<message>");
        final Optional<Format> format = Formats.format("mini-message", cfg);
        assertThat(format).isPresent().get()
                .extracting("format").isEqualTo("<message>");
    }

    @Test
    @Disabled
    void withNestedTemplates_resolvesAllTemplates() {
        final Format format = plugin.getPluginConfig().defaults().channel().format();
        final Channel channel = createChannel("test");
        final Chatter chatter = Chatter.player(server.addPlayer());
        final Message message = chatter.message("Hi!").to(channel).build();

        final Component result = format.applyTo(message);
        assertComponents(result, text("[", GOLD).append(text("test", GREEN)).append(text("]", GOLD)
                .append(text("[ADMIN]", GRAY).append(text("Player0[!]: Hi", GREEN)))));
    }

    private String toText(String format, Message message) {
        return toText(Formats.miniMessage(format).applyTo(message));
    }
}