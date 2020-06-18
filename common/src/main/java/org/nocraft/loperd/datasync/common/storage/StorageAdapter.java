package org.nocraft.loperd.datasync.common.storage;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface StorageAdapter<I, O> {
    CompletableFuture<Optional<O>> loadPlayerData(UUID uniqueId);

    CompletableFuture<Void> savePlayerData(I p);
}
