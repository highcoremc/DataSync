package org.nocraft.loperd.playerdatasync.bungee.Listener;

import net.md_5.bungee.api.plugin.Listener;
import org.nocraft.loperd.playerdatasync.bungee.PDSyncPluginBungee;
import org.nocraft.loperd.playerdatasync.common.Registerable;
import org.nocraft.loperd.playerdatasync.common.Shutdownable;

public abstract class PDSyncListenerBungee implements Registerable, Shutdownable, Listener {
    private PDSyncPluginBungee plugin;

    public PDSyncListenerBungee(PDSyncPluginBungee plugin) {
        this.plugin = plugin;
    }

    @Override
    public void register() {
        this.plugin.registerEvents(this);
    }

    @Override
    public void unregister() {
        this.plugin.unregisterEvents(this);
    }

    @Override
    public void shutdown() {
        this.plugin = null;
    }
}
