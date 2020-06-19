package org.nocraft.loperd.datasync.spigot.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.nocraft.loperd.datasync.spigot.DataSyncListenerBukkit;
import org.nocraft.loperd.datasync.spigot.DataSyncPluginBukkit;
import org.nocraft.loperd.datasync.spigot.event.PlayerAppliedEvent;
import org.nocraft.loperd.datasync.spigot.event.PlayerApplyEvent;
import org.nocraft.loperd.datasync.spigot.event.PlayerNewbieEvent;
import org.nocraft.loperd.datasync.spigot.manager.LockedPlayerManager;

import java.util.UUID;

public class PlayerLoadListener extends DataSyncListenerBukkit {

    private final DataSyncPluginBukkit plugin;

    public PlayerLoadListener(DataSyncPluginBukkit plugin) {
        super(plugin);
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerLoad(PlayerApplyEvent e) {
        Player player = e.getPlayer();
        LockedPlayerManager lockedPlayerManager =
                this.plugin.getLockedPlayerManager();
        lockedPlayerManager.add(player.getUniqueId());
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerLoaded(PlayerAppliedEvent e) {
        Player p = e.getPlayer();
        p.sendActionBar("");

        UUID uniqueId = p.getUniqueId();
        LockedPlayerManager lockedPlayerManager =
                this.plugin.getLockedPlayerManager();

        lockedPlayerManager.remove(uniqueId);
        this.plugin.getPlayerSaveManager().add(p);

        plugin.getLogger().info(
                String.format("Successfully apply PlayerData for player %s:%s",
                        p.getName(),
                        uniqueId)
        );
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onNewbie(PlayerNewbieEvent e) {
        this.plugin.getPlayerSaveManager().add(e.getPlayer());
    }
}
