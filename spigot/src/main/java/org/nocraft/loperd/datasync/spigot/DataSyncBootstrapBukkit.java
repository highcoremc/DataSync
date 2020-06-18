package org.nocraft.loperd.datasync.spigot;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;
import org.nocraft.loperd.datasync.common.plugin.DataSyncBootstrap;
import org.nocraft.loperd.datasync.common.plugin.PluginLogger;
import org.nocraft.loperd.datasync.common.plugin.logging.JavaPluginLogger;
import org.nocraft.loperd.datasync.common.scheduler.SchedulerAdapter;

import java.io.InputStream;
import java.nio.file.Path;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.CountDownLatch;

public final class DataSyncBootstrapBukkit extends JavaPlugin implements DataSyncBootstrap {

    private final SchedulerAdapter schedulerAdapter;

    private final CountDownLatch enableLatch = new CountDownLatch(1);

    private final JavaPluginLogger logger;
    private final DataSyncPluginBukkit plugin;

    private Instant startTime;

    public DataSyncBootstrapBukkit() {
        this.logger = new JavaPluginLogger(getLogger());
        this.schedulerAdapter = new BukkitSchedulerAdapter(this);
        this.plugin = new DataSyncPluginBukkit(this);
    }

    @Override
    public void onEnable() {
        this.startTime = Instant.now();

        try {
            this.plugin.enable();
        } finally {
            this.enableLatch.countDown();
        }
    }

    @Override
    public void onDisable() {
        this.plugin.disable();
    }

    @Override
    public PluginLogger getPluginLogger() {
        return this.logger;
    }

    @Override
    public SchedulerAdapter getSchedulerAdapter() {
        return this.schedulerAdapter;
    }

    @Override
    public String getVersion() {
        return getDescription().getVersion();
    }

    @Override
    public Instant getStartupTime() {
        return this.startTime;
    }

    @Override
    public Path getDataDirectory() {
        return getDataFolder().toPath().toAbsolutePath();
    }

    @Override
    public InputStream getResourceStream(String path) {
        return getResource(path);
    }

    @Override
    public Optional<Player> getPlayer(UUID uniqueId) {
        return Optional.ofNullable(getServer().getPlayer(uniqueId));
    }

    @Override
    public Optional<UUID> lookupUniqueId(String username) {
        //noinspection deprecation
        return Optional.of(getServer().getOfflinePlayer(username)).map(OfflinePlayer::getUniqueId);
    }

    @Override
    public Optional<String> lookupUsername(UUID uniqueId) {
        return Optional.of(getServer().getOfflinePlayer(uniqueId)).map(OfflinePlayer::getName);
    }

    @Override
    public int getPlayerCount() {
        return getServer().getOnlinePlayers().size();
    }

    @Override
    public Collection<String> getPlayerList() {
        Collection<? extends Player> players = getServer().getOnlinePlayers();
        List<String> list = new ArrayList<>(players.size());

        for (Player player : players) {
            list.add(player.getName());
        }

        return list;
    }

    @Override
    public Collection<UUID> getOnlinePlayers() {
        Collection<? extends Player> players = getServer().getOnlinePlayers();
        List<UUID> list = new ArrayList<>(players.size());

        for (Player player : players) {
            list.add(player.getUniqueId());
        }

        return list;
    }

    @Override
    public boolean isPlayerOnline(UUID uniqueId) {
        Player player = getServer().getPlayer(uniqueId);

        return player != null && player.isOnline();
    }

    @Override
    public @Nullable String identifyClassLoader(ClassLoader classLoader) {
        if (classLoader instanceof org.bukkit.plugin.java.PluginClassLoader) {
            return ((org.bukkit.plugin.java.PluginClassLoader) classLoader).getPlugin().getName();
        }

        return null;
    }

    public void registerListener(DataSyncListenerBukkit listener) {
        this.getServer().getPluginManager().registerEvents(listener, this);
    }
}
