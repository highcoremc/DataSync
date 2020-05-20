package org.nocraft.loperd.playerdatasync.Listener;

import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.nocraft.loperd.playerdatasync.NoPlayerDataSync;
import org.nocraft.loperd.playerdatasync.Domain.Registerable;
import org.nocraft.loperd.playerdatasync.Domain.Shutdownable;

public abstract class NoListener implements Registerable, Shutdownable, Listener {

    private NoPlayerDataSync plugin;

    public NoListener(NoPlayerDataSync plugin) {
        this.plugin = plugin;
    }

    public void register() {
        this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
    }

    public void unregister() {
        HandlerList.unregisterAll(this);
    }

    public void shutdown() {
        this.plugin = null;
    }
}
