package org.nocraft.loperd.playerdatasync.Listener;

import lombok.NonNull;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.nocraft.loperd.playerdatasync.Domain.Player.LockedPlayerManager;
import org.nocraft.loperd.playerdatasync.NoPlayerDataSync;
import org.nocraft.loperd.playerdatasync.Domain.Player.PlayerData;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerLoadListener extends NoListener {

    private final Map<UUID, PlayerData> pendingConnections = new HashMap<>();
    private final LockedPlayerManager lockedPlayerManager;
    private NoPlayerDataSync plugin;

    public PlayerLoadListener(NoPlayerDataSync plugin, LockedPlayerManager lockedPlayerManager) {
        super(plugin);
        this.plugin = plugin;
        this.lockedPlayerManager = lockedPlayerManager;
    }

    @EventHandler
    public void onPreLogin(AsyncPlayerPreLoginEvent event) {
        AsyncPlayerPreLoginEvent.Result loginResult = event.getLoginResult();

        if (!loginResult.equals(AsyncPlayerPreLoginEvent.Result.ALLOWED)) {
            return;
        }

        UUID uuid = event.getUniqueId();
        // TODO: exact player from database and past him data to PendingConnections hashmap.

    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        if (!this.pendingConnections.containsKey(uuid)) {
            return;
        }

        loadPlayer(player);
    }

    private void loadPlayer(@NonNull Player player) {
        UUID uuid = player.getUniqueId();
        lockedPlayerManager.add(uuid);

//        PlayerLoad runnable = new PlayerLoad(
//                playerName,
//                playerDataFile,
//                new PlayerLoadCallbacks(playerName));
//        DeltaExecutor.instance().execute(runnable);
    }

//    private class PlayerLoadCallbacks implements PlayerLoad.Callbacks {
//        private final String playerName;
//
//        public PlayerLoadCallbacks(@NonNull String playerName) {
//            this.playerName = playerName;
//        }
//
//        @Override
//        public void onSuccess(DeltaEssPlayerData playerData) {
//            Bukkit.getScheduler().runTask(plugin, () ->
//            {
//                Player player = Bukkit.getPlayerExact(playerName);
//                if (player != null) {
//                    // Unlock the player
//                    lockedPlayerManager.remove(playerName);
//
//                    // Save the playerData
//                    plugin.getPlayerDataMap().put(playerName, playerData);
//
//                    // Apply the playerData
//                    playerDataHelper.applyPlayerData(playerData, player);
//
//                    // Fire a PlayerPostLoadEvent
//                    PlayerPostLoadEvent postLoadEvent = new PlayerPostLoadEvent(
//                            player,
//                            playerData.getMetaData());
//                    Bukkit.getPluginManager().callEvent(postLoadEvent);
//                }
//            });
//        }
//
//        @Override
//        public void onNotFoundFailure() {
//            Bukkit.getScheduler().runTask(plugin, () ->
//            {
//                Player player = Bukkit.getPlayerExact(playerName);
//                if (player != null) {
//                    // Unlock the player
//                    lockedPlayerManager.remove(playerName);
//
//                    // Build new playerData
//                    DeltaEssPlayerData playerData = new DeltaEssPlayerData(playerName);
//                    SavedPlayerInventory playerInventory = new SavedPlayerInventory(player);
//
//                    // Set the gamemode as the default on the server
//                    playerData.setGameMode(settings.getDefaultGameMode());
//
//                    // Save the current EnderChest
//                    playerData.setEnderChest(player.getEnderChest().getContents());
//
//                    // Save the current inventory based on GameMode
//                    if (player.getGameMode() == GameMode.SURVIVAL) {
//                        playerData.setSurvival(playerInventory);
//                    } else if (player.getGameMode() == GameMode.CREATIVE) {
//                        playerData.setCreative(playerInventory);
//                    }
//
//                    // Save the playerData
//                    plugin.getPlayerDataMap().put(playerName, playerData);
//
//                    // Apply the playerData
//                    playerDataHelper.applyPlayerData(playerData, player);
//
//                    PlayerPostLoadEvent postLoadEvent = new PlayerPostLoadEvent(
//                            player,
//                            playerData.getMetaData(),
//                            true);
//                    Bukkit.getPluginManager().callEvent(postLoadEvent);
//                }
//            });
//        }
//
//        @Override
//        public void onExceptionFailure(Exception ex) {
//            ex.printStackTrace();
//
//            Bukkit.getScheduler().runTask(plugin, () ->
//            {
//                Player player = Bukkit.getPlayerExact(playerName);
//                if (player != null) {
//                    lockedPlayerManager.remove(playerName);
//
//                    player.sendMessage(format("DeltaEss.FailedToLoadInventory"));
//                }
//            });
//        }
//    }
}
