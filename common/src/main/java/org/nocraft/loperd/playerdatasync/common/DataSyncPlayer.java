package org.nocraft.loperd.playerdatasync.common;

import lombok.Data;

import java.util.UUID;

@Data
public class DataSyncPlayer {
    /**
     * Username of a player in lowercase.
     */
    public final String username;

    /**
     * UUID of player.
     */
    public final UUID playerId;

    /**
     * Serialized data in byte array.
     */
    public final String data;
}
