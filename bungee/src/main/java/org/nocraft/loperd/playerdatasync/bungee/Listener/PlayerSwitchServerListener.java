package org.nocraft.loperd.playerdatasync.bungee.Listener;

import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.event.EventHandler;
import org.nocraft.loperd.playerdatasync.bungee.PDSyncPluginBungee;

public class PlayerSwitchServerListener extends PDSyncListenerBungee {

    public final PDSyncPluginBungee plugin;

    public PlayerSwitchServerListener(PDSyncPluginBungee plugin) {
        super(plugin);
        this.plugin = plugin;
    }

    @EventHandler
    public void onServerSwitch(ServerSwitchEvent e) {
        ServerInfo from = e.getFrom();
    }
}
