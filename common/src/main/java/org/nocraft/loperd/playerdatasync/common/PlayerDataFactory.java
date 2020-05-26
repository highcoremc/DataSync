package org.nocraft.loperd.playerdatasync.common;

import org.nocraft.loperd.playerdatasync.common.player.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class PlayerDataFactory {

    public PlayerDataFactory() {
    }

    public static PlayerData create(UUID uniqueId, String username) {
        return new PlayerData(new PlayerId(uniqueId, username));
    }

    public static void applyFromDataBase(PlayerData pd, ResultSet rs) throws SQLException {
        PlayerHealth health = new PlayerHealth(rs.getDouble("health"), rs.getInt("food_level"));
        PlayerXp xp = new PlayerXp(rs.getInt("xp_level"), rs.getFloat("xp_progress"));
        PlayerGameMode gm = PlayerGameMode.valueOf(rs.getString("game_mode"));

        pd.changePotionEffects(rs.getString("potion_effects"));
        pd.changeSavedInventory(rs.getString("inventory"));
        pd.changeEnderChest(rs.getString("ender_chest"));

        pd.changeXp(xp);
        pd.changeGameMode(gm);
        pd.changeHealth(health);
        pd.changeFlightStatus(rs.getBoolean("flight"));
        pd.changeHeldItemSlot(rs.getInt("held_item_slot"));
    }
}
