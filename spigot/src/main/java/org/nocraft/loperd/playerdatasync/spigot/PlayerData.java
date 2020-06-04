package org.nocraft.loperd.datasync.spigot;

import lombok.Getter;
import lombok.ToString;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.Vector;

import java.io.Serializable;
import java.util.Collection;
import java.util.UUID;

@ToString
@Getter
public class PlayerData implements Serializable {
    private final long timeStamp = System.currentTimeMillis();
    private final UUID playerId;
    private final String playerName;
    private final int totalExperience;
    private final int level;
    private final float exp;
    private final ItemStack[] inventory;
    private final ItemStack[] enderchest;
    private final Collection<PotionEffect> potionEffects;
    private final double maxHealth;
    private final double health;
    private final boolean isHealthScaled;
    private final double healthScale;
    private final int foodLevel;
    private final float exhaustion;
    private final int maxAir;
    private final int remainingAir;
    private final int fireTicks;
    private final int maxNoDamageTicks;
    private final int noDamageTicks;
    private final Vector velocity;
    private final int heldItemSlot;

    public PlayerData(Player player) {
        this.playerId = player.getUniqueId();
        this.playerName = player.getName();
        this.totalExperience = player.getTotalExperience();
        this.level = player.getLevel();
        this.exp = player.getExp();
        this.inventory = player.getInventory().getContents();
        this.enderchest = player.getEnderChest().getContents();
        this.potionEffects = player.getActivePotionEffects();
        this.maxHealth = player.getMaxHealth();
        this.health = player.getHealth();
        this.isHealthScaled = player.isHealthScaled();
        this.healthScale = player.getHealthScale();
        this.foodLevel = player.getFoodLevel();
        this.exhaustion = player.getExhaustion();
        this.maxAir = player.getMaximumAir();
        this.remainingAir = player.getRemainingAir();
        this.fireTicks = player.getFireTicks();
        this.maxNoDamageTicks = player.getMaximumNoDamageTicks();
        this.noDamageTicks = player.getNoDamageTicks();
        this.velocity = player.getVelocity();
        this.heldItemSlot = player.getInventory().getHeldItemSlot();
    }
}
