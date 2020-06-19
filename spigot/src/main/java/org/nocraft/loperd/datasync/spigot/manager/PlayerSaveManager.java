package org.nocraft.loperd.datasync.spigot.manager;

import org.bukkit.entity.Player;
import org.nocraft.loperd.datasync.spigot.DataSyncPluginBukkit;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class PlayerSaveManager {

    public Set<Player> activePlayers = ConcurrentHashMap.newKeySet();
    private final DataSyncPluginBukkit plugin;

    public PlayerSaveManager(DataSyncPluginBukkit plugin) {
        this.plugin = plugin;

        plugin.getScheduler().asyncRepeating(this::saveData, 5L, TimeUnit.SECONDS);
    }

    private void saveData() {
        if (this.plugin.getStorage() == null || this.activePlayers.isEmpty()) {
            return;
        }

        for (Player player : this.activePlayers) {
            if (null != player) {
                this.plugin.getStorage().savePlayerData(player);
            }
        }
    }

    public void add(Player player) {
        this.activePlayers.add(player);
    }

    public void remove(Player player) {
        this.activePlayers.remove(player);
    }
}
