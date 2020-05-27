package org.nocraft.loperd.playerdatasync.spigot.runnable;

import lombok.NonNull;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.nocraft.loperd.playerdatasync.common.player.PlayerData;
import org.nocraft.loperd.playerdatasync.common.player.PlayerGameMode;
import org.nocraft.loperd.playerdatasync.spigot.PDSyncPluginBukkit;
import org.nocraft.loperd.playerdatasync.spigot.event.PlayerLoadedEvent;
import org.nocraft.loperd.playerdatasync.spigot.inventory.SavedPlayerInventory;
import org.nocraft.loperd.playerdatasync.spigot.serializer.PlayerSerializer;

import java.io.IOException;
import java.util.List;

public class PlayerDataApplier implements Runnable {

    private final PlayerData data;
    private final Player player;
    private final PlayerSerializer serializer;
    private final PDSyncPluginBukkit plugin;

    public PlayerDataApplier(PDSyncPluginBukkit plugin, PlayerData data, Player player) {
        this.data = data;
        this.player = player;
        this.serializer = plugin.getPlayerSerializer();
        this.plugin = plugin;
    }

    @Override
    public void run() {
        this.player.setFoodLevel(this.data.health().getFoodLevel());
        this.player.setHealth(this.data.health().getHealth());
        this.player.setExp(this.data.xp().getProgress());
        this.player.setLevel(this.data.xp().getLevel());

        this.setGameModeForPlayer(this.player, this.data.gameMode());
        this.applyPotionEffects(this.player, this.data.potionEffects());
        this.setEnderChestForPlayer(this.player, this.data.enderChest());
        this.setInventoryForPlayer(this.player, this.data.savedInventory());

        PlayerLoadedEvent event = new PlayerLoadedEvent(player);
        this.plugin.getBootstrap().getServer().getPluginManager().callEvent(event);
    }

    private void setGameModeForPlayer(Player player, PlayerGameMode gameMode) {
        player.setGameMode(GameMode.valueOf(gameMode.toString()));
    }

    private void setEnderChestForPlayer(Player player, @NonNull String enderChest) {
        try {
            ItemStack[] contents = this.serializer.deserializeEnderChest(enderChest);
            player.getEnderChest().setContents(contents);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void setInventoryForPlayer(Player player, @NonNull String serializedInventory) {
        try {
            SavedPlayerInventory inv = this.serializer.deserializeInventory(serializedInventory);
            player.getInventory().setStorageContents(inv.getStorage());
            player.getInventory().setArmorContents(inv.getArmor());
            player.getInventory().setExtraContents(inv.getExtra());
            player.updateInventory();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void applyPotionEffects(Player player, @NonNull String serializedEffects) {
        List<PotionEffect> effects = this.serializer.deserializePotionEffects(serializedEffects);

        player.getActivePotionEffects().clear();

        for (PotionEffect effect : effects) {
            player.addPotionEffect(effect);
        }
    }
}
