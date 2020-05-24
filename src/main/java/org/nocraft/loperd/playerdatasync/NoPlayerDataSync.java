package org.nocraft.loperd.playerdatasync;

import lombok.Getter;
import lombok.NonNull;
import org.bukkit.plugin.java.JavaPlugin;
import org.nocraft.loperd.playerdatasync.Domain.Composer;
import org.nocraft.loperd.playerdatasync.Domain.Config.Adapter.ConfigurationAdapter;
import org.nocraft.loperd.playerdatasync.Domain.Config.PluginConfiguration;
import org.nocraft.loperd.playerdatasync.Domain.Serializer.ItemStackSerializer;
import org.nocraft.loperd.playerdatasync.Domain.Scheduler.SchedulerAdapter;
import org.nocraft.loperd.playerdatasync.Listener.LockedPlayerListener;
import org.nocraft.loperd.playerdatasync.Listener.NoListener;
import org.nocraft.loperd.playerdatasync.Listener.PlayerLoadListener;
import org.nocraft.loperd.playerdatasync.Manager.LockedPlayerManager;
import org.nocraft.loperd.playerdatasync.Manager.PlayerDataManager;
import org.nocraft.loperd.playerdatasync.Serializer.ItemStackSerializerFactory;
import org.nocraft.loperd.playerdatasync.Domain.Serializer.ItemStackSerializerType;
import org.nocraft.loperd.playerdatasync.Serializer.PlayerInventorySerializer;
import org.nocraft.loperd.playerdatasync.Storage.Storage;
import org.nocraft.loperd.playerdatasync.Storage.StorageFactory;

import java.io.File;
import java.io.InputStream;

public final class NoPlayerDataSync extends JavaPlugin {

    @NonNull
    @Getter
    private PlayerInventorySerializer playerInventorySerializer;

    @NonNull
    @Getter
    private ItemStackSerializer itemStackSerializer;

    @NonNull
    @Getter
    private PlayerDataManager playerDataManager;

    @NonNull
    @Getter
    private PluginConfiguration configuration;

    @NonNull
    @Getter
    private SchedulerAdapter scheduler;

    private final Composer<NoListener> listeners = new Composer<>();

    @Override
    public void onEnable() {
        LockedPlayerManager lockedManager = new LockedPlayerManager(this);

        this.itemStackSerializer = ItemStackSerializerFactory.create(ItemStackSerializerType.BS_64);
        this.playerInventorySerializer = new PlayerInventorySerializer(this.itemStackSerializer);

        this.playerDataManager = new PlayerDataManager(this);

        // load configuration
        getLogger().info("Loading configuration...");
        this.configuration = new PluginConfiguration(this, provideConfigurationAdapter());
        this.scheduler = new BukkitSchedulerAdapter(this);

        Storage storage = new StorageFactory(this).getInstance();
        this.listeners.add(new PlayerLoadListener(this, storage, lockedManager));
        this.listeners.add(new LockedPlayerListener(this, lockedManager));

        this.listeners.register();
    }

    @Override
    public void onDisable() {
        listeners.unregister();
        listeners.shutdown();
    }

    public InputStream getResourceStream(String path) {
        return getResource(path);
    }

    public String identifyClassLoader(ClassLoader loader) {
        if (loader instanceof org.bukkit.plugin.java.PluginClassLoader) {
            return ((org.bukkit.plugin.java.PluginClassLoader) loader).getPlugin().getName();
        }
        return null;
    }

    protected ConfigurationAdapter provideConfigurationAdapter() {
        return new BukkitConfigAdapter(this, resolveConfig());
    }

    private File resolveConfig() {
        File configFile = new File(this.getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            this.getDataFolder().mkdirs();
            this.saveResource("config.yml", false);
        }
        return configFile;
    }
}
