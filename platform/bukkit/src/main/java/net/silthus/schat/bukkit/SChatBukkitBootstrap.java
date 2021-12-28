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

package net.silthus.schat.bukkit;

import java.io.File;
import kr.entree.spigradle.annotations.PluginMain;
import lombok.Getter;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;
import org.jetbrains.annotations.NotNull;

@PluginMain
public final class SChatBukkitBootstrap extends JavaPlugin {

    @Getter
    private final SChatBukkitPlugin plugin;
    @Getter
    private final BukkitPlayerAdapter playerAdapter = new BukkitPlayerAdapter();

    public SChatBukkitBootstrap() {
        plugin = new SChatBukkitPlugin(this);
    }

    // testing constructor
    public SChatBukkitBootstrap(@NotNull JavaPluginLoader loader,
                                @NotNull PluginDescriptionFile description,
                                @NotNull File dataFolder,
                                @NotNull File file) {
        super(loader, description, dataFolder, file);
        plugin = new SChatBukkitPlugin(this);
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();

        plugin.enable();
    }
}
