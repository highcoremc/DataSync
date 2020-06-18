package org.nocraft.loperd.datasync.common.storage.implementation;

import org.nocraft.loperd.datasync.common.DataSyncPlayer;
import org.nocraft.loperd.datasync.common.Shutdownable;
import org.nocraft.loperd.datasync.common.plugin.DataSyncPlugin;

import java.util.Optional;
import java.util.UUID;

public interface StorageImplementation extends Shutdownable {
    DataSyncPlugin getPlugin();

    String getImplementationName();

    void init() throws Exception;

    Optional<String> loadPlayerData(UUID uniqueId) throws Exception;

    void savePlayerData(DataSyncPlayer player) throws Exception;
}
