package org.nocraft.loperd.playerdatasync.spigot;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

public class PlayerDataApply implements Runnable {

    private final PlayerData data;
    private final Player player;
    private Runnable finalize;

    public PlayerDataApply(Player player, PlayerData data) {
        this.data = data;
        this.player = player;
    }

    public PlayerDataApply(Player player, PlayerData data, Runnable finalize) {
        this(player, data);
        this.finalize = finalize;
    }

    @Override
    public void run() {
        this.resetPlayer(this.player);

        this.player.setTotalExperience(data.getTotalExperience());
        this.player.setLevel(data.getLevel());
        this.player.setExp(data.getExp());
        this.player.getInventory().setContents(data.getInventory());
        this.player.getEnderChest().setContents(data.getEnderchest());
        this.player.setMaxHealth(data.getMaxHealth());
        this.player.setHealth(data.getHealth());
        this.player.setFoodLevel(data.getFoodLevel());
        this.player.setExhaustion(data.getExhaustion());
        this.player.setMaximumAir(data.getMaxAir());
        this.player.setRemainingAir(data.getRemainingAir());
        this.player.setFireTicks(data.getFireTicks());
        this.player.setMaximumNoDamageTicks(data.getMaxNoDamageTicks());
        this.player.setNoDamageTicks(data.getNoDamageTicks());
        this.player.setVelocity(data.getVelocity());
        this.player.addPotionEffects(data.getPotionEffects());
        this.player.setHealthScaled(data.isHealthScaled());
        this.player.setHealthScale(data.getHealthScale());
        this.player.getInventory().setHeldItemSlot(data.getHeldItemSlot());
        this.player.updateInventory();

        if (this.finalize != null) {
            this.finalize.run();
        }
    }

    private void resetPlayer(Player p) {
        p.getEnderChest().clear();
        p.getInventory().clear();
        p.setTotalExperience(0);
        p.setLevel(0);
        p.setExp(0);
        for (PotionEffect effect : p.getActivePotionEffects()) {
            p.removePotionEffect(effect.getType());
        }
        p.resetMaxHealth();
    }
}
