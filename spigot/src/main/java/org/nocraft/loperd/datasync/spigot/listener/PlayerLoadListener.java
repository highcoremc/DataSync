package org.nocraft.loperd.datasync.spigot.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.nocraft.loperd.datasync.spigot.DataSyncListenerBukkit;
import org.nocraft.loperd.datasync.spigot.DataSyncPluginBukkit;
import org.nocraft.loperd.datasync.spigot.event.PlayerLoadedEvent;
import org.nocraft.loperd.datasync.spigot.manager.LockedPlayerManager;

public class PlayerLoadListener extends DataSyncListenerBukkit {

    private final LockedPlayerManager lockedPlayerManager;
    private final DataSyncPluginBukkit plugin;

    public PlayerLoadListener(DataSyncPluginBukkit plugin) {
        super(plugin);
        this.plugin = plugin;
        this.lockedPlayerManager = plugin.getLockedPlayerManager();
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
}
