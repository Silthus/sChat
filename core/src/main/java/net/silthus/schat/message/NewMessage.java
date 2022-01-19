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

package net.silthus.schat.message;

import java.util.UUID;
import lombok.NonNull;
import net.kyori.adventure.text.Component;
import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.identity.Identity;
import org.jetbrains.annotations.NotNull;

public interface NewMessage {

    static @NotNull Draft message() {
        return NewMessageImpl.builder();
    }

    static @NotNull Draft message(@NonNull Component text) {
        return message().text(text);
    }

    @NotNull UUID id();

    @NotNull Identity target();

    @NotNull Component text();

    interface Draft extends NewMessage {

        @NotNull Draft id(@NonNull UUID id);

        @NotNull Draft target(@NonNull Identity target);

        @NotNull Draft to(@NonNull Chatter chatter);

        @NotNull Draft text(@NonNull Component text);

        @NotNull NewMessage send(@NonNull Messenger messenger);
    }
}
