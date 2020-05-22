package org.nocraft.loperd.playerdatasync.Domain;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;

public interface InventorySerializer <T, Inv extends Inventory> {

    T serializeInventory(Inv inventory) throws IllegalStateException;

    Inv deserializeInventory(T serializedInventory) throws IOException;

    T serializeItemStacks(ItemStack[] items) throws IllegalStateException;

    ItemStack[] deserializeItemStack(T serialized) throws IllegalStateException, IOException;
}
