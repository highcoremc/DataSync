package org.nocraft.loperd.playerdatasync.common.storage.implementation.custom;

import org.nocraft.loperd.playerdatasync.common.plugin.PDSyncPlugin;
import org.nocraft.loperd.playerdatasync.common.storage.implementation.StorageImplementation;

/**
 * A storage provider
 */
@FunctionalInterface
public interface CustomStorageProvider {
    StorageImplementation provide(PDSyncPlugin plugin);
}
