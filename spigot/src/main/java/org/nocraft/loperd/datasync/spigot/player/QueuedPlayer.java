package org.nocraft.loperd.datasync.spigot.player;

import lombok.Getter;
import lombok.Setter;
import org.nocraft.loperd.datasync.common.scheduler.SchedulerTask;
import org.nocraft.loperd.datasync.spigot.player.PlayerData;

import java.util.Optional;
import java.util.UUID;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class QueuedPlayer {

    @Getter
    private final long timestamp = System.currentTimeMillis();

    @Getter
    @Setter
    private SchedulerTask timeoutTask;

    @Getter
    private final UUID playerId;

    @Getter
    public Optional<PlayerData> data;

    public QueuedPlayer(UUID playerId) {
        this.playerId = playerId;
        this.data = Optional.empty();
    }

    public void stopTimeout() {
        if (timeoutTask != null) {
            timeoutTask.cancel();
            timeoutTask = null;
        }
    }

    public void changePlayerData(PlayerData data) {
        this.data = Optional.of(data);
    }
}
