/*
 * This file is part of DeltaEssentials.
 *
 * DeltaEssentials is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * DeltaEssentials is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with DeltaEssentials.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.nocraft.loperd.playerdatasync.Domain.Player;

import com.google.common.base.Preconditions;
import lombok.NonNull;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Trace Bachi (tracebachi@gmail.com, BigBossZee) on 12/12/15.
 */
public class SavedPlayerInventory {

    public static final SavedPlayerInventory EMPTY = new SavedPlayerInventory(new ItemStack[4], new ItemStack[36]);

    private final ItemStack[] armor;
    private final ItemStack[] storage;

    public SavedPlayerInventory(Player player) {
        ItemStack[] armor = player.getInventory().getArmorContents();
        ItemStack[] storage = player.getInventory().getStorageContents();

        this.armor = armor;
        this.storage = storage;
    }

    public SavedPlayerInventory(@NonNull ItemStack[] armor, @NonNull ItemStack[] storage) {
        Preconditions.checkArgument(armor.length == 4, "armor size must be 4");
        Preconditions.checkArgument(storage.length == 36, "storage size must be 36");

        this.armor = armor;
        this.storage = storage;
    }

    public ItemStack[] getArmor() {
        return armor;
    }

    public ItemStack[] getStorage() {
        return storage;
    }
}
