package org.nocraft.loperd.datasync.spigot.listener;

import com.gmail.tracebachi.DeltaRedis.Spigot.Events.DeltaRedisMessageEvent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.nocraft.loperd.datasync.spigot.*;
import org.nocraft.loperd.datasync.spigot.manager.LockedPlayerManager;
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
    private final LockedPlayerManager lockedPlayerManager;
    private final DataSyncPluginBukkit plugin;

    public PlayerEnterListener(DataSyncPluginBukkit plugin) {
        super(plugin);
        this.plugin = plugin;
        this.lockedPlayerManager = plugin.getLockedPlayerManager();

        // Scheduled cleanup tasks for invalid queued players who are not on the server.
        this.plugin.getScheduler().asyncRepeating(this::cleanup, 5L, TimeUnit.SECONDS);
    }

    private void playerReset(Player player) {
        player.getActivePotionEffects().clear();
        player.getInventory().clear();
        player.updateInventory();
        player.setFoodLevel(20);
        player.setHealth(20D);
    }

    private synchronized void cleanup() {
        Iterator<Map.Entry<UUID, QueuedPlayer>> iterator = queue.entrySet().iterator();
        long oldestTime = System.currentTimeMillis() - 5000;

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
        queuedPlayer.setTimeoutTask(this.plugin.getScheduler().asyncLater(() ->
                this.plugin.getStorage().loadPlayerData(uniqueId).thenAccept(
                        result -> this.plugin.applyPlayerData(uniqueId, result)
                ), 2, TimeUnit.SECONDS)
        );

        this.lockedPlayerManager.add(uniqueId);
        this.queue.put(uniqueId, queuedPlayer);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        UUID uniqueId = player.getUniqueId();

        this.playerReset(player);

        QueuedPlayer queuedPlayer = this.queue.remove(uniqueId);
        if (null == queuedPlayer || !queuedPlayer.getData().isPresent()) {
            this.queue.put(uniqueId, queuedPlayer);
            return;
        }

        queuedPlayer.stopTimeout();
        this.plugin.applyPlayerData(queuedPlayer.getData().get());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        UUID uniqueId = player.getUniqueId();

        String data = BukkitSerializer.toByteArray(new PlayerData(player));

        List<String> messageParts = new ArrayList<>(2);
        messageParts.add(uniqueId.toString());
        messageParts.add(data);

        this.plugin.getDeltaRedisApi().publish(DataSyncChannels.POST_INVENTORY, messageParts);
        this.plugin.getStorage().savePlayerData(player).thenAccept(
                ignored -> plugin.getLogger().info("PlayerData for player " + player.getName() + " have successful saved"));

        this.queue.remove(uniqueId);
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
