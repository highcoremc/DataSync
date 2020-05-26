package org.nocraft.loperd.playerdatasync.spigot.manager;

import lombok.NonNull;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.nocraft.loperd.playerdatasync.common.player.*;
import org.nocraft.loperd.playerdatasync.spigot.inventory.SavedPlayerInventory;
import org.nocraft.loperd.playerdatasync.spigot.serializer.PlayerSerializer;

import java.io.IOException;
import java.util.List;

public final class PlayerDataManager {

    private final PlayerSerializer playerSerializer;

    public PlayerDataManager(PlayerSerializer playerSerializer) {
        this.playerSerializer = playerSerializer;
    }

    public void applyPlayerData(@NonNull PlayerData playerData, @NonNull Player player) {
        player.setFoodLevel(playerData.health().getFoodLevel());
        player.setHealth(playerData.health().getHealth());
        player.setExp(playerData.xp().getProgress());
        player.setLevel(playerData.xp().getLevel());

        this.setGameModeForPlayer(player, playerData.gameMode());
        this.applyPotionEffects(player, playerData.potionEffects());
        this.setEnderChestForPlayer(player, playerData.enderChest());
        this.setInventoryForPlayer(player, playerData.savedInventory());
    }

    private void setGameModeForPlayer(Player player, PlayerGameMode gameMode) {
         player.setGameMode(GameMode.valueOf(gameMode.toString()));
    }

    private void setEnderChestForPlayer(Player player, String enderChest) {
        try {
            ItemStack[] contents = this.playerSerializer.deserializeEnderChest(enderChest);
            player.getEnderChest().setContents(contents);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void setInventoryForPlayer(Player player, String serializedInventory) {
        try {
            SavedPlayerInventory inv = this.playerSerializer.deserializeInventory(serializedInventory);
            player.getInventory().setStorageContents(inv.getStorage());
            player.getInventory().setArmorContents(inv.getArmor());
            player.getInventory().setExtraContents(inv.getExtra());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void applyPotionEffects(Player player, String serializedEffects) {
        List<PotionEffect> effects = this.playerSerializer.deserializePotionEffects(serializedEffects);

        player.getActivePotionEffects().clear();

        for (PotionEffect effect : effects) {
            player.addPotionEffect(effect);
        }
    }

    public PlayerData createPlayerData(@NonNull Player p) {
        PlayerHealth health = new PlayerHealth(p.getFoodLevel(), p.getHealth());
        PlayerId playerId = new PlayerId(p.getName(), p.getUniqueId());
        PlayerXp xp = new PlayerXp(p.getLevel(), p.getExp());
        PlayerData playerData = new PlayerData(playerId, xp, health);

        String potionEffects = this.playerSerializer.serializePotionEffects(p.getActivePotionEffects());
        String enderChestContents = this.playerSerializer.serializeEnderChest(p.getEnderChest().getContents());

        playerData.changeGameMode(PlayerGameMode.valueOf(p.getGameMode().toString()));
        playerData.changeHeldItemSlot(p.getInventory().getHeldItemSlot());
        playerData.changeEnderChest(enderChestContents);
        playerData.changePotionEffects(potionEffects);

        playerData.changeSavedInventory(this.playerSerializer.serializeInventory(p));

        return playerData;
    }
}
