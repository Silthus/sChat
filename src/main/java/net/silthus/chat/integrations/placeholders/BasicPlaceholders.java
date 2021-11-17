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

package net.silthus.chat.integrations.placeholders;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.silthus.chat.Chatter;
import net.silthus.chat.identities.PlayerChatter;

public class BasicPlaceholders implements Placeholders {

    @Override
    public Component setPlaceholders(PlayerChatter chatter, Component text) {
        return text
                .replaceText(playerName(chatter))
                .replaceText(playerWorld(chatter));
    }

    private TextReplacementConfig playerName(Chatter chatter) {
        return TextReplacementConfig.builder()
                .match("<player_name>").replacement(chatter.getDisplayName()).build();
    }

    private TextReplacementConfig playerWorld(PlayerChatter chatter) {
        return TextReplacementConfig.builder()
                .match("<player_world>").replacement(chatter.getPlayer().map(player -> Component.text(player.getWorld().getName())).orElse(Component.empty()))
                .build();
    }
}
