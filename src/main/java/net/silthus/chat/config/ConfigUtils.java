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

package net.silthus.chat.config;

import lombok.NonNull;
import net.silthus.chat.Format;
import net.silthus.chat.Formats;
import org.bukkit.configuration.ConfigurationSection;

public final class ConfigUtils {

    static Format getFormatFromConfig(@NonNull ConfigurationSection config, Format defaultFormat, String template, String format) {
        if (!config.isSet("format")) return defaultFormat;
        return Formats.format(config.getConfigurationSection("format"))
                .or(() -> Formats.formatFromTemplate(config.getString("format", template)))
                .orElseGet(() -> Formats.miniMessage(config.getString("format", format)));
    }
}
