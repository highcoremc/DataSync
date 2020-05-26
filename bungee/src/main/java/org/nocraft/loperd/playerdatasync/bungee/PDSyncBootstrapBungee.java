package org.nocraft.loperd.playerdatasync.bungee;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import org.nocraft.loperd.playerdatasync.common.plugin.PDSyncBootstrap;
import org.nocraft.loperd.playerdatasync.common.plugin.PluginLogger;
import org.nocraft.loperd.playerdatasync.common.plugin.logging.JavaPluginLogger;
import org.nocraft.loperd.playerdatasync.common.scheduler.SchedulerAdapter;

import java.io.InputStream;
import java.nio.file.Path;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.CountDownLatch;

public final class PDSyncBootstrapBungee extends Plugin implements PDSyncBootstrap {

    private final BungeeSchedulerAdapter schedulerAdapter;
    private final CountDownLatch enableLatch = new CountDownLatch(1);
    private final PDSyncPluginBungee plugin;
    private JavaPluginLogger logger;
    private Instant startTime;

    public PDSyncBootstrapBungee() {
        this.schedulerAdapter = new BungeeSchedulerAdapter(this);
        this.plugin = new PDSyncPluginBungee(this);
    }

    @Override
    public void onLoad() {
        this.logger = new JavaPluginLogger(getLogger());
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
        if (this.logger == null) {
            throw new IllegalStateException("Logger has not been initialised yet");
        }
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
        return getResourceAsStream(path);
    }

    @Override
    public Optional<ProxiedPlayer> getPlayer(UUID uniqueId) {
        return Optional.ofNullable(getProxy().getPlayer(uniqueId));
    }

    @Override
    public Optional<UUID> lookupUniqueId(String username) {
        return Optional.empty();
    }

    @Override
    public Optional<String> lookupUsername(UUID uniqueId) {
        return Optional.empty();
    }

    @Override
    public int getPlayerCount() {
        return getProxy().getOnlineCount();
    }

    @Override
    public Collection<String> getPlayerList() {
        Collection<ProxiedPlayer> players = getProxy().getPlayers();
        List<String> list = new ArrayList<>(players.size());
        for (ProxiedPlayer player : players) {
            list.add(player.getName());
        }
        return list;
    }

    @Override
    public Collection<UUID> getOnlinePlayers() {
        Collection<ProxiedPlayer> players = getProxy().getPlayers();
        List<UUID> list = new ArrayList<>(players.size());
        for (ProxiedPlayer player : players) {
            list.add(player.getUniqueId());
        }
        return list;
    }

    @Override
    public boolean isPlayerOnline(UUID uniqueId) {
        return null != getProxy().getPlayer(uniqueId);
    }
}
