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

import lombok.NonNull;
import net.silthus.chat.ChatTarget;
import net.silthus.chat.Conversation;
import net.silthus.chat.SChat;
import net.silthus.chat.identities.PlayerChatter;

import java.util.*;
import java.util.stream.Collectors;

public final class ConversationManager {

    private final SChat plugin;
    private final Map<UUID, Conversation> conversations = Collections.synchronizedMap(new HashMap<>());

    public ConversationManager(SChat plugin) {
        this.plugin = plugin;
    }

    public Collection<Conversation> getConversations() {
        return Set.copyOf(conversations.values());
    }

    public Conversation getConversation(@NonNull UUID id) {
        Conversation conversation = plugin.getChannelRegistry().get(id);
        if (conversation == null)
            conversation = conversations.get(id);
        return conversation;
    }

    public Conversation registerConversation(@NonNull Conversation conversation) {
        if (conversations.containsKey(conversation.getUniqueId()))
            return conversations.get(conversation.getUniqueId());
        conversation.addTarget(plugin.getBungeecord());
        conversations.put(conversation.getUniqueId(), conversation);
        return conversation;
    }

    public Optional<Conversation> getDirectConversation(ChatTarget... targets) {
        return conversations.values().stream()
                .filter(conversation -> conversation instanceof DirectConversation)
                .filter(conversation -> conversation.getTargets().stream().filter(target -> target instanceof PlayerChatter).collect(Collectors.toSet()).equals(Set.of(targets)))
                .findFirst();
    }

    public void remove(@NonNull Conversation conversation) {
        conversations.remove(conversation.getUniqueId());
    }
}