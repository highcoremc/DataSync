package org.nocraft.loperd.playerdatasync.bungee;

import org.nocraft.loperd.playerdatasync.bungee.Listener.PDSyncListenerBungee;
import org.nocraft.loperd.playerdatasync.bungee.Listener.PlayerServerChangeListener;
import org.nocraft.loperd.playerdatasync.common.Composer;
import org.nocraft.loperd.playerdatasync.common.config.Configuration;
import org.nocraft.loperd.playerdatasync.common.plugin.PDSyncBootstrap;
import org.nocraft.loperd.playerdatasync.common.plugin.PDSyncPlugin;
import org.nocraft.loperd.playerdatasync.common.plugin.PluginLogger;
import org.nocraft.loperd.playerdatasync.common.scheduler.SchedulerAdapter;

public class PDSyncPluginBungee implements PDSyncPlugin {

    private final Composer<PDSyncListenerBungee> listeners = new Composer<>();
    private final PDSyncBootstrapBungee bootstrap;

    public PDSyncPluginBungee(PDSyncBootstrapBungee bootstrap) {
        this.bootstrap = bootstrap;
    }

    public final void enable() {
        this.listeners.add(new PlayerServerChangeListener(this));
        this.listeners.register();
    }

    public final void disable() {
        this.listeners.unregister();
        this.listeners.shutdown();
    }

    @Override
    public PluginLogger getLogger() {
        return getBootstrap().getPluginLogger();
    }

    @Override
    public SchedulerAdapter getScheduler() {
        return getBootstrap().getSchedulerAdapter();
    }

    @Override
    public PDSyncBootstrap getBootstrap() {
        return this.bootstrap;
    }

    @Override
    public Configuration getConfiguration() {
        return null;
    }

    public void registerEvents(PDSyncListenerBungee listener) {
        this.bootstrap.getProxy().getPluginManager().registerListener(this.bootstrap, listener);
    }

    public void unregisterEvents(PDSyncListenerBungee listener) {
        this.bootstrap.getProxy().getPluginManager().unregisterListener(listener);
    }
}
