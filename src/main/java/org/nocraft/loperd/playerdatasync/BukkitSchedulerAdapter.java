package org.nocraft.loperd.playerdatasync;

import org.nocraft.loperd.playerdatasync.Domain.Scheduler.AbstractJavaScheduler;
import org.nocraft.loperd.playerdatasync.Domain.Scheduler.SchedulerAdapter;

import java.util.concurrent.Executor;

public class BukkitSchedulerAdapter extends AbstractJavaScheduler implements SchedulerAdapter {

    protected Executor sync;

    public BukkitSchedulerAdapter(NoPlayerDataSync plugin) {
        this.sync = r -> plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, r);
    }

    @Override
    public Executor sync() {
        return this.sync;
    }
}
