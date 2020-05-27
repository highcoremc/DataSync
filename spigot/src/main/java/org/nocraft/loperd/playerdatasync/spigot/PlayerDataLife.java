package org.nocraft.loperd.playerdatasync.spigot;

import lombok.Getter;
import org.nocraft.loperd.playerdatasync.common.player.PlayerData;

public class PlayerDataLife {

    private final PlayerData playerData;

    @Getter
    private final long createdAt;

    public PlayerDataLife(PlayerData playerData) {
        this.playerData = playerData;
        this.createdAt = System.currentTimeMillis();
    }

    public PlayerData get() {
        return this.playerData;
    }
}
