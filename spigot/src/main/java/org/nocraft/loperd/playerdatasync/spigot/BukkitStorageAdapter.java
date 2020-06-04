package org.nocraft.loperd.datasync.spigot;

import org.bukkit.entity.Player;
import org.nocraft.loperd.datasync.common.DataSyncPlayer;
import org.nocraft.loperd.datasync.common.storage.Storage;
import org.nocraft.loperd.datasync.common.storage.StorageAdapter;
import org.nocraft.loperd.datasync.common.storage.StorageFactory;
import org.nocraft.loperd.datasync.spigot.serialization.BukkitSerializer;
import org.nocraft.loperd.datasync.spigot.serialization.VersionMismatchException;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class BukkitStorageAdapter implements StorageAdapter<Player, PlayerData> {

    private final Storage storage;

    public BukkitStorageAdapter(StorageFactory storage) {
        this.storage = storage.getInstance();
    }

    @Override
    public CompletableFuture<Optional<PlayerData>> loadPlayerData(UUID uniqueId) {
        return this.storage.loadPlayerData(uniqueId).thenApply(data -> {
            if (!data.isPresent()) {
                return Optional.empty();
            }

            try {
                return Optional.of(BukkitSerializer.fromByteArray(data.get()));
            } catch (VersionMismatchException | IOException | ClassNotFoundException e) {
                return Optional.empty();
            }
        });
    }

    @Override
    public CompletableFuture<Void> savePlayerData(Player p) {
        String data = BukkitSerializer.toByteArray(new PlayerData(p));
        return this.storage.savePlayerData(new DataSyncPlayer(
                p.getName(), p.getUniqueId(), data)
        );
    }

    public void shutdown() {
        this.storage.shutdown();
    }
}
