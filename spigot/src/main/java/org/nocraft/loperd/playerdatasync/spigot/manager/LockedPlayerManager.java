package org.nocraft.loperd.playerdatasync.spigot.manager;

import org.nocraft.loperd.playerdatasync.common.Shutdownable;
import org.nocraft.loperd.playerdatasync.common.scheduler.SchedulerTask;
import org.nocraft.loperd.playerdatasync.spigot.PDSyncPluginBukkit;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class LockedPlayerManager implements Shutdownable {

    private Map<UUID, Long> locked = new HashMap<>();
    private SchedulerTask cleanupTask;

    public LockedPlayerManager(PDSyncPluginBukkit plugin) {
        this.cleanupTask = plugin.getScheduler().asyncRepeating(this::cleanup, 1L, TimeUnit.SECONDS);
    }

    @Override
    public void shutdown() {
        this.cleanupTask.cancel();
        this.cleanupTask = null;
        this.locked.clear();
        this.locked = null;
    }

    public void add(UUID uuid) {
        add(uuid, Long.MAX_VALUE);
    }

    public void add(UUID uuid, long endTime) {
        this.locked.put(uuid, endTime);
    }

    public synchronized Map<UUID, Long> getLockedPlayers() {
        return this.locked;
    }

    public Long remove(UUID uuid) {
        return this.locked.remove(uuid);
    }

    public boolean isLocked(UUID uuid) {
        return this.locked.containsKey(uuid);
    }

    private synchronized void cleanup() {
        Long current = System.currentTimeMillis();
        this.locked.entrySet().removeIf(entry -> entry.getValue() < current);
    }

    public void clear() {
        this.locked.clear();
    }
}
