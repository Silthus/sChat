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

package net.silthus.schat.channel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;
import net.kyori.adventure.text.Component;
import net.silthus.schat.channel.checks.JoinChannelPermissionCheck;
import net.silthus.schat.checks.Check;
import net.silthus.schat.checks.JoinChannel;
import net.silthus.schat.message.Message;
import net.silthus.schat.message.MessageRepository;
import net.silthus.schat.message.MessageTarget;
import net.silthus.schat.message.Messenger;
import net.silthus.schat.settings.Setting;
import net.silthus.schat.settings.Settings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import static net.kyori.adventure.text.Component.empty;
import static net.kyori.adventure.text.Component.text;
import static net.silthus.schat.message.MessageRepository.createInMemoryMessageRepository;
import static net.silthus.schat.permission.Permission.of;

@Data
@ToString(of = {"key", "settings"})
@EqualsAndHashCode(of = "key")
final class ChannelImpl implements Channel {

    private static final Pattern CHANNEL_KEY_PATTERN = Pattern.compile("^[a-z0-9_-]+$");
    private static final Messenger<Channel> DEFAULT_MESSENGER = new DefaultChannelMessenger();

    private final String key;
    private final Set<MessageTarget> targets = new HashSet<>();
    private final MessageRepository messageRepository;
    private final Messenger<Channel> messenger;
    private final Settings settings;
    private final Map<Class<? extends Check<?>>, List<? extends Check<?>>> checks;

    private ChannelImpl(ChannelImplBuilder builder) {
        this.key = builder.key;
        this.messenger = builder.messenger;
        this.messageRepository = builder.messageRepository;
        this.settings = builder.settings.create();
        this.checks = builder.checks;
    }

    @Override
    public @NotNull @Unmodifiable Set<MessageTarget> getTargets() {
        return Collections.unmodifiableSet(targets);
    }

    @Override
    public @NotNull @Unmodifiable List<Message> getMessages() {
        return List.copyOf(messageRepository.all());
    }

    @Override
    @SuppressWarnings("unchecked")
    public @NotNull @Unmodifiable <T extends Check<?>> Collection<T> getChecks(Class<T> checkType) {
        return Collections.unmodifiableList((List<T>) this.checks.getOrDefault(checkType, new ArrayList<>()));
    }

    @Override
    public @NotNull Component getDisplayName() {
        return getOrDefault(DISPLAY_NAME, text(key));
    }

    @Override
    public void addTarget(final @NonNull MessageTarget target) {
        this.targets.add(target);
    }

    @Override
    public void sendMessage(final @NonNull Message message) {
        messageRepository.add(message);
        messenger.sendMessage(message, Messenger.Context.of(this, message));
    }

    private static class DefaultChannelMessenger implements Messenger<Channel> {

        @Override
        public void sendMessage(Message message, Context<Channel> context) {
            context.target().getTargets().forEach(messageTarget -> messageTarget.sendMessage(context.message()));
        }
    }

    static class ChannelImplBuilder implements Builder {

        private final String key;
        private final Map<Class<? extends Check<?>>, List<? extends Check<?>>> checks = new HashMap<>(Map.of(
            JoinChannel.class, List.of(
                new JoinChannelPermissionCheck()
            )
        ));
        private Component displayName;
        private Settings.Builder settings;
        private Messenger<Channel> messenger = DEFAULT_MESSENGER;
        private MessageRepository messageRepository = createInMemoryMessageRepository();

        ChannelImplBuilder(String key) {
            if (isInvalidChannelKey(key))
                throw new InvalidKey();
            this.key = key;
            defaultSettings(Settings.settings()
                .withStatic(JOIN_PERMISSION, of("schat.channel." + key + ".join")));
        }

        @Override
        public <V> @NotNull Builder setting(final @NonNull Setting<V> setting, final @Nullable V value) {
            this.settings.withStatic(setting, value);
            return this;
        }

        @Override
        public @NotNull Builder defaultSettings(final @NonNull Settings.Builder settings) {
            this.settings = settings;
            return this;
        }

        @Override
        public @NotNull Builder settings(final @NonNull Settings settings) {
            this.settings = settings.copy();
            return this;
        }

        @Override
        public @NotNull Builder settings(final @NonNull Consumer<Settings.Builder> settings) {
            settings.accept(this.settings);
            return this;
        }

        @Override
        public Builder displayName(@NonNull Component displayName) {
            if (displayName.equals(empty()))
                return this;
            this.displayName = displayName;
            return this;
        }

        @Override
        public Builder messageRepository(MessageRepository messageRepository) {
            this.messageRepository = messageRepository;
            return this;
        }

        @Override
        public Builder messenger(@NonNull Messenger<Channel> messenger) {
            this.messenger = messenger;
            return this;
        }

        @Override
        public Builder clearChecks() {
            this.checks.clear();
            return this;
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T extends Check<?>> Builder check(T @NonNull ... checks) {
            if (checks.length > 1) return this;
            final List<T> checklist = (List<T>) this.checks.computeIfAbsent((Class<? extends Check<?>>) checks[0].getClass(), c -> new ArrayList<>());
            checklist.addAll(List.of(checks));
            return this;
        }

        @Override
        public Channel create() {
            if (displayName != null)
                setting(DISPLAY_NAME, displayName);
            return new ChannelImpl(this);
        }

        private boolean isInvalidChannelKey(final String key) {
            return CHANNEL_KEY_PATTERN.asMatchPredicate().negate().test(key);
        }
    }
}
