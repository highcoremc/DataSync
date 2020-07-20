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
import org.nocraft.loperd.datasync.spigot.event.PlayerAppliedEvent;
import org.nocraft.loperd.datasync.spigot.event.PlayerNewbieEvent;
import org.nocraft.loperd.datasync.spigot.manager.LockedPlayerManager;
import org.nocraft.loperd.datasync.spigot.player.PlayerData;
import org.nocraft.loperd.datasync.spigot.player.QueuedPlayer;
import org.nocraft.loperd.datasync.spigot.serialization.BukkitSerializer;
import org.nocraft.loperd.datasync.spigot.serialization.VersionMismatchException;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class PlayerEnterListener extends DataSyncListenerBukkit {

    private final Map<UUID, QueuedPlayer> queue = new ConcurrentHashMap<>();
    private final Map<UUID, PlayerData> keepData = new ConcurrentHashMap<>();

    private final DataSyncPluginBukkit plugin;
    private final Set<UUID> skipList = new HashSet<>();

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

        LockedPlayerManager lockedPlayerManager =
                this.plugin.getLockedPlayerManager();
        lockedPlayerManager.add(uniqueId);
    }

    private void createTimeoutTask(UUID uniqueId) {
        try {
            Optional<PlayerData> result = this.plugin.getStorage()
                    .loadPlayerData(uniqueId).get();

            if (!result.isPresent()) {
                Optional<Player> optional =
                        plugin.getBootstrap().getPlayer(uniqueId);
                optional.ifPresent(player -> this.plugin.callEvent(
                        new PlayerNewbieEvent(player)));
                plugin.getLogger().info("Event for PlayerNewbie was fired.");
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
        } catch (InterruptedException | ExecutionException ex) {
            ex.printStackTrace();

            this.skipList.add(uniqueId);
            handleFailedLoadPlayerData(uniqueId);
        }
    }

    private void handleFailedLoadPlayerData(UUID uniqueId) {
        this.plugin.getScheduler().sync().execute(() -> {
            Optional<Player> p = this.plugin.getBootstrap().getPlayer(uniqueId);

            if (!p.isPresent()) {
                return;
            }

            p.get().kickPlayer("§cFAIL§f - §fПожалуйста, перезайдите на сервер :3");
        });
    }

    @EventHandler
    public void onPlayerLoaded(PlayerAppliedEvent e) {
        this.queue.remove(e.getPlayer().getUniqueId());
        this.keepData.remove(e.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onPlayerNewbie(PlayerNewbieEvent e) {
        Player p = e.getPlayer();
        PlayerData data = this.keepData.remove(p.getUniqueId());
        plugin.getLogger().info("Event for PlayerNewbie was accepted.");

        if (null != data) {
            plugin.getLogger().info("Event for PlayerNewbie was handled.");
            this.plugin.applyPlayerData(data);
            return;
        }

        LockedPlayerManager lockedPlayerManager =
                this.plugin.getLockedPlayerManager();
        lockedPlayerManager.remove(p.getUniqueId());
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        UUID uniqueId = player.getUniqueId();

        this.keepPlayerData(player);
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

    private void keepPlayerData(Player player) {
        this.keepData.put(player.getUniqueId(), new PlayerData(player));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        UUID uniqueId = player.getUniqueId();

        this.plugin.getPlayerSaveManager().remove(player);

        if (this.skipList.remove(player.getUniqueId())) {
            return;
        }

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
        }
    }
}
