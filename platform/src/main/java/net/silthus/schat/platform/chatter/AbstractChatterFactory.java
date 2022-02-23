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

import java.util.UUID;
import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.chatter.ChatterFactory;
import net.silthus.schat.identity.Identity;
import net.silthus.schat.ui.ViewConnector;
import net.silthus.schat.ui.ViewProvider;
import org.jetbrains.annotations.NotNull;

import static net.silthus.schat.chatter.Chatter.chatterBuilder;

public abstract class AbstractChatterFactory implements ChatterFactory {
    protected final ViewProvider viewProvider;

    public AbstractChatterFactory(ViewProvider viewProvider) {
        this.viewProvider = viewProvider;
    }

    @Override
    public final Chatter createChatter(UUID id) {
        return chatterBuilder(createIdentity(id))
            .viewConnector(createViewConnector(id))
            .permissionHandler(createPermissionHandler(id))
            .create();
    }

    @NotNull
    protected abstract Identity createIdentity(UUID id);

    protected abstract Chatter.PermissionHandler createPermissionHandler(UUID id);

    protected abstract ViewConnector.Factory createViewConnector(UUID id);
}