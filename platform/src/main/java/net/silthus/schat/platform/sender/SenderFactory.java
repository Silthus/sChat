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

package net.silthus.schat.platform.sender;

import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.chatter.MessageHandler;
import net.silthus.schat.chatter.PermissionHandler;
import net.silthus.schat.chatter.PlayerAdapter;
import net.silthus.schat.identity.Identity;

public abstract class SenderFactory<T> implements PlayerAdapter<T> {

    @Override
    public final Chatter adapt(T player) {
        return createCommandSender(player);
    }

    public final ChatterSender createCommandSender(T player) {
        return new ChatterSender(getIdentity(player),
            getPermissionHandler(player),
            getMessageHandler(player)
        );
    }

    public final void checkPlayerType(Class<?> playerType) {
        if (!getType().isAssignableFrom(playerType))
            throw new InvalidPlayerType();
    }

    protected abstract Class<T> getType();

    protected abstract Identity getIdentity(T player);

    protected abstract PermissionHandler getPermissionHandler(T player);

    protected abstract MessageHandler getMessageHandler(T player);

    public static class InvalidPlayerType extends RuntimeException {
    }
}