package org.nocraft.loperd.playerdatasync.common.storage;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface StorageAdapter<I, O> {
    CompletableFuture<Optional<O>> loadPlayerData(UUID uniqueId, String username);

    CompletableFuture<Void> savePlayerData(I p);
}
