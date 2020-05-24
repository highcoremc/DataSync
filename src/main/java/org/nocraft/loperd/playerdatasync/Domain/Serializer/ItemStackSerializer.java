package org.nocraft.loperd.playerdatasync.Domain.Serializer;

import org.bukkit.inventory.ItemStack;

import java.io.IOException;

public interface ItemStackSerializer {

    String serialize(ItemStack[] items);

    ItemStack[] deserialize(String serialized) throws IOException;

    ItemStackSerializerType getType();
}
