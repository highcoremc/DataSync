package org.nocraft.loperd.playerdatasync.spigot.serializer;

public class ItemStackSerializerFactory {
    public static ItemStackSerializer create(ItemStackSerializerType type) {
        switch (type) {
            case BUKKIT:
                return new BukkitItemStackSerializer();
        }

        throw new IllegalStateException("Can not create serializer instance.");
    }
}
