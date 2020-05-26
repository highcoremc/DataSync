package org.nocraft.loperd.playerdatasync.spigot.inventory;

import com.google.common.base.Preconditions;
import org.bukkit.inventory.ItemStack;
import org.bukkit.entity.Player;
import lombok.NonNull;

public class SavedPlayerInventory {

    private final ItemStack[] armor;
    private final ItemStack[] storage;
    private final ItemStack[] extra;

    public SavedPlayerInventory(Player player) {
        ItemStack[] armor = player.getInventory().getArmorContents();
        ItemStack[] extra = player.getInventory().getExtraContents();
        ItemStack[] storage = player.getInventory().getStorageContents();

        this.armor = armor;
        this.extra = extra;
        this.storage = storage;
    }

    public SavedPlayerInventory(@NonNull ItemStack[] armor, @NonNull ItemStack[] storage, @NonNull ItemStack[] extra) {
        Preconditions.checkArgument(armor.length == 4, "armor size must be 4");
        Preconditions.checkArgument(storage.length == 36, "storage size must be 36");

        this.extra = extra;
        this.armor = armor;
        this.storage = storage;
    }

    public ItemStack[] getExtra() {
        return extra;
    }

    public ItemStack[] getArmor() {
        return armor;
    }

    public ItemStack[] getStorage() {
        return storage;
    }
}
