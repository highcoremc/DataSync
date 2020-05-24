package org.nocraft.loperd.playerdatasync.Serializer;

import org.bukkit.inventory.ItemStack;
import org.nocraft.loperd.playerdatasync.Domain.Serializer.ItemStackSerializer;
import org.nocraft.loperd.playerdatasync.Domain.Serializer.ItemStackSerializerType;

import java.io.IOException;

public class JsonItemStackSerializer implements ItemStackSerializer {

    @Override
    public String serialize(ItemStack[] items) {
        return null;
    }

    @Override
    public ItemStack[] deserialize(String serialized) throws IOException {
        return new ItemStack[0];
    }

    @Override
    public ItemStackSerializerType getType() {
        return ItemStackSerializerType.JSON;
    }
}
