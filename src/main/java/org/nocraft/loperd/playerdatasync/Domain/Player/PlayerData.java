package org.nocraft.loperd.playerdatasync.Domain.Player;

import lombok.NonNull;
import org.bukkit.GameMode;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

public class PlayerData {

    private final String name;
    private final UUID uuid;

    private double health = 20.00D;
    private int foodLevel = 20;
    private int xpLevel = 0;
    private float xpProgress = 0.00F;
    private GameMode gameMode = GameMode.SURVIVAL;
    private Collection<PotionEffect> potionEffects = Collections.emptyList();
    private SavedPlayerInventory savedInventory = SavedPlayerInventory.EMPTY;
    private ItemStack[] enderChest;
    private int heldItemSlot = 0;
    private boolean denyingTeleports = false;
    private boolean vanished = false;
    private boolean flight = false;

    public PlayerData(@NonNull String name, @NonNull UUID uuid) {
        this.name = name;
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public UUID getUuid() {
        return uuid;
    }

    public double getHealth() {
        return health;
    }

    public void setHealth(double health) {
        this.health = health;
    }

    public int getFoodLevel() {
        return foodLevel;
    }

    public void setFoodLevel(int foodLevel) {
        this.foodLevel = foodLevel;
    }

    public int getXpLevel() {
        return xpLevel;
    }

    public void setXpLevel(int xpLevel) {
        this.xpLevel = xpLevel;
    }

    public float getXpProgress() {
        return xpProgress;
    }

    public void setXpProgress(float xpProgress) {
        this.xpProgress = xpProgress;
    }

    public GameMode getGameMode() {
        return gameMode;
    }

    public void setGameMode(@NonNull GameMode gameMode) {
        this.gameMode = gameMode;
    }

    public Collection<PotionEffect> getPotionEffects() {
        return potionEffects;
    }

    public void setPotionEffects(@NonNull Collection<PotionEffect> potionEffects) {
        this.potionEffects = potionEffects;
    }

    public ItemStack[] getEnderChest() {
        return enderChest;
    }

    public void setEnderChest(@NonNull ItemStack[] enderChest) {
        this.enderChest = enderChest;
    }

    public int getHeldItemSlot() {
        return heldItemSlot;
    }

    public void setHeldItemSlot(int heldItemSlot) {
        this.heldItemSlot = heldItemSlot;
    }

    public boolean isDenyingTeleports() {
        return denyingTeleports;
    }

    public void setDenyingTeleports(boolean denyingTeleports) {
        this.denyingTeleports = denyingTeleports;
    }

    public boolean isVanished() {
        return vanished;
    }

    public void setVanished(boolean vanished) {
        this.vanished = vanished;
    }

    public boolean isFlying() {
        return this.flight;
    }

    public void setFlight(boolean flight) {
        this.flight = flight;
    }

    public void setSavedInventory(SavedPlayerInventory savedInventory) {
        this.savedInventory = savedInventory;
    }

    public SavedPlayerInventory getSavedInventory() {
        return this.savedInventory;
    }
}
