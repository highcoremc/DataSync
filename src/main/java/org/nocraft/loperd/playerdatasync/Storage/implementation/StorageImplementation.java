package org.nocraft.loperd.playerdatasync.Storage.implementation;

import org.jetbrains.annotations.Nullable;
import org.nocraft.loperd.playerdatasync.PlayerData;
import org.nocraft.loperd.playerdatasync.Domain.Shutdownable;
import org.nocraft.loperd.playerdatasync.NoPlayerDataSync;

import java.util.*;

public interface StorageImplementation extends Shutdownable {
    NoPlayerDataSync getPlugin();

    String getImplementationName();

    void init() throws Exception;

    PlayerData loadPlayerData(UUID uniqueId, String username) throws Exception;

    void savePlayerData(PlayerData playerData, String query) throws Exception;

    @Nullable UUID getPlayerUniqueId(String username) throws Exception;

    @Nullable String getPlayerName(UUID uniqueId) throws Exception;
}
