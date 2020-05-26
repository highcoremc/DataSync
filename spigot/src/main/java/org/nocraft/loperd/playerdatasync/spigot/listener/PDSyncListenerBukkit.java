package org.nocraft.loperd.playerdatasync.spigot.listener;

import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.nocraft.loperd.playerdatasync.common.Registerable;
import org.nocraft.loperd.playerdatasync.common.Shutdownable;
import org.nocraft.loperd.playerdatasync.spigot.PDSyncBootstrapBukkit;
import org.nocraft.loperd.playerdatasync.spigot.PDSyncPluginBukkit;

public abstract class PDSyncListenerBukkit implements Registerable, Shutdownable, Listener {

    private PDSyncPluginBukkit plugin;

    public PDSyncListenerBukkit(PDSyncPluginBukkit plugin) {
        this.plugin = plugin;
    }

    public void register() {
        ((PDSyncBootstrapBukkit) this.plugin.getBootstrap()).registerListener(this);
    }

    public void unregister() {
        HandlerList.unregisterAll(this);
    }

    public void shutdown() {
        this.plugin = null;
    }
}
