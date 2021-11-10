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

package net.silthus.chat.integrations.bungeecord;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.google.gson.Gson;
import lombok.extern.java.Log;
import net.silthus.chat.Constants;
import net.silthus.chat.Message;
import net.silthus.chat.SChat;
import net.silthus.chat.identities.Chatter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

import static net.silthus.chat.Constants.BUNGEECORD_CHANNEL;
import static net.silthus.chat.Constants.SCHAT_MESSAGES_CHANNEL;

@Log(topic = Constants.PLUGIN_NAME)
@SuppressWarnings("UnstableApiUsage")
public class BungeecordIntegration implements PluginMessageListener {
    private final SChat plugin;

    private final Supplier<Player> playerSupplier;
    private final Gson gson = new Gson();

    public BungeecordIntegration(SChat plugin, Supplier<Player> playerSupplier) {
        this.plugin = plugin;
        this.playerSupplier = playerSupplier;
    }

    public BungeecordIntegration(SChat plugin) {
        this(plugin, () -> Bukkit.getOnlinePlayers().stream().findFirst().orElse(null));
    }

    public void sendGlobalChatMessage(Message message) {
        String json = gson.toJson(new MessageDto(message));
        sendPluginMessage(forwardToAllServers(SCHAT_MESSAGES_CHANNEL), json);
    }

    public void synchronizeChatter(Chatter chatter) {

    }

    @Override
    public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, byte[] serverMessage) {
        if (!channel.equals(BUNGEECORD_CHANNEL)) {
            return;
        }

        ByteArrayDataInput in = ByteStreams.newDataInput(serverMessage);
        String subChannel = in.readUTF();

        switch (subChannel) {
            case SCHAT_MESSAGES_CHANNEL -> processGlobalMessageChannel(in);
        }
    }

    private void processGlobalMessageChannel(ByteArrayDataInput in) {
        Message message = gson.fromJson(getJsonData(in), MessageDto.class).toMessage();
        message.getTargets().forEach(target -> target.sendMessage(message));
    }

    private void sendPluginMessage(ByteArrayDataOutput out) {
        Player player = playerSupplier.get();
        if (player == null) return;
        player.sendPluginMessage(plugin, BUNGEECORD_CHANNEL, out.toByteArray());
    }

    private void sendPluginMessage(ByteArrayDataOutput out, String json) {
        sendPluginMessage(writeJsonDataToStream(out, json));
    }

    private ByteArrayDataOutput forwardToAllServers(String channel) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Forward");
        out.writeUTF("ALL");
        out.writeUTF(channel);
        return out;
    }

    private ByteArrayDataOutput writeJsonDataToStream(ByteArrayDataOutput out, String json) {
        ByteArrayDataOutput data = ByteStreams.newDataOutput();
        data.writeUTF(json);
        return writeDataToStream(out, data);
    }

    private ByteArrayDataOutput writeDataToStream(ByteArrayDataOutput out, ByteArrayDataOutput data) {
        byte[] bytes = data.toByteArray();
        out.writeShort(bytes.length);
        out.write(bytes);
        return out;
    }

    private String getJsonData(ByteArrayDataInput in) {
        short len = in.readShort();
        byte[] bytes = new byte[len];
        in.readFully(bytes);

        return ByteStreams.newDataInput(bytes).readUTF();
    }
}
