package org.nocraft.loperd.datasync.spigot;

import com.gmail.tracebachi.DeltaRedis.Spigot.DeltaRedisApi;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.NotNull;
import org.nocraft.loperd.datasync.common.Composer;
import org.nocraft.loperd.datasync.common.config.Adapter.ConfigurationAdapter;
import org.nocraft.loperd.datasync.common.config.Configuration;
import org.nocraft.loperd.datasync.common.config.PluginConfiguration;
import org.nocraft.loperd.datasync.common.plugin.DataSyncPlugin;
import org.nocraft.loperd.datasync.common.plugin.PluginLogger;
import org.nocraft.loperd.datasync.common.scheduler.SchedulerAdapter;
import org.nocraft.loperd.datasync.common.storage.StorageFactory;
import org.nocraft.loperd.datasync.spigot.event.PlayerLoadEvent;
import org.nocraft.loperd.datasync.spigot.event.PlayerLoadedEvent;
import org.nocraft.loperd.datasync.spigot.listener.LockedPlayerListener;
import org.nocraft.loperd.datasync.spigot.listener.PlayerEnterListener;
import org.nocraft.loperd.datasync.spigot.listener.PlayerLoadListener;
import org.nocraft.loperd.datasync.spigot.manager.LockedPlayerManager;
import org.nocraft.loperd.datasync.spigot.player.PlayerData;
import org.nocraft.loperd.datasync.spigot.player.PlayerDataApply;
import org.nocraft.loperd.datasync.spigot.player.PlayerDataSave;

import java.io.File;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class DataSyncPluginBukkit implements DataSyncPlugin {

    private final Composer<DataSyncListenerBukkit> listeners = new Composer<>();

    @Getter
    private LockedPlayerManager lockedPlayerManager;

    private PluginConfiguration configuration;
    private final DataSyncBootstrapBukkit bootstrap;

    @Getter
    private BukkitStorageAdapter storage;

    @Getter
    private DeltaRedisApi deltaRedisApi;

    public DataSyncPluginBukkit(DataSyncBootstrapBukkit bootstrap) {
        this.bootstrap = bootstrap;

    }

    public void enable() {
        this.lockedPlayerManager = new LockedPlayerManager(this);

        // load configuration
        getLogger().info("Loading configuration...");

        this.configuration = new PluginConfiguration(this, provideConfigurationAdapter());

        this.storage = new BukkitStorageAdapter(new StorageFactory(this));

        this.listeners.add(new PlayerEnterListener(this));
        this.listeners.add(new PlayerLoadListener(this));
        this.listeners.add(new LockedPlayerListener(this));
        this.listeners.register();

        this.deltaRedisApi = DeltaRedisApi.instance();

        this.getScheduler().asyncRepeating(new PlayerDataSave(this), 3, TimeUnit.SECONDS);
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

    @Override
    public String getName() {
        return "DataSync";
    }

    protected ConfigurationAdapter provideConfigurationAdapter() {
        return new BukkitConfigAdapter(this, resolveConfig());
    }

    private File resolveConfig() {
        File configFile = new File(this.bootstrap.getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            //noinspection ResultOfMethodCallIgnored
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
            return;
        }

        Player player = p.get();

        getPluginManager().callEvent(new PlayerLoadEvent(player));
        Runnable finalize = () -> getPluginManager().callEvent(new PlayerLoadedEvent(player));
        this.getScheduler().sync().execute(new PlayerDataApply(player, data, finalize));
    }

    @NotNull
    private PluginManager getPluginManager() {
        return getBootstrap().getServer().getPluginManager();
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public void applyPlayerData(UUID uuid, Optional<PlayerData> data) {
        if (!data.isPresent()) {
            getLogger().info("PlayerData is not present. Sorry but i can not synchronized it..");
            this.lockedPlayerManager.remove(uuid);
            return;
        }

        this.applyPlayerData(data.get());
    }
}
