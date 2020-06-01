package org.nocraft.loperd.playerdatasync.common.storage.implementation;

import org.nocraft.loperd.playerdatasync.common.DataSyncPlayer;
import org.nocraft.loperd.playerdatasync.common.Shutdownable;
import org.nocraft.loperd.playerdatasync.common.plugin.DataSyncPlugin;

import java.util.Optional;
import java.util.UUID;

public interface StorageImplementation extends Shutdownable {
    DataSyncPlugin getPlugin();

    String getImplementationName();

    void init() throws Exception;

    Optional<String> loadPlayerData(UUID uniqueId, String username) throws Exception;

    void savePlayerData(DataSyncPlayer player) throws Exception;
}
