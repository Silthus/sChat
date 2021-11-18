package net.silthus.chat.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.ConditionFailedException;
import co.aikar.commands.MessageType;
import co.aikar.commands.annotation.*;
import co.aikar.locales.MessageKey;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.silthus.chat.Chatter;
import net.silthus.chat.SChat;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static net.kyori.adventure.text.Component.text;
import static net.silthus.chat.Constants.Language.Commands.Nicknames.*;
import static net.silthus.chat.Constants.PERMISSION_NICKNAME_SET;
import static net.silthus.chat.Constants.PERMISSION_NICKNAME_SET_OTHERS;

@CommandAlias("nickname|nick")
public class NicknameCommands extends BaseCommand {

    private final SChat plugin;

    public NicknameCommands(SChat plugin) {
        this.plugin = plugin;
    }

    @Default
    @Subcommand("set")
    @CommandCompletion("*")
    @CommandPermission(PERMISSION_NICKNAME_SET)
    public void set(@Flags("defaultself,other=" + PERMISSION_NICKNAME_SET_OTHERS) Chatter chatter, String name) {
        validateAndSetNickname(chatter, name);
    }

    @Subcommand("reset")
    @CommandPermission(PERMISSION_NICKNAME_SET)
    public void reset(@Flags("defaultself,other=" + PERMISSION_NICKNAME_SET_OTHERS) Chatter chatter) {
        resetNickname(chatter);
    }

    private void resetNickname(Chatter chatter) {
        chatter.setDisplayName(null);
        setPlayerNickname(chatter, chatter.getName());
        getCurrentCommandIssuer().sendMessage(MessageType.INFO, key(RESET),
                "{nickname}", getDisplayName(chatter)
        );
    }

    private void validateAndSetNickname(Chatter chatter, String name) {
        validateNickname(name);
        setNickname(chatter, name);
    }

    private void setNickname(Chatter chatter, String name) {
        String oldName = getDisplayName(chatter);
        chatter.setDisplayName(text(name));
        setPlayerNickname(chatter, name);
        getCurrentCommandIssuer().sendMessage(MessageType.INFO, key(CHANGED),
                "{nickname}", name,
                "{old_nickname}", oldName
        );
    }

    @NotNull
    private String getDisplayName(Chatter chatter) {
        return LegacyComponentSerializer.legacyAmpersand().serialize(chatter.getDisplayName());
    }

    private void setPlayerNickname(Chatter chatter, String name) {
        Player player = Bukkit.getPlayer(chatter.getUniqueId());
        if (player != null)
            player.setDisplayName(name);
    }

    private void validateNickname(String name) {
        validateNicknamePattern(name);
        validateBlockedNicknames(name);
    }

    private void validateBlockedNicknames(String name) {
        boolean nicknameIsBlocked = plugin.getPluginConfig().player().blockedNickNames()
                .stream().map(s -> Pattern.compile(s, Pattern.CASE_INSENSITIVE))
                .map(pattern -> pattern.matcher(name))
                .anyMatch(Matcher::matches);
        if (nicknameIsBlocked)
            throw new ConditionFailedException(key(BLOCKED), "{nickname}", name);
    }

    private void validateNicknamePattern(String name) {
        Matcher matcher = plugin.getPluginConfig().player().nickNamePattern().matcher(name);
        if (!matcher.matches())
            throw new ConditionFailedException(key(INVALID), "{nickname}", name);
    }

    private MessageKey key(String key) {
        return SChatCommands.key(NICKNAMES_BASE + "." + key);
    }
}
