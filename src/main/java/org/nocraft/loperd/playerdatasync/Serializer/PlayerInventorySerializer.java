package org.nocraft.loperd.playerdatasync.Serializer;

import org.jetbrains.annotations.NotNull;
import org.nocraft.loperd.playerdatasync.Domain.Serializer.ItemStackSerializer;
import org.nocraft.loperd.playerdatasync.Domain.Serializer.ItemStackSerializerType;
import org.nocraft.loperd.playerdatasync.Inventory.SavedPlayerInventory;
import org.bukkit.inventory.ItemStack;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonObject;

import java.io.IOException;

public class PlayerInventorySerializer {

    private final ItemStackSerializer baseSerializer;

    public PlayerInventorySerializer(ItemStackSerializer serializer) {
        this.baseSerializer = serializer;
    }

    public JsonObject serialize(@NotNull SavedPlayerInventory inventory) throws IllegalStateException {
        //get the main content part, this doesn't return the armor
        String content = baseSerializer.serialize(inventory.getStorage());
        String armor = baseSerializer.serialize(inventory.getArmor());

        JsonObject result = new JsonObject();

        if (content == null || armor == null) {
            throw new IllegalStateException("Can not understand type of serialized value");
        }

        result.add("type", new JsonPrimitive(baseSerializer.getType().toString()));
        result.add("content", new JsonPrimitive(content));
        result.add("armor", new JsonPrimitive(armor));

        return result;
    }

    public SavedPlayerInventory deserialize(@NotNull JsonObject serializedInventory) throws IOException {
        String contentsValue = serializedInventory.get("content").getAsString();
        String serializerType = serializedInventory.get("type").getAsString();
        String armorValue = serializedInventory.get("armor").getAsString();

        ItemStackSerializerType type = ItemStackSerializerType.valueOf(serializerType);
        ItemStackSerializer serializer = ItemStackSerializerFactory.create(type);

        ItemStack[] inventory = serializer.deserialize(contentsValue);
        ItemStack[] enderChest = serializer.deserialize(armorValue);

        return new SavedPlayerInventory(enderChest, inventory);
    }
}
