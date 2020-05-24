package org.nocraft.loperd.playerdatasync.Listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.nocraft.loperd.playerdatasync.Manager.LockedPlayerManager;
import org.nocraft.loperd.playerdatasync.Manager.PlayerDataManager;
import org.nocraft.loperd.playerdatasync.NoPlayerDataSync;
import org.nocraft.loperd.playerdatasync.PlayerData;
import org.nocraft.loperd.playerdatasync.Storage.Storage;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class PlayerLoadListener extends NoListener {

    private final LockedPlayerManager lockedPlayerManager;
    private final NoPlayerDataSync plugin;
    private final Storage storage;
    private final PlayerDataManager manager;

    public PlayerLoadListener(NoPlayerDataSync plugin, Storage storage, LockedPlayerManager lockedPlayerManager) {
        super(plugin);
        this.plugin = plugin;
        this.storage = storage;
        this.lockedPlayerManager = lockedPlayerManager;
        this.manager = new PlayerDataManager(plugin);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();
        String name = player.getName();

        this.lockedPlayerManager.add(uuid);

        PlayerData data = this.storage.loadPlayerData(uuid, name).join();

        this.manager.applyPlayerData(data, player);
        this.lockedPlayerManager.remove(uuid);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        PlayerData playerData = new PlayerData(p);

        plugin.getLogger().info(String.format("Start save playerData to database for user %s with uuid [%s]",
                p.getName(), p.getUniqueId()));

        this.manager.updatePlayerData(playerData, p);

        this.storage.savePlayerData(playerData).join();
        plugin.getLogger().info(String.format("Save playerData successfully for player %s", p.getName()));
    }
}
