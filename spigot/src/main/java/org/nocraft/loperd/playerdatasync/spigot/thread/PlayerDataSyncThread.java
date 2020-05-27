package org.nocraft.loperd.playerdatasync.spigot.thread;

import lombok.SneakyThrows;
import org.bukkit.entity.Player;
import org.nocraft.loperd.playerdatasync.spigot.PDSyncPluginBukkit;
import org.nocraft.loperd.playerdatasync.spigot.PlayerDataLife;
import org.nocraft.loperd.playerdatasync.spigot.runnable.PlayerDataApplier;

import java.util.Optional;
import java.util.concurrent.BlockingQueue;

public class PlayerDataSyncThread extends Thread {

    private final PDSyncPluginBukkit plugin;
    private volatile boolean terminateRequested = false;
    private final long lifeTime = 5000L;

    public PlayerDataSyncThread(PDSyncPluginBukkit plugin) {
        this.plugin = plugin;
    }

    @SneakyThrows
    @Override
    public void run() {
        BlockingQueue<PlayerDataLife> playerSyncQueue = this.plugin.getPlayerSyncQueue();

        plugin.getLogger().info("Start PlayerDataSync Thread for joined players.");

        while (!terminateRequested) {
            if (playerSyncQueue.isEmpty()) {
                Thread.sleep(500L);
                continue;
            }

            try {
                PlayerDataLife data = playerSyncQueue.take();
                Optional<Player> player = this.plugin.getBootstrap().getPlayer(data.get().uuid());

                if ((System.currentTimeMillis() - data.getCreatedAt()) > this.lifeTime) {
                    return;
                }

                if (!player.isPresent()) {
                    playerSyncQueue.offer(data);
                    return;
                }

                plugin.getLogger().info(
                        String.format("Start applying PlayerData for player %s:%s",
                                player.get().getName(),
                                player.get().getUniqueId())
                );

                Runnable runnable = new PlayerDataApplier(plugin, data.get(), player.get());

                this.plugin.getScheduler().sync().execute(runnable);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void terminate() {
        this.terminateRequested = true;
    }
}
