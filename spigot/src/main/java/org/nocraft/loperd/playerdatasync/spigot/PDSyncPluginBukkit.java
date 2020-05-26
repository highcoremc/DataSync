package org.nocraft.loperd.playerdatasync.spigot;

import org.nocraft.loperd.playerdatasync.common.Composer;
import org.nocraft.loperd.playerdatasync.common.config.Adapter.ConfigurationAdapter;
import org.nocraft.loperd.playerdatasync.common.config.Configuration;
import org.nocraft.loperd.playerdatasync.common.config.PluginConfiguration;
import org.nocraft.loperd.playerdatasync.common.plugin.PDSyncBootstrap;
import org.nocraft.loperd.playerdatasync.common.plugin.PDSyncPlugin;
import org.nocraft.loperd.playerdatasync.common.plugin.PluginLogger;
import org.nocraft.loperd.playerdatasync.common.scheduler.SchedulerAdapter;
import org.nocraft.loperd.playerdatasync.common.storage.Storage;
import org.nocraft.loperd.playerdatasync.common.storage.StorageFactory;
import org.nocraft.loperd.playerdatasync.spigot.listener.LockedPlayerListener;
import org.nocraft.loperd.playerdatasync.spigot.listener.PDSyncListenerBukkit;
import org.nocraft.loperd.playerdatasync.spigot.listener.PlayerLoadListener;
import org.nocraft.loperd.playerdatasync.spigot.manager.LockedPlayerManager;
import org.nocraft.loperd.playerdatasync.spigot.manager.PlayerDataManager;
import org.nocraft.loperd.playerdatasync.spigot.serializer.ItemStackSerializer;
import org.nocraft.loperd.playerdatasync.spigot.serializer.ItemStackSerializerFactory;
import org.nocraft.loperd.playerdatasync.spigot.serializer.ItemStackSerializerType;
import org.nocraft.loperd.playerdatasync.spigot.serializer.PlayerSerializer;

import java.io.File;

public class PDSyncPluginBukkit implements PDSyncPlugin {

    private PlayerDataManager playerDataManager;
    private PluginConfiguration configuration;

    private final PDSyncBootstrapBukkit bootstrap;

    private final Composer<PDSyncListenerBukkit> listeners = new Composer<>();

    public PDSyncPluginBukkit(PDSyncBootstrapBukkit bootstrap) {
        this.bootstrap = bootstrap;
    }

    public void enable() {
        LockedPlayerManager lockedManager = new LockedPlayerManager(this);

        ItemStackSerializer itemStackSerializer = ItemStackSerializerFactory.create(ItemStackSerializerType.BUKKIT);
        PlayerSerializer playerSerializer = new PlayerSerializer(itemStackSerializer);

        this.playerDataManager = new PlayerDataManager(playerSerializer);

        // load configuration
        getLogger().info("Loading configuration...");

        // TODO: fix it
        this.configuration = new PluginConfiguration(this, provideConfigurationAdapter());

        Storage storage = new StorageFactory(this).getInstance();

        this.listeners.add(new PlayerLoadListener(this, storage, lockedManager));
        this.listeners.add(new LockedPlayerListener(this, lockedManager));

        this.listeners.register();
    }

    public void disable() {
        listeners.unregister();
        listeners.shutdown();
    }

    @Override
    public PluginLogger getLogger() {
        return this.getBootstrap().getPluginLogger();
    }

    @Override
    public SchedulerAdapter getScheduler() {
        return getBootstrap().getSchedulerAdapter();
    }

    @Override
    public PDSyncBootstrap getBootstrap() {
        return this.bootstrap;
    }

    @Override
    public Configuration getConfiguration() {
        return this.configuration;
    }

    protected ConfigurationAdapter provideConfigurationAdapter() {
        return new BukkitConfigAdapter(this, resolveConfig());
    }

    private File resolveConfig() {
        File configFile = new File(this.bootstrap.getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            this.bootstrap.getDataFolder().mkdirs();
            this.bootstrap.saveResource("config.yml", false);
        }
        return configFile;
    }

    public PlayerDataManager getPlayerManager() {
        return this.playerDataManager;
    }
}
