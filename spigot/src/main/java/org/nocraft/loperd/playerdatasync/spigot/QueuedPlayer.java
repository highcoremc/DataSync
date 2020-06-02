package org.nocraft.loperd.playerdatasync.spigot;

import lombok.Getter;
import lombok.Setter;
import org.nocraft.loperd.playerdatasync.common.scheduler.SchedulerTask;

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
