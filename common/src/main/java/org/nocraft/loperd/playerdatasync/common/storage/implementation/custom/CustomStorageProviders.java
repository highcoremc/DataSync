package org.nocraft.loperd.playerdatasync.common.storage.implementation.custom;

/**
 * Hook to allow external code to provide a storage implementation
 */
public final class CustomStorageProviders {
    private CustomStorageProviders() {}

    private static CustomStorageProvider provider = null;

    public static void register(CustomStorageProvider provider) {
        CustomStorageProviders.provider = provider;
    }

    public static CustomStorageProvider getProvider() {
        if (provider == null) {
            throw new IllegalStateException("Provider not present.");
        }

        return provider;
    }

}
