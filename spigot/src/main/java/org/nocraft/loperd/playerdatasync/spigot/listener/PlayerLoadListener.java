package org.nocraft.loperd.playerdatasync.spigot.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.nocraft.loperd.playerdatasync.common.DataSyncPlayer;
import org.nocraft.loperd.playerdatasync.common.storage.Storage;
import org.nocraft.loperd.playerdatasync.common.storage.StorageAdapter;
import org.nocraft.loperd.playerdatasync.spigot.BukkitStorageAdapter;
import org.nocraft.loperd.playerdatasync.spigot.DataSyncPluginBukkit;
import org.nocraft.loperd.playerdatasync.spigot.PlayerData;
import org.nocraft.loperd.playerdatasync.spigot.PlayerDataApply;
import org.nocraft.loperd.playerdatasync.spigot.event.PlayerLoadedEvent;
import org.nocraft.loperd.playerdatasync.spigot.manager.LockedPlayerManager;
import org.nocraft.loperd.playerdatasync.spigot.serialization.BukkitSerializer;

import java.util.Optional;
import java.util.UUID;

public class PlayerLoadListener extends DataSyncListenerBukkit {

    private final LockedPlayerManager lockedPlayerManager;
    private final DataSyncPluginBukkit plugin;

    public PlayerLoadListener(DataSyncPluginBukkit plugin) {
        super(plugin);
        this.plugin = plugin;
        this.lockedPlayerManager = plugin.getLockedPlayerManager();
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();
        String name = player.getName();

        this.playerReset(player); // used for developing
        this.lockedPlayerManager.add(uuid);

        Optional<PlayerData> result = this.plugin.getStorage().loadPlayerData(uuid, name).join();

//        .thenAccept(result -> {
            if (!result.isPresent()) {
                this.lockedPlayerManager.remove(uuid);
                return;
            }

            this.plugin.applyPlayerData(result.get());
//        });
    }

    private void playerReset(Player player) {
        player.getActivePotionEffects().clear();
        player.getInventory().clear();
        player.updateInventory();
        player.setFoodLevel(20);
        player.setHealth(20D);
    }

    @EventHandler
    public void onPlayerLoaded(PlayerLoadedEvent e) {
        Player p = e.getPlayer();

        this.lockedPlayerManager.remove(p.getUniqueId());

        plugin.getLogger().info(
                String.format("Successfully apply PlayerData for player %s:%s",
                        p.getName(),
                        p.getUniqueId())
        );
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        UUID uniqueId = player.getUniqueId();

        this.plugin.getStorage().savePlayerData(player).thenAccept(ignored -> {
            this.lockedPlayerManager.remove(uniqueId);
        });
    }
}
