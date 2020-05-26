package org.nocraft.loperd.playerdatasync.spigot.serializer;

import com.google.gson.JsonParser;
import lombok.NonNull;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.nocraft.loperd.playerdatasync.spigot.inventory.SavedPlayerInventory;
import org.jetbrains.annotations.NotNull;
import org.bukkit.inventory.ItemStack;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

public class PlayerSerializer {

    private final ItemStackSerializer baseSerializer;

    public PlayerSerializer(ItemStackSerializer serializer) {
        this.baseSerializer = serializer;
    }

    public String serializeInventory(@NotNull SavedPlayerInventory inventory) throws IllegalStateException {
        //get the main content part, this doesn't return the armor
        String content = baseSerializer.serialize(inventory.getStorage());
        String armor = baseSerializer.serialize(inventory.getArmor());
        String extra = baseSerializer.serialize(inventory.getExtra());

        return serializePlayerInventoryContents(content, armor, extra);
    }

    public String serializeInventory(@NotNull Player player) throws IllegalStateException {
        String storage = baseSerializer.serialize(player.getInventory().getStorageContents());
        String armor = baseSerializer.serialize(player.getInventory().getArmorContents());
        String extra = baseSerializer.serialize(player.getInventory().getExtraContents());

        if (storage == null || armor == null) {
            throw new IllegalStateException("Can not understand type of serialized value");
        }

        return serializePlayerInventoryContents(storage, armor, extra);
    }

    private String serializePlayerInventoryContents(
            @NonNull String storage,
            @NonNull String armor,
            @NonNull String extra
    ) {
        JsonObject result = new JsonObject();

        result.add("type", new JsonPrimitive(baseSerializer.getType().toString()));
        result.add("contents", new JsonPrimitive(storage));
        result.add("armor", new JsonPrimitive(armor));
        result.add("extra", new JsonPrimitive(extra));

        return result.toString();
    }

    public SavedPlayerInventory deserializeInventory(String serializedInventory) throws IOException {
        JsonObject jsonInventory = (JsonObject) new JsonParser().parse(serializedInventory);
        String contentsValue = jsonInventory.get("contents").getAsString();
        String serializerType = jsonInventory.get("type").getAsString();
        String armorValue = jsonInventory.get("armor").getAsString();
        String extraValue = jsonInventory.get("extra").getAsString();

        ItemStackSerializerType type = ItemStackSerializerType.valueOf(serializerType);
        ItemStackSerializer serializer = ItemStackSerializerFactory.create(type);

        ItemStack[] inventory = serializer.deserialize(contentsValue);
        ItemStack[] armor = serializer.deserialize(armorValue);
        ItemStack[] extra = serializer.deserialize(extraValue);

        return new SavedPlayerInventory(armor, inventory, extra);
    }

    public String serializePotionEffects(@NotNull Collection<PotionEffect> effects) {
        return PotionEffectsSerializer.toJsonObject(effects).toString();
    }

    public List<PotionEffect> deserializePotionEffects(@NonNull String serializedPotionEffects) {
        return PotionEffectsSerializer.toEffectList(serializedPotionEffects);
    }

    public String serializeEnderChest(ItemStack[] contents) {
        String storage = baseSerializer.serialize(contents);

        if (storage == null) {
            throw new IllegalStateException("Can not understand type of serialized value");
        }

        JsonObject result = new JsonObject();

        result.add("type", new JsonPrimitive(baseSerializer.getType().toString()));
        result.add("contents", new JsonPrimitive(storage));

        return result.toString();
    }

    public ItemStack[] deserializeEnderChest(@NonNull String contents) throws IOException {
        JsonObject jsonInventory = (JsonObject) new JsonParser().parse(contents);
        String contentsValue = jsonInventory.get("contents").getAsString();
        String serializerType = jsonInventory.get("type").getAsString();

        ItemStackSerializerType type = ItemStackSerializerType.valueOf(serializerType);
        ItemStackSerializer serializer = ItemStackSerializerFactory.create(type);

        return serializer.deserialize(contentsValue);
    }
}
