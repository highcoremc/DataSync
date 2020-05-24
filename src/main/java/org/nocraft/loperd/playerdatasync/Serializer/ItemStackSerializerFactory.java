package org.nocraft.loperd.playerdatasync.Serializer;

import org.nocraft.loperd.playerdatasync.Domain.Serializer.ItemStackSerializer;
import org.nocraft.loperd.playerdatasync.Domain.Serializer.ItemStackSerializerType;

public class ItemStackSerializerFactory {
    public static ItemStackSerializer create(ItemStackSerializerType type) {
        switch (type) {
            case BS_64:
                return new Bs64ItemStackSerializer();
            case JSON:
                return new JsonItemStackSerializer();
        }

        throw new IllegalStateException("Can not create serializer instance.");
    }
}
