package org.nocraft.loperd.playerdatasync.spigot;

import org.nocraft.loperd.playerdatasync.spigot.scheduler.AbstractJavaScheduler;
import org.nocraft.loperd.playerdatasync.common.scheduler.SchedulerAdapter;

import java.util.concurrent.Executor;

public class BukkitSchedulerAdapter extends AbstractJavaScheduler implements SchedulerAdapter {

    protected Executor sync;

    public BukkitSchedulerAdapter(PDSyncBootstrapBukkit plugin) {
        this.sync = r -> plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, r);
    }

    @Override
    public Executor sync() {
        return this.sync;
    }
}
