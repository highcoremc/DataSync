package org.nocraft.loperd.playerdatasync.Domain.Player;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;
import org.nocraft.loperd.playerdatasync.Listener.NoListener;
import org.nocraft.loperd.playerdatasync.NoPlayerDataSync;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LockedPlayerManager extends NoListener {

    private Map<UUID, Long> locked = new HashMap<>();
    private BukkitTask cleanupTask;

    public LockedPlayerManager(NoPlayerDataSync plugin) {
        super(plugin);

        this.cleanupTask = Bukkit.getScheduler()
                .runTaskTimer(plugin, this::cleanup, 20, 20);
    }

    @Override
    public void shutdown() {
        cleanupTask.cancel();
        cleanupTask = null;
        locked.clear();
        locked = null;

        super.shutdown();
    }

    public void add(UUID uuid) {
        add(uuid, Long.MAX_VALUE);
    }

    public void add(UUID uuid, long endTime) {
        locked.put(uuid, endTime);
    }

    public Map<UUID, Long> getLockedPlayers() {
        return locked;
    }

    public Long remove(UUID uuid) {
        return locked.remove(uuid);
    }

    public boolean isLocked(UUID uuid) {
        return locked.containsKey(uuid);
    }

    private void cleanup() {
        Long current = System.currentTimeMillis();
        locked.entrySet().removeIf(entry -> entry.getValue() < current);
    }

    public void clear() {
        this.locked.clear();
    }
}
