package org.nocraft.loperd.playerdatasync.spigot.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.nocraft.loperd.playerdatasync.common.player.PlayerData;
import org.nocraft.loperd.playerdatasync.spigot.PDSyncPluginBukkit;
import org.nocraft.loperd.playerdatasync.spigot.manager.LockedPlayerManager;
import org.nocraft.loperd.playerdatasync.spigot.manager.PlayerDataManager;
import org.nocraft.loperd.playerdatasync.common.storage.Storage;

import java.util.UUID;

public class PlayerLoadListener extends PDSyncListenerBukkit {

    private final LockedPlayerManager lockedPlayerManager;
    private final PDSyncPluginBukkit plugin;
    private final Storage storage;
    private final PlayerDataManager playerDataManager;

    public PlayerLoadListener(PDSyncPluginBukkit plugin, Storage storage, LockedPlayerManager lockedPlayerManager) {
        super(plugin);
        this.plugin = plugin;
        this.storage = storage;
        this.lockedPlayerManager = lockedPlayerManager;
        this.playerDataManager = this.plugin.getPlayerManager();
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();
        String name = player.getName();

        this.lockedPlayerManager.add(uuid);

        PlayerData data = this.storage.loadPlayerData(uuid, name).join();

        this.lockedPlayerManager.remove(uuid);

        if (null == data) {
            return;
        }

        this.playerDataManager.applyPlayerData(data, player);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        PlayerData data = this.playerDataManager.createPlayerData(player);
        this.storage.savePlayerData(data).join();
        player.getInventory().clear();
    }
}
