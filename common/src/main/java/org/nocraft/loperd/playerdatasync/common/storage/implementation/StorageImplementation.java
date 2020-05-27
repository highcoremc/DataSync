package org.nocraft.loperd.playerdatasync.common.storage.implementation;

import org.jetbrains.annotations.Nullable;
import org.nocraft.loperd.playerdatasync.common.plugin.PDSyncPlugin;
import org.nocraft.loperd.playerdatasync.common.player.PlayerData;
import org.nocraft.loperd.playerdatasync.common.Shutdownable;

import java.util.*;

public interface StorageImplementation extends Shutdownable {
    PDSyncPlugin getPlugin();

    String getImplementationName();

    void init() throws Exception;

    Optional<PlayerData> loadPlayerData(UUID uniqueId, String username) throws Exception;

    void savePlayerData(PlayerData playerData) throws Exception;

    @Nullable UUID getPlayerUniqueId(String username) throws Exception;

    @Nullable String getPlayerName(UUID uniqueId) throws Exception;
}
