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

package net.silthus.schat.util.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import java.lang.reflect.Type;
import lombok.NonNull;
import net.silthus.schat.util.gson.types.ComponentSerializer;
import net.silthus.schat.util.gson.types.IdentitySerializer;
import net.silthus.schat.util.gson.types.InstantSerializer;
import net.silthus.schat.util.gson.types.MessageSerializer;
import net.silthus.schat.util.gson.types.SettingsSerializer;

import static net.silthus.schat.util.gson.types.ComponentSerializer.COMPONENT_TYPE;
import static net.silthus.schat.util.gson.types.IdentitySerializer.IDENTITY_TYPE;
import static net.silthus.schat.util.gson.types.InstantSerializer.INSTANT_TYPE;
import static net.silthus.schat.util.gson.types.MessageSerializer.MESSAGE_TYPE;
import static net.silthus.schat.util.gson.types.SettingsSerializer.SETTINGS_TYPE;

public final class GsonProvider {

    public static GsonProvider createGsonProvider() {
        return new GsonProvider();
    }

    private final GsonBuilder base = new GsonBuilder()
        .disableHtmlEscaping()
        .registerTypeAdapter(INSTANT_TYPE, new InstantSerializer())
        .registerTypeAdapter(COMPONENT_TYPE, new ComponentSerializer())
        .registerTypeAdapter(MESSAGE_TYPE, new MessageSerializer())
        .registerTypeAdapter(IDENTITY_TYPE, new IdentitySerializer())
        .registerTypeAdapter(SETTINGS_TYPE, new SettingsSerializer());
    private final GsonBuilder prettyPrinting = base.setPrettyPrinting();
    private final JsonParser normalParser = new JsonParser();

    public Gson normalGson() {
        return base.create();
    }

    public Gson prettyGson() {
        return prettyPrinting.create();
    }

    public JsonParser gsonParser() {
        return normalParser;
    }

    public void registerTypeAdapter(Type type, @NonNull Object adapter) {
        base.registerTypeAdapter(type, adapter);
    }

    private GsonProvider() {
    }
}
