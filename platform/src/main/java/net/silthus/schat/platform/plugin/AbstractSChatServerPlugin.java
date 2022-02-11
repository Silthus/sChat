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

package net.silthus.schat.platform.plugin;

import cloud.commandframework.CommandManager;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.silthus.schat.channel.ChannelPrototype;
import net.silthus.schat.channel.ChannelRepository;
import net.silthus.schat.chatter.ChatterFactory;
import net.silthus.schat.chatter.ChatterRepository;
import net.silthus.schat.commands.CreatePrivateChannelCommand;
import net.silthus.schat.commands.JoinChannelCommand;
import net.silthus.schat.commands.SendMessageCommand;
import net.silthus.schat.eventbus.EventBus;
import net.silthus.schat.features.GlobalChatFeature;
import net.silthus.schat.messenger.Messenger;
import net.silthus.schat.platform.chatter.AbstractChatterFactory;
import net.silthus.schat.platform.chatter.ConnectionListener;
import net.silthus.schat.platform.commands.ChannelCommands;
import net.silthus.schat.platform.commands.Commands;
import net.silthus.schat.platform.commands.PrivateMessageCommands;
import net.silthus.schat.platform.config.ChannelConfig;
import net.silthus.schat.platform.sender.Sender;
import net.silthus.schat.ui.view.ViewFactory;
import net.silthus.schat.ui.view.ViewProvider;
import net.silthus.schat.ui.views.Views;
import net.silthus.schat.util.gson.GsonProvider;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import static net.silthus.schat.channel.ChannelRepository.createInMemoryChannelRepository;
import static net.silthus.schat.chatter.ChatterRepository.createInMemoryChatterRepository;
import static net.silthus.schat.platform.commands.parser.ChannelArgument.registerChannelArgument;
import static net.silthus.schat.platform.commands.parser.ChatterArgument.registerChatterArgument;
import static net.silthus.schat.platform.config.ConfigKeys.CHANNELS;
import static net.silthus.schat.platform.config.ConfigKeys.DEBUG;
import static net.silthus.schat.ui.view.ViewProvider.cachingViewProvider;
import static net.silthus.schat.util.gson.types.ChannelSerializer.CHANNEL_TYPE;
import static net.silthus.schat.util.gson.types.ChannelSerializer.createChannelSerializer;
import static net.silthus.schat.util.gson.types.TargetsSerializer.TARGETS_TYPE;
import static net.silthus.schat.util.gson.types.TargetsSerializer.createTargetsSerializer;

@Getter
@Accessors(fluent = true)
public abstract class AbstractSChatServerPlugin extends AbstractSChatPlugin {

    private ViewFactory viewFactory;
    private ViewProvider viewProvider;

    private ChatterFactory chatterFactory;
    private ChatterRepository chatterRepository;
    private ConnectionListener connectionListener;

    private ChannelRepository channelRepository;

    private Commands commands;

    private final List<Object> features = new ArrayList<>();

    protected void onLoad() {
    }

    @Override
    protected void onEnable() {
        viewFactory = createViewFactory();
        viewProvider = createViewProvider(viewFactory);

        chatterRepository = createInMemoryChatterRepository(config().get(DEBUG));
        chatterFactory = createChatterFactory(viewProvider);
        connectionListener = registerConnectionListener(chatterRepository, chatterFactory, messenger(), eventBus());

        channelRepository = createChannelRepository();

        registerSerializers();
        setupPrototypes();
        loadFeatures();

        loadChannels();

        commands = createCommands();

        registerListeners();
    }

    @Override
    protected void onDisable() {
    }

    @ApiStatus.OverrideOnly
    protected ViewFactory createViewFactory() {
        return Views::tabbedChannels;
    }

    @ApiStatus.OverrideOnly
    protected ViewProvider createViewProvider(ViewFactory viewFactory) {
        return cachingViewProvider(viewFactory);
    }

    protected abstract AbstractChatterFactory createChatterFactory(final ViewProvider viewProvider);

    protected abstract ConnectionListener registerConnectionListener(ChatterRepository repository, ChatterFactory factory, Messenger messenger, EventBus eventBus);

    @ApiStatus.OverrideOnly
    protected ChannelRepository createChannelRepository() {
        return createInMemoryChannelRepository(config().get(DEBUG));
    }

    private void registerSerializers() {
        GsonProvider.registerTypeAdapter(CHANNEL_TYPE, createChannelSerializer(channelRepository(), config().get(DEBUG)));
        GsonProvider.registerTypeAdapter(TARGETS_TYPE, createTargetsSerializer(chatterRepository(), config().get(DEBUG)));
    }

    private void setupPrototypes() {
        SendMessageCommand.prototype(builder -> builder
            .eventBus(eventBus())
            .use(b -> config().get(DEBUG) ? new SendMessageCommand.Logging(b) : new SendMessageCommand(b))
        );
        CreatePrivateChannelCommand.prototype(builder -> builder
            .channelRepository(channelRepository())
            .messenger(messenger())
        );
        JoinChannelCommand.prototype(builder -> builder
            .eventBus(eventBus())
        );
        ChannelPrototype.configure(eventBus());
    }

    private void loadFeatures() {
        final GlobalChatFeature feature = new GlobalChatFeature(messenger());
        feature.bind(eventBus());
        features.add(feature);
    }

    private void loadChannels() {
        logger().info("Loading channels...");
        for (final ChannelConfig channelConfig : this.config().get(CHANNELS)) {
            channelRepository().add(channelConfig.toChannel());
        }
        logger().info("... loaded " + channelRepository.keys().size() + " channels.");
    }

    @NotNull
    private Commands createCommands() {
        final CommandManager<Sender> commandManager = provideCommandManager();
        final Commands commands = new Commands(commandManager);

        registerCommandArguments(commandManager);
        registerCommands(commands);

        return commands;
    }

    private void registerCommandArguments(CommandManager<Sender> commandManager) {
        registerChatterArgument(commandManager, chatterRepository());
        registerChannelArgument(commandManager, channelRepository(), chatterRepository);
    }

    protected abstract CommandManager<Sender> provideCommandManager();

    private void registerCommands(Commands commands) {
        registerNativeCommands(commands);
        registerCustomCommands(commands);
    }

    private void registerNativeCommands(Commands commands) {
        commands.register(new ChannelCommands());
        commands.register(new PrivateMessageCommands());
    }

    @ApiStatus.OverrideOnly
    protected void registerCustomCommands(Commands commands) {
    }

    @ApiStatus.OverrideOnly
    protected void registerListeners() {
    }

}
