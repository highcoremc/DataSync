package org.nocraft.loperd.playerdatasync.Listener;

import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.nocraft.loperd.playerdatasync.Manager.LockedPlayerManager;
import org.nocraft.loperd.playerdatasync.Manager.PlayerDataManager;
import org.nocraft.loperd.playerdatasync.NoPlayerDataSync;
import org.nocraft.loperd.playerdatasync.PlayerData;
import org.nocraft.loperd.playerdatasync.Storage.Storage;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class PlayerLoadListener extends NoListener {

    private final Map<UUID, PlayerData> pendingConnections = new HashMap<>();
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

    @EventHandler
    public void onPreLogin(AsyncPlayerPreLoginEvent e) {
        AsyncPlayerPreLoginEvent.Result loginResult = e.getLoginResult();

        if (!loginResult.equals(AsyncPlayerPreLoginEvent.Result.ALLOWED)) {
            return;
        }

        UUID uuid = e.getUniqueId();
        String name = e.getName();

        PlayerData playerData = this.storage.loadPlayerData(uuid, name).join();

        this.pendingConnections.put(uuid, playerData);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();
        this.lockedPlayerManager.add(uuid);

        if (this.pendingConnections.containsKey(uuid)) {
            PlayerData data = this.pendingConnections.get(uuid);
            this.manager.applyPlayerData(data, player);
            this.lockedPlayerManager.remove(uuid);
            return;
        }

        this.plugin.getScheduler().asyncRepeating(() -> {
            if (!this.pendingConnections.containsKey(uuid)) {
                return;
            }

            PlayerData data = this.pendingConnections.get(uuid);

            this.plugin.getScheduler().sync().execute(() -> {
                Player p = Bukkit.getPlayer(uuid);

                if (p == null) {
                    return;
                }

                manager.applyPlayerData(data, p);
                lockedPlayerManager.remove(uuid);
            });
        }, 1, TimeUnit.SECONDS);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();

        PlayerData playerData = new PlayerData(p);

        plugin.getLogger().info(String.format("Start save playerData to database for user %s with uuid [%s]",
                p.getName(), p.getUniqueId()));

        this.manager.updatePlayerData(playerData, p);

        try {
            this.storage.savePlayerData(playerData).get();
        } catch (ExecutionException | InterruptedException ex) {
            ex.printStackTrace();
        }
    }
}
