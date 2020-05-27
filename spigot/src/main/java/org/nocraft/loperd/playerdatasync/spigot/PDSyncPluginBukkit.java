package org.nocraft.loperd.playerdatasync.spigot;

import lombok.Getter;
import org.nocraft.loperd.playerdatasync.common.Composer;
import org.nocraft.loperd.playerdatasync.common.config.Adapter.ConfigurationAdapter;
import org.nocraft.loperd.playerdatasync.common.config.Configuration;
import org.nocraft.loperd.playerdatasync.common.config.PluginConfiguration;
import org.nocraft.loperd.playerdatasync.common.player.PlayerData;
import org.nocraft.loperd.playerdatasync.common.plugin.PDSyncPlugin;
import org.nocraft.loperd.playerdatasync.common.plugin.PluginLogger;
import org.nocraft.loperd.playerdatasync.common.scheduler.SchedulerAdapter;
import org.nocraft.loperd.playerdatasync.common.storage.Storage;
import org.nocraft.loperd.playerdatasync.common.storage.StorageFactory;
import org.nocraft.loperd.playerdatasync.spigot.listener.LockedPlayerListener;
import org.nocraft.loperd.playerdatasync.spigot.listener.PDSyncListenerBukkit;
import org.nocraft.loperd.playerdatasync.spigot.listener.PlayerLoadListener;
import org.nocraft.loperd.playerdatasync.spigot.manager.LockedPlayerManager;
import org.nocraft.loperd.playerdatasync.spigot.manager.PlayerDataFactory;
import org.nocraft.loperd.playerdatasync.spigot.serializer.ItemStackSerializer;
import org.nocraft.loperd.playerdatasync.spigot.serializer.ItemStackSerializerFactory;
import org.nocraft.loperd.playerdatasync.spigot.serializer.ItemStackSerializerType;
import org.nocraft.loperd.playerdatasync.spigot.serializer.PlayerSerializer;
import org.nocraft.loperd.playerdatasync.spigot.thread.PlayerDataSaveThread;
import org.nocraft.loperd.playerdatasync.spigot.thread.PlayerDataSyncThread;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class PDSyncPluginBukkit implements PDSyncPlugin {

    private final Composer<PDSyncListenerBukkit> listeners = new Composer<>();

    @Getter
    private volatile BlockingQueue<PlayerDataLife> playerSyncQueue;

    @Getter
    private volatile Map<UUID, Long> playerDataSaveStatusMap = new HashMap<>();

    @Getter
    private PlayerDataFactory playerDataFactory;

    @Getter
    private PlayerSerializer playerSerializer;

    @Getter
    private LockedPlayerManager lockedPlayerManager;

    private PluginConfiguration configuration;
    private final PDSyncBootstrapBukkit bootstrap;

    private PlayerDataSyncThread syncThread;
    private PlayerDataSaveThread saveThread;

    private Storage storage;

    public PDSyncPluginBukkit(PDSyncBootstrapBukkit bootstrap) {
        this.bootstrap = bootstrap;

    }

    public void enable() {
        LockedPlayerManager lockedManager = new LockedPlayerManager(this);

        ItemStackSerializer itemStackSerializer = ItemStackSerializerFactory.create(ItemStackSerializerType.BUKKIT);

        this.playerSerializer = new PlayerSerializer(itemStackSerializer);

        this.playerDataFactory = new PlayerDataFactory(playerSerializer);
        this.lockedPlayerManager = new LockedPlayerManager(this);

        this.playerSyncQueue = new ArrayBlockingQueue<>(bootstrap.getServer().getMaxPlayers());
        this.syncThread = new PlayerDataSyncThread(this);
        this.saveThread = new PlayerDataSaveThread(this);

        // load configuration
        getLogger().info("Loading configuration...");

        // TODO: fix it
        this.configuration = new PluginConfiguration(this, provideConfigurationAdapter());

        this.storage = new StorageFactory(this).getInstance();

        this.listeners.add(new PlayerLoadListener(this, storage));
        this.listeners.add(new LockedPlayerListener(this, lockedManager));

        this.listeners.register();

        this.syncThread.start();
        this.saveThread.start();
    }

    public void disable() {
        this.listeners.unregister();
        this.listeners.shutdown();
        this.syncThread.terminate();
        this.saveThread.terminate();
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
    public PDSyncBootstrapBukkit getBootstrap() {
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

    public Storage getStorage() {
        return this.storage;
    }
}
