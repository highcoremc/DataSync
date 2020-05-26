package org.nocraft.loperd.playerdatasync.common.player;

import lombok.NonNull;

import java.util.UUID;

public class PlayerId {
    private final String name;
    private final UUID uuid;

    public PlayerId(@NonNull String name, @NonNull UUID uuid) {
        this.name = name;
        this.uuid = uuid;
    }

    public PlayerId(@NonNull UUID uuid, @NonNull String name) {
        this.name = name;
        this.uuid = uuid;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }
}
