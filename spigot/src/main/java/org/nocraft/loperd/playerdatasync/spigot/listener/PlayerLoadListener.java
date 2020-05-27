package org.nocraft.loperd.playerdatasync.spigot.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.nocraft.loperd.playerdatasync.common.storage.Storage;
import org.nocraft.loperd.playerdatasync.spigot.PDSyncPluginBukkit;
import org.nocraft.loperd.playerdatasync.spigot.PlayerDataLife;
import org.nocraft.loperd.playerdatasync.spigot.event.PlayerLoadedEvent;
import org.nocraft.loperd.playerdatasync.spigot.manager.LockedPlayerManager;
import org.nocraft.loperd.playerdatasync.spigot.runnable.PlayerDataSaver;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PlayerLoadListener extends PDSyncListenerBukkit {

    private final LockedPlayerManager lockedPlayerManager;
    private final PDSyncPluginBukkit plugin;
    private final Storage storage;

    public PlayerLoadListener(PDSyncPluginBukkit plugin, Storage storage) {
        super(plugin);
        this.plugin = plugin;
        this.storage = storage;
        this.lockedPlayerManager = plugin.getLockedPlayerManager();
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();
        String name = player.getName();

        this.playerReset(player);
        this.lockedPlayerManager.add(uuid);

        this.storage.loadPlayerData(uuid, name).thenAccept(result -> {
            if (!result.isPresent()) {
                this.lockedPlayerManager.remove(uuid);
                return;
            }
            this.plugin.getPlayerSyncQueue().offer(new PlayerDataLife(result.get()));
        });
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

        long lastSavedTime = this.plugin.getPlayerDataSaveStatusMap().get(player.getUniqueId());
        long currentTime = System.currentTimeMillis();

        if ((lastSavedTime - currentTime) > 1000) {
            this.plugin.getScheduler().executeAsync(new PlayerDataSaver(plugin, player));
        }

        this.lockedPlayerManager.remove(player.getUniqueId());
        this.plugin.getPlayerDataSaveStatusMap().remove(player.getUniqueId());
    }
}
