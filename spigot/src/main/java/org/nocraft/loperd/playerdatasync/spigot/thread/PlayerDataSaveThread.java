package org.nocraft.loperd.playerdatasync.spigot.thread;

import lombok.SneakyThrows;
import org.bukkit.entity.Player;
import org.nocraft.loperd.playerdatasync.spigot.PDSyncPluginBukkit;
import org.nocraft.loperd.playerdatasync.spigot.runnable.PlayerDataSaver;

import java.util.Collection;

public class PlayerDataSaveThread extends Thread {

    private volatile boolean terminateRequested = false;
    private final PDSyncPluginBukkit plugin;

    public PlayerDataSaveThread(PDSyncPluginBukkit plugin) {
        this.plugin = plugin;
    }

    @SneakyThrows
    @Override
    public void run() {
        plugin.getLogger().info("Start PlayerDataSave Thread for joined players.");

        while (!this.terminateRequested) {
            Collection<? extends Player> players = this.plugin.getBootstrap().getServer().getOnlinePlayers();

            for (Player p : players) {
                Runnable runnable = new PlayerDataSaver(plugin, p);
                this.plugin.getScheduler().sync().execute(runnable);
            }

            Thread.sleep(1500L);
        }
    }

    public void terminate() {
        this.terminateRequested = true;
    }
}
