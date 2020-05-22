package org.nocraft.loperd.playerdatasync.Storage.implementation.custom;

import org.nocraft.loperd.playerdatasync.NoPlayerDataSync;
import org.nocraft.loperd.playerdatasync.Storage.implementation.StorageImplementation;

/**
 * A storage provider
 */
@FunctionalInterface
public interface CustomStorageProvider {
    StorageImplementation provide(NoPlayerDataSync plugin);
}
