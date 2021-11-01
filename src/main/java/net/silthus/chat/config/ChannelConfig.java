package net.silthus.chat.config;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.extern.java.Log;
import net.silthus.chat.Format;
import net.silthus.chat.formats.SimpleFormat;
import org.bukkit.configuration.ConfigurationSection;

@Log
@Data
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ChannelConfig {

    public static ChannelConfig of(ConfigurationSection config) {
        return new ChannelConfig(config);
    }

    public static ChannelConfig empty() {
        return ChannelConfig.builder().build();
    }

    private String name;
    @Builder.Default
    private boolean protect = false;
    @Builder.Default
    private SimpleFormat format = Format.defaultFormat();

    private ChannelConfig(ConfigurationSection config) {
        this.name = config.getString("name");
        ConfigurationSection formatSection = config.getConfigurationSection("format");
        if (formatSection != null)
            this.format = Format.fromConfig(formatSection);
        else
            this.format = Format.defaultFormat();
    }
}
