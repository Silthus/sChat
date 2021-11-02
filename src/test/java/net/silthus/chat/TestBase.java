package net.silthus.chat;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import lombok.SneakyThrows;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.silthus.chat.config.ChannelConfig;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.RegisteredListener;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class TestBase {

    protected ServerMock server;
    protected SChat plugin;

    @BeforeEach
    public void setUp() {
        server = MockBukkit.mock();
        plugin = MockBukkit.load(SChat.class);
    }

    @AfterEach
    @SneakyThrows
    @SuppressWarnings("unchecked")
    public void tearDown() {
        Bukkit.getScheduler().cancelTasks(plugin);
        MockBukkit.unmock();
        Field instances = Class.forName("net.kyori.adventure.platform.bukkit.BukkitAudiencesImpl").getDeclaredField("INSTANCES");
        instances.setAccessible(true);
        ((Map<String, BukkitAudiences>) instances.get(null)).clear();
    }

    protected Stream<Listener> getRegisteredListeners() {
        return HandlerList.getRegisteredListeners(plugin).stream()
                .map(RegisteredListener::getListener);
    }

    protected void assertReceivedNoMessage(PlayerMock player) {
        assertThat(player.nextMessage()).isNull();
    }

    protected void assertReceivedMessage(PlayerMock player, String message) {
        assertThat(player.nextMessage()).isEqualTo(message);
    }

    protected Channel createChannel(Function<ChannelConfig.ChannelConfigBuilder, ChannelConfig.ChannelConfigBuilder> cfg) {
        return createChannel("test", cfg);
    }

    protected Channel createChannel(String name, Function<ChannelConfig.ChannelConfigBuilder, ChannelConfig.ChannelConfigBuilder> cfg) {
        return new Channel(name, cfg.apply(ChannelConfig.builder()).build());
    }

    protected String toText(Component component) {
        return LegacyComponentSerializer.legacyAmpersand().serialize(component);
    }

    protected String toText(Message message) {
        return toText(message.formattedMessage());
    }
}
