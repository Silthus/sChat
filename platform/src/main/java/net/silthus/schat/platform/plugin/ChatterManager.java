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

package net.silthus.schat.platform.plugin;

import java.util.Collection;
import java.util.UUID;
import lombok.Getter;
import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.chatter.ChatterRepository;
import net.silthus.schat.chatter.Chatters;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

final class ChatterManager implements Chatters {

    @Getter
    private final ChatterRepository repository;

    ChatterManager(ChatterRepository repository) {
        this.repository = repository;
    }

    @Override
    public @NotNull @Unmodifiable Collection<Chatter> all() {
        return getRepository().all();
    }

    @Override
    public boolean contains(UUID key) {
        return getRepository().contains(key);
    }

    @Override
    public @NotNull Chatter get(@NotNull UUID id) throws NotFound {
        return getRepository().get(id);
    }

    @Override
    public void add(@NotNull Chatter chatter) {
        getRepository().add(chatter);
    }

    @Override
    public void remove(@NotNull UUID key) {
        getRepository().remove(key);
    }
}
