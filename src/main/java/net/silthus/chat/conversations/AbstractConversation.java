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

package net.silthus.chat.conversations;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.silthus.chat.*;
import net.silthus.chat.identities.AbstractChatTarget;

import java.util.*;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public abstract class AbstractConversation extends AbstractChatTarget implements Conversation {

    private final Set<ChatTarget> targets = Collections.newSetFromMap(Collections.synchronizedMap(new WeakHashMap<>()));
    private Format format = Format.defaultFormat();

    public AbstractConversation(UUID id, String name) {
        super(id, name);
    }

    protected AbstractConversation(String name) {
        super(name);
    }

    protected void addTargets(Collection<ChatTarget> targets) {
        this.targets.addAll(targets);
    }

    @Override
    public Collection<ChatTarget> getTargets() {
        return List.copyOf(targets);
    }

    @Override
    public void addTarget(@NonNull ChatTarget target) {
        this.targets.add(target);
    }

    @Override
    public void removeTarget(@NonNull ChatTarget target) {
        this.targets.remove(target);
    }

    @Override
    public boolean deleteMessage(Message message) {
        if (super.deleteMessage(message)) {
            getTargets().forEach(target -> target.deleteMessage(message));
            return true;
        }
        return false;
    }

    @Override
    public int compareTo(@NonNull Conversation o) {
        return Comparator.comparing(Conversation::getType)
                .thenComparing(Identity::getName)
                .compare(this, o);
    }

    @Override
    public void close() {
        getTargets().forEach(target -> target.unsubscribe(this));
        targets.clear();
    }
}