package org.nocraft.loperd.playerdatasync.spigot.listener;

import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.nocraft.loperd.playerdatasync.common.Registerable;
import org.nocraft.loperd.playerdatasync.common.Shutdownable;
import org.nocraft.loperd.playerdatasync.spigot.DataSyncBootstrapBukkit;
import org.nocraft.loperd.playerdatasync.spigot.DataSyncPluginBukkit;

public abstract class DataSyncListenerBukkit implements Registerable, Shutdownable, Listener {

    private DataSyncPluginBukkit plugin;

    public DataSyncListenerBukkit(DataSyncPluginBukkit plugin) {
        this.plugin = plugin;
    }

    public void register() {
        this.plugin.getBootstrap().registerListener(this);
    }

    public void unregister() {
        HandlerList.unregisterAll(this);
    }

    public void shutdown() {
        this.plugin = null;
    }
}
