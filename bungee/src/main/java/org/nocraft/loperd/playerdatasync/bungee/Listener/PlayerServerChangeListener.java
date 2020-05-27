package org.nocraft.loperd.playerdatasync.bungee.Listener;

import org.nocraft.loperd.playerdatasync.bungee.PDSyncPluginBungee;

public class PlayerServerChangeListener extends PDSyncListenerBungee {

    public final PDSyncPluginBungee plugin;

    public PlayerServerChangeListener(PDSyncPluginBungee plugin) {
        super(plugin);
        this.plugin = plugin;
    }
}
