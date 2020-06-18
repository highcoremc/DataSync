package org.nocraft.loperd.datasync.spigot.player;

import lombok.SneakyThrows;
import org.bukkit.entity.Player;
import org.nocraft.loperd.datasync.spigot.BukkitStorageAdapter;
import org.nocraft.loperd.datasync.spigot.DataSyncPluginBukkit;

public class PlayerDataSave implements Runnable {

    private final DataSyncPluginBukkit plugin;
    private final BukkitStorageAdapter storage;

    public PlayerDataSave(DataSyncPluginBukkit plugin) {
        this.storage = plugin.getStorage();
        this.plugin = plugin;
    }

    @SneakyThrows
    @Override
    public void run() {
        for (Player player : this.plugin.getBootstrap().getServer().getOnlinePlayers()) {
            this.storage.savePlayerData(player);
        }
    }
}
