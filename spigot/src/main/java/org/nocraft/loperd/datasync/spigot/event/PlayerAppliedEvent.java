package org.nocraft.loperd.datasync.spigot.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class PlayerAppliedEvent extends Event {

    private final Player player;

    public PlayerAppliedEvent(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }


    /**
     * Used by Bukkit and Spigot
     */
    private static final HandlerList handlers = new HandlerList();


    /**
     * Used by Bukkit and Spigot
     */
    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    /**
     * Used by Bukkit and Spigot
     */
    public static HandlerList getHandlerList() {
        return handlers;
    }
}
