package org.nocraft.loperd.datasync.spigot.manager;

import org.bukkit.entity.Player;
import org.nocraft.loperd.datasync.spigot.BukkitStorageAdapter;
import org.nocraft.loperd.datasync.spigot.DataSyncPluginBukkit;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

public class PlayerSaveManager {

    public Queue<Player> activePlayers =
            new ConcurrentLinkedQueue<>();
    private final DataSyncPluginBukkit plugin;

    public PlayerSaveManager(DataSyncPluginBukkit plugin) {
        this.plugin = plugin;
        plugin.getScheduler().asyncRepeating(
                this::saveData,
                1L, TimeUnit.SECONDS);
    }

    private void saveData() {
        if (this.plugin.getStorage() == null) {
            return;
        }

        for (Player player : activePlayers) {
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
