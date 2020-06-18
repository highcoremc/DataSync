package org.nocraft.loperd.datasync.spigot.listener;

import com.gmail.tracebachi.DeltaRedis.Spigot.Events.DeltaRedisMessageEvent;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import org.nocraft.loperd.datasync.spigot.DataSyncChannels;
import org.nocraft.loperd.datasync.spigot.DataSyncListenerBukkit;
import org.nocraft.loperd.datasync.spigot.DataSyncPluginBukkit;
import org.nocraft.loperd.datasync.spigot.event.PlayerLoadedEvent;
import org.nocraft.loperd.datasync.spigot.player.PlayerData;
import org.nocraft.loperd.datasync.spigot.player.QueuedPlayer;
import org.nocraft.loperd.datasync.spigot.serialization.BukkitSerializer;
import org.nocraft.loperd.datasync.spigot.serialization.VersionMismatchException;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class PlayerEnterListener extends DataSyncListenerBukkit {

    private final Map<UUID, QueuedPlayer> queue = new ConcurrentHashMap<>();
    private final DataSyncPluginBukkit plugin;

    public PlayerEnterListener(DataSyncPluginBukkit plugin) {
        super(plugin);
        this.plugin = plugin;

        // Scheduled cleanup tasks for invalid queued players who are not on the server.
        this.plugin.getScheduler().asyncRepeating(this::cleanup, 5L, TimeUnit.SECONDS);
    }

    private void playerReset(Player player) {
        player.getActivePotionEffects().clear();
        player.getInventory().clear();
        player.updateInventory();
        player.setFoodLevel(2);
        player.setHealth(2D);
    }

    private synchronized void cleanup() {
        Iterator<Map.Entry<UUID, QueuedPlayer>> iterator = queue.entrySet().iterator();
        long oldestTime = System.currentTimeMillis() - 7000;

        while (iterator.hasNext()) {
            QueuedPlayer player = iterator.next().getValue();

            if (player.getTimestamp() < oldestTime) {
                iterator.remove();
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerPreLogin(AsyncPlayerPreLoginEvent e) {
        if (e.getLoginResult() != AsyncPlayerPreLoginEvent.Result.ALLOWED) {
            return;
        }

        if (this.plugin.getDeltaRedisApi() == null) {
            e.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
            e.setKickMessage(ChatColor.RED + this.plugin.getName() + " is not enabled! Please contact administrator!");
        }

        UUID uniqueId = e.getUniqueId();

        QueuedPlayer queuedPlayer = new QueuedPlayer(e.getUniqueId());
        queuedPlayer.setTimeoutTask(this.plugin.getScheduler().asyncLater(
                () -> this.createTimeoutTask(uniqueId),
                1100, TimeUnit.MILLISECONDS));

        this.queue.put(uniqueId, queuedPlayer);
    }

    private void createTimeoutTask(UUID uniqueId) {
        Optional<PlayerData> result = this.plugin.getStorage()
                .loadPlayerData(uniqueId)
                .join();

        if (!result.isPresent()) {
            Optional<Player> optional = plugin.getBootstrap()
                    .getPlayer(uniqueId);
            optional.ifPresent(player -> {
                player.setFoodLevel(20);
                player.setHealth(20D);
            });
            return;
        }

        PlayerData data = result.get();
        if (this.plugin.isPlayerOnline(uniqueId)) {
            this.plugin.applyPlayerData(data);
        } else {
            QueuedPlayer player = this.queue.getOrDefault(
                    uniqueId, new QueuedPlayer(uniqueId));
            player.changePlayerData(data);
            this.queue.put(uniqueId, player);
        }
    }

    @EventHandler
    public void onPlayerLoaded(PlayerLoadedEvent e) {
        this.queue.remove(e.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        UUID uniqueId = player.getUniqueId();

        this.playerReset(player);

        if (!this.queue.containsKey(uniqueId)) {
            this.plugin.getLogger().info("Player " + uniqueId + "  is not contains in queue.");
            return;
        }

        player.sendActionBar('&', "&2&lLoading player data, please wait...");

        QueuedPlayer queuedPlayer = this.queue
                .get(uniqueId);
        queuedPlayer.getData().ifPresent(data -> {
            queuedPlayer.stopTimeout();
            this.queue.remove(uniqueId);
            this.plugin.applyPlayerData(data);
        });
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        UUID uniqueId = player.getUniqueId();

        this.plugin.getPlayerSaveManager().remove(player);

        if (null == this.queue.remove(uniqueId)) {
            String data = BukkitSerializer.toByteArray(new PlayerData(player));

            List<String> messageParts = new ArrayList<>(2);
            messageParts.add(uniqueId.toString());
            messageParts.add(data);

            this.plugin.getDeltaRedisApi().publish(DataSyncChannels.POST_INVENTORY, messageParts);
            this.plugin.getStorage().savePlayerData(player).thenAccept(
                    ignored -> plugin.getLogger().info("PlayerData for player " + player.getName() + " have successful saved"));
        }
    }

    @EventHandler
    @SuppressWarnings("SwitchStatementWithTooFewBranches")
    public void onDeltaRedisMessage(DeltaRedisMessageEvent e) throws IOException, VersionMismatchException, ClassNotFoundException {
        switch (e.getChannel()) {
            case DataSyncChannels.POST_INVENTORY:
                List<String> parts = e.getMessageParts();
                UUID uniqueId = UUID.fromString(parts.get(0));
                PlayerData data = BukkitSerializer.fromByteArray(parts.get(1));

                if (this.queue.containsKey(uniqueId)) {
                    this.queue.remove(uniqueId).stopTimeout();
                }

                if (!this.plugin.getBootstrap().getPlayer(uniqueId).isPresent()) {
                    QueuedPlayer queuedPlayer = new QueuedPlayer(uniqueId);
                    queuedPlayer.changePlayerData(data);
                    this.queue.put(uniqueId, queuedPlayer);
                } else {
                    this.plugin.applyPlayerData(data);
                }
                break;
            default:
                this.plugin.getLogger().warn("Accepted message from unregistered channel " + e.getChannel());
        }
    }
}
