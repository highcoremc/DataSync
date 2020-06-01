package org.nocraft.loperd.playerdatasync.spigot;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.NotNull;
import org.nocraft.loperd.playerdatasync.common.Composer;
import org.nocraft.loperd.playerdatasync.common.config.Adapter.ConfigurationAdapter;
import org.nocraft.loperd.playerdatasync.common.config.Configuration;
import org.nocraft.loperd.playerdatasync.common.config.PluginConfiguration;
import org.nocraft.loperd.playerdatasync.common.plugin.DataSyncPlugin;
import org.nocraft.loperd.playerdatasync.common.plugin.PluginLogger;
import org.nocraft.loperd.playerdatasync.common.scheduler.SchedulerAdapter;
import org.nocraft.loperd.playerdatasync.common.storage.StorageFactory;
import org.nocraft.loperd.playerdatasync.spigot.event.PlayerLoadedEvent;
import org.nocraft.loperd.playerdatasync.spigot.listener.DataSyncListenerBukkit;
import org.nocraft.loperd.playerdatasync.spigot.listener.LockedPlayerListener;
import org.nocraft.loperd.playerdatasync.spigot.listener.PlayerLoadListener;
import org.nocraft.loperd.playerdatasync.spigot.manager.LockedPlayerManager;

import java.io.File;
import java.util.Optional;

public class DataSyncPluginBukkit implements DataSyncPlugin {

    private final Composer<DataSyncListenerBukkit> listeners = new Composer<>();

    @Getter
    private LockedPlayerManager lockedPlayerManager;

    private PluginConfiguration configuration;
    private final DataSyncBootstrapBukkit bootstrap;

    @Getter
    private BukkitStorageAdapter storage;

    public DataSyncPluginBukkit(DataSyncBootstrapBukkit bootstrap) {
        this.bootstrap = bootstrap;

    }

    public void enable() {
        this.lockedPlayerManager = new LockedPlayerManager(this);

        // load configuration
        getLogger().info("Loading configuration...");

        this.configuration = new PluginConfiguration(this, provideConfigurationAdapter());

        this.storage = new BukkitStorageAdapter(new StorageFactory(this));

        this.listeners.add(new PlayerLoadListener(this));
        this.listeners.add(new LockedPlayerListener(this));
        this.listeners.register();
    }

    public void disable() {
        this.listeners.unregister();
        this.listeners.shutdown();
        this.storage.shutdown();
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
    public DataSyncBootstrapBukkit getBootstrap() {
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

    public void applyPlayerData(PlayerData data) {
        Optional<Player> p = getBootstrap().getPlayer(data.getPlayerId());

        if (!p.isPresent()) {
            getLogger().info(String.format(
                    "PlayerData for player %s is not applied. Player not found on the server.", data.getPlayerId()));
            this.lockedPlayerManager.remove(data.getPlayerId());
            // TODO: place in queue with timeout
            return;
        }

        Runnable finalize = () -> getPluginManager().callEvent(new PlayerLoadedEvent(p.get()));
        this.getScheduler().sync().execute(new PlayerDataApply(p.get(), data, finalize));
    }

    @NotNull
    private PluginManager getPluginManager() {
        return getBootstrap().getServer().getPluginManager();
    }
}
