package org.nocraft.loperd.playerdatasync;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.nocraft.loperd.playerdatasync.Inventory.SavedPlayerInventory;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

public class PlayerData {

    @Getter
    private final String name;

    @Getter
    private final UUID uuid;

    @Getter
    @Setter
    private double health = 20.00D;

    @Getter
    @Setter
    private int foodLevel = 20;

    @Getter
    @Setter
    private int xpLevel = 0;

    @Getter
    @Setter
    private float xpProgress = 0.00F;

    @Getter
    @Setter
    private GameMode gameMode = GameMode.SURVIVAL;

    @Getter
    @Setter
    private Collection<PotionEffect> potionEffects = Collections.emptyList();

    @Getter
    @Setter
    private SavedPlayerInventory savedInventory = SavedPlayerInventory.EMPTY;

    @Getter
    @Setter
    private ItemStack[] enderChest = new ItemStack[27];

    @Getter
    @Setter
    private int heldItemSlot = 0;

    @Setter
    private boolean denyingTeleports = false;

    @Setter
    private boolean vanished = false;

    @Setter
    private boolean flight = false;

    public PlayerData(@NonNull UUID uuid, @NonNull String name) {
        this.name = name;
        this.uuid = uuid;
    }

    public PlayerData(Player p) {
        this.name = p.getName();
        this.uuid = p.getUniqueId();
    }

    public boolean isDenyingTeleports() {
        return denyingTeleports;
    }
    public boolean isVanished() {
        return vanished;
    }
    public boolean isFlight() {
        return this.flight;
    }
}
