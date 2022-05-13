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
package net.silthus.schat.platform.chatter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;
import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.chatter.ChatterFactory;
import net.silthus.schat.identity.Identity;
import net.silthus.schat.pointer.Pointers;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import static net.silthus.schat.chatter.Chatter.chatterBuilder;

public abstract class AbstractChatterFactory implements ChatterFactory {

    private final List<BiConsumer<UUID, Pointers.Builder>> additionalPointers = new ArrayList<>();

    @Override
    public final Chatter createChatter(UUID id) {
        return chatterBuilder(createIdentity(id))
            .permissionHandler(createPermissionHandler(id))
            .messageHandler(createMessageHandler(id))
            .pointers(builder -> createPointers(id, builder))
            .create();
    }

    @NotNull
    protected abstract Identity createIdentity(UUID id);

    protected abstract Chatter.PermissionHandler createPermissionHandler(UUID id);

    protected abstract Chatter.MessageHandler createMessageHandler(UUID id);

    @Override
    public final ChatterFactory addPointer(BiConsumer<UUID, Pointers.Builder> pointer) {
        this.additionalPointers.add(pointer);
        return this;
    }

    private void createPointers(UUID id, Pointers.Builder pointers) {
        additionalPointers.forEach(consumer -> consumer.accept(id, pointers));
        buildPointers(id, pointers);
    }

    /**
     * Add platform specific pointers by overriding this method.
     *
     * @param id the id of the chatter being created
     * @param pointers the additional pointers to add to the chatter
     * @since next
     */
    @ApiStatus.OverrideOnly
    protected void buildPointers(UUID id, Pointers.Builder pointers) {
    }
}
