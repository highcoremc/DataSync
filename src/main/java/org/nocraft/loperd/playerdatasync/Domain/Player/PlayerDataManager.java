package org.nocraft.loperd.playerdatasync.Domain.Player;

import lombok.NonNull;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.nocraft.loperd.playerdatasync.NoPlayerDataSync;

public class PlayerDataManager {

    private final NoPlayerDataSync plugin;

    public PlayerDataManager(NoPlayerDataSync plugin) {
        this.plugin = plugin;
    }

    /**
     * Applies all the passed data to the passed player
     * <p>
     * This method applies everything in the file to the player including
     * DeltaEssentials specific settings.
     * </p>
     *
     * @param playerData DeltaEssPlayerData to apply
     * @param player     Player who will get the data
     */
    public void applyPlayerData(@NonNull PlayerData playerData, @NonNull Player player) {
        player.setHealth(playerData.getHealth());
        player.setFoodLevel(playerData.getFoodLevel());
        player.setLevel(playerData.getXpLevel());
        player.setExp(playerData.getXpProgress());
        player.getEnderChest().setContents(playerData.getEnderChest());

        for (PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }

        for (PotionEffect effect : playerData.getPotionEffects()) {
            player.addPotionEffect(effect);
        }

        PlayerInventory playerInventory = player.getInventory();
        SavedPlayerInventory survival = playerData.getSavedInventory();
        playerInventory.setStorageContents(survival.getStorage());
        playerInventory.setArmorContents(survival.getArmor());

        if (player.getGameMode() != playerData.getGameMode()) {
            player.setGameMode(playerData.getGameMode());
        }
    }

    /**
     * Updates the passed data with information from the passed player
     * <p>
     * This method updates things like health, xp, and inventory which are
     * not constantly updated while the player is online.
     * </p>
     *
     * @param playerData DeltaEssPlayerData to update
     * @param player     Player to get updated data from
     */
    public void updatePlayerData(@NonNull PlayerData playerData, @NonNull Player player) {
        playerData.setHealth(player.getHealth());
        playerData.setFoodLevel(player.getFoodLevel());
        playerData.setXpLevel(player.getLevel());
        playerData.setXpProgress(player.getExp());
        playerData.setGameMode(player.getGameMode());
        playerData.setPotionEffects(player.getActivePotionEffects());
        playerData.setEnderChest(player.getEnderChest().getContents());
        playerData.setHeldItemSlot(player.getInventory().getHeldItemSlot());

        playerData.setPotionEffects(player.getActivePotionEffects());
        playerData.setSavedInventory(new SavedPlayerInventory(player));
    }
}
