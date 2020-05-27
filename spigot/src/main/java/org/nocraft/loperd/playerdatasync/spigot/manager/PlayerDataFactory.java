package org.nocraft.loperd.playerdatasync.spigot.manager;

import lombok.NonNull;
import org.bukkit.entity.Player;
import org.nocraft.loperd.playerdatasync.common.player.*;
import org.nocraft.loperd.playerdatasync.spigot.serializer.PlayerSerializer;

public final class PlayerDataFactory {

    private final PlayerSerializer playerSerializer;

    public PlayerDataFactory(PlayerSerializer playerSerializer) {
        this.playerSerializer = playerSerializer;
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
