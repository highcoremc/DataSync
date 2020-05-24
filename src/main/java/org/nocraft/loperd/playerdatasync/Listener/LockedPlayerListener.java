package org.nocraft.loperd.playerdatasync.Listener;

import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.scheduler.BukkitTask;
import org.nocraft.loperd.playerdatasync.Manager.LockedPlayerManager;
import org.nocraft.loperd.playerdatasync.NoPlayerDataSync;

import static com.gmail.tracebachi.DeltaRedis.Shared.ChatMessageHelper.format;

public class LockedPlayerListener extends NoListener {

    private LockedPlayerManager locked;

    public LockedPlayerListener(NoPlayerDataSync plugin, LockedPlayerManager locked) {
        super(plugin);
        this.locked = locked;
    }

    @Override
    public void shutdown() {
        this.locked.clear();
        this.locked = null;

        super.shutdown();
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onInventoryOpen(InventoryOpenEvent event) {
        HumanEntity player = event.getPlayer();

        if (this.locked.getLockedPlayers().containsKey(player.getUniqueId())) {
            player.sendMessage(format("PlayerLocked"));
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onInventoryInteract(InventoryInteractEvent event) {
        HumanEntity player = event.getWhoClicked();

        if (this.locked.getLockedPlayers().containsKey(player.getUniqueId())) {
            player.sendMessage(format("PlayerLocked"));
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (this.locked.getLockedPlayers().containsKey(player.getUniqueId())) {
            player.sendMessage(format("PlayerLocked"));
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerDropItemEvent(PlayerDropItemEvent event) {
        Player player = event.getPlayer();

        if (this.locked.getLockedPlayers().containsKey(player.getUniqueId())) {
            player.sendMessage(format("PlayerLocked"));
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerPickupItemEvent(PlayerPickupItemEvent event) {
        Player player = event.getPlayer();

        if (this.locked.getLockedPlayers().containsKey(player.getUniqueId())) {
            player.sendMessage(format("PlayerLocked"));
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerDamagedEvent(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity();

        if (this.locked.getLockedPlayers().containsKey(player.getUniqueId())) {
            player.sendMessage(format("PlayerLocked"));
            event.setCancelled(true);
        }
    }
}
