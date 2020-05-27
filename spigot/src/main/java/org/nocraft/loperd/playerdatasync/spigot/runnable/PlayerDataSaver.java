package org.nocraft.loperd.playerdatasync.spigot.runnable;

import org.bukkit.entity.Player;
import org.nocraft.loperd.playerdatasync.common.storage.Storage;
import org.nocraft.loperd.playerdatasync.spigot.PDSyncPluginBukkit;
import org.nocraft.loperd.playerdatasync.spigot.manager.PlayerDataFactory;

import java.util.Map;
import java.util.UUID;

public class PlayerDataSaver implements Runnable {

    private final Map<UUID, Long> playerDataSaveStatusMap;
    private final PlayerDataFactory playerDataFactory;
    private final Storage storage;
    private final Player player;

    public PlayerDataSaver(PDSyncPluginBukkit plugin, Player p) {
        this.player = p;
        this.storage = plugin.getStorage();
        this.playerDataFactory = plugin.getPlayerDataFactory();
        this.playerDataSaveStatusMap = plugin.getPlayerDataSaveStatusMap();
    }

    @Override
    public void run() {
        this.storage.savePlayerData(this.playerDataFactory.createPlayerData(player));
        this.playerDataSaveStatusMap.put(player.getUniqueId(), System.currentTimeMillis());
    }
}
