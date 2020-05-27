package org.nocraft.loperd.playerdatasync.common.player;

import lombok.NonNull;

import java.util.UUID;

public class PlayerData {

    private final String name;
    private final UUID uuid;

    private PlayerXp xp = new PlayerXp();
    private PlayerHealth health = new PlayerHealth();
    private PlayerGameMode gameMode = PlayerGameMode.SURVIVAL;

    private String potionEffects;
    private String savedInventory;
    private String enderChest;

    private int heldItemSlot = 0;
    private boolean vanished = false;
    private boolean flight = false;

    public PlayerData(@NonNull PlayerId playerId) {
        this.name = playerId.getName();
        this.uuid = playerId.getUuid();
    }

    public PlayerData(@NonNull PlayerId playerId, @NonNull PlayerXp xp) {
        this(playerId);
        this.xp = xp;
    }

    public PlayerData(@NonNull PlayerId playerId, @NonNull PlayerHealth health) {
        this(playerId);
        this.health = health;
    }

    public PlayerData(@NonNull PlayerId playerId, @NonNull PlayerXp xp, @NonNull PlayerHealth health) {
        this(playerId);
        this.xp = xp;
        this.health = health;
    }

    public UUID uuid() {
        return this.uuid;
    }

    public String username() {
        return this.name;
    }

    public PlayerHealth health() {
        return this.health;
    }

    public PlayerXp xp() {
        return this.xp;
    }

    public String enderChest() {
        return this.enderChest;
    }

    public String potionEffects() {
        return this.potionEffects;
    }

    public String savedInventory() {
        return this.savedInventory;
    }

    public PlayerGameMode gameMode() {
        return this.gameMode;
    }

    public int heldItemSlot() {
        return this.heldItemSlot;
    }

    public boolean isVanished() {
        return this.vanished;
    }

    public boolean isFlight() {
        return this.flight;
    }

    public PlayerData changeGameMode(PlayerGameMode gameMode) {
        this.gameMode = gameMode;
        return this;
    }

    public PlayerData changePotionEffects(String potionEffects) {
        this.potionEffects = potionEffects;
        return this;
    }

    public PlayerData changeSavedInventory(String savedInventory) {
        this.savedInventory = savedInventory;
        return this;
    }

    public PlayerData changeEnderChest(String enderChest) {
        this.enderChest = enderChest;
        return this;
    }

    public PlayerData changeHeldItemSlot(int heldItemSlot) {
        this.heldItemSlot = heldItemSlot;
        return this;
    }

    public PlayerData changeVanishStatus(boolean vanished) {
        this.vanished = vanished;
        return this;
    }

    public PlayerData changeFlightStatus(boolean flight) {
        this.flight = flight;
        return this;
    }

    public PlayerData changeHealth(PlayerHealth health) {
        this.health = health;
        return this;
    }

    public PlayerData changeXp(PlayerXp xp) {
        this.xp = xp;
        return this;
    }
}
