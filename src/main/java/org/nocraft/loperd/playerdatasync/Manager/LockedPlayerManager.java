package org.nocraft.loperd.playerdatasync.Manager;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;
import org.nocraft.loperd.playerdatasync.Domain.Shutdownable;
import org.nocraft.loperd.playerdatasync.NoPlayerDataSync;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LockedPlayerManager implements Shutdownable {

    private Map<UUID, Long> locked = new HashMap<>();
    private BukkitTask cleanupTask;

    public LockedPlayerManager(NoPlayerDataSync plugin) {
        this.cleanupTask = Bukkit.getScheduler()
                .runTaskTimer(plugin, this::cleanup, 20, 20);
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

    private void cleanup() {
        Long current = System.currentTimeMillis();
        this.locked.entrySet().removeIf(entry -> entry.getValue() < current);
    }

    public void clear() {
        this.locked.clear();
    }
}
