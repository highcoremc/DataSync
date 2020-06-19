package org.nocraft.loperd.datasync.common.storage;

import org.nocraft.loperd.datasync.common.DataSyncPlayer;
import org.nocraft.loperd.datasync.common.Throwing;
import org.nocraft.loperd.datasync.common.plugin.DataSyncPlugin;
import org.nocraft.loperd.datasync.common.storage.implementation.StorageImplementation;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

/**
 * Provides a {@link CompletableFuture} based API for interacting with a {@link StorageImplementation}.
 */
public class Storage implements StorageAdapter<DataSyncPlayer, String> {
    private final DataSyncPlugin plugin;
    private final StorageImplementation implementation;

    public Storage(DataSyncPlugin plugin, StorageImplementation implementation) {
        this.plugin = plugin;
        this.implementation = implementation;
    }

    public StorageImplementation getImplementation() {
        return this.implementation;
    }

    public Collection<StorageImplementation> getImplementations() {
        return Collections.singleton(this.implementation);
    }

    private <T> CompletableFuture<T> makeFuture(Callable<T> supplier) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return supplier.call();
            } catch (Exception e) {
                if (e instanceof RuntimeException) {
                    throw (RuntimeException) e;
                }
                throw new CompletionException(e);
            }
        }, this.plugin.getScheduler().async());
    }

    private CompletableFuture<Void> makeFuture(Throwing.Runnable runnable) {
        return CompletableFuture.runAsync(() -> {
            try {
                runnable.run();
            } catch (Exception e) {
                if (e instanceof RuntimeException) {
                    throw (RuntimeException) e;
                }
                throw new CompletionException(e);
            }
        }, this.plugin.getScheduler().async());
    }

    public String getName() {
        return this.implementation.getImplementationName();
    }

    public void init() {
        try {
            this.implementation.init();
        } catch (Exception e) {
            this.plugin.getLogger().severe("Failed to init storage implementation");
            e.printStackTrace();
        }
    }

    public void shutdown() {
        try {
            this.implementation.shutdown();
        } catch (Exception e) {
            this.plugin.getLogger().severe("Failed to shutdown storage implementation");
            e.printStackTrace();
        }
    }

    @Override
    public CompletableFuture<Optional<String>> loadPlayerData(UUID uniqueId) {
        return makeFuture(() -> this.implementation.loadPlayerData(uniqueId));
    }

    @Override
    public CompletableFuture<Void> savePlayerData(DataSyncPlayer player) {
        return makeFuture(() -> this.implementation.savePlayerData(player));
    }
}
