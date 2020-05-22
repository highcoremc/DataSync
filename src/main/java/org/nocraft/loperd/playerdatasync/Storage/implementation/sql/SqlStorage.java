package org.nocraft.loperd.playerdatasync.Storage.implementation.sql;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.GameMode;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.nocraft.loperd.playerdatasync.Inventory.SavedPlayerInventory;
import org.nocraft.loperd.playerdatasync.NoPlayerDataSync;
import org.nocraft.loperd.playerdatasync.PlayerData;
import org.nocraft.loperd.playerdatasync.Serializer.Bs64InventorySerializer;
import org.nocraft.loperd.playerdatasync.Serializer.PotionEffectsSerializer;
import org.nocraft.loperd.playerdatasync.Storage.implementation.StorageImplementation;
import org.nocraft.loperd.playerdatasync.Storage.implementation.sql.connection.ConnectionFactory;
import org.nocraft.loperd.playerdatasync.Serializer.PlayerInventorySerializer;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SqlStorage implements StorageImplementation {

    private static final String INSERT_PLAYER_DATA = "INSERT INTO {prefix}users (uuid,name,health,food_level,xp_level,xp_progress,game_mode,potion_effects,inventory,ender_chest,held_item_slot,flight) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String UPDATE_PLAYER_DATA = "UPDATE {prefix}users SET uuid = ?, name = ?, health = ?, food_level = ?, xp_level = ?, xp_progress = ?, game_mode = ?, potion_effects = ?, inventory = ?, ender_chest = ?, held_item_slot = ?, flight = ? WHERE uuid = ?";
    private static final String GET_PLAYER_DATA = "SELECT * FROM {prefix}users WHERE uuid = ?"; // AND username = ?
    private static final String GET_USERNAME = "SELECT name FROM {prefix}users WHERE uuid = ?";
    private final NoPlayerDataSync plugin;

    private final Function<String, String> statementProcessor;
    private final ConnectionFactory connectionFactory;
    private final PlayerInventorySerializer serializer;

    public SqlStorage(NoPlayerDataSync plugin, ConnectionFactory connectionFactory, String tablePrefix) {
        this.plugin = plugin;
        this.connectionFactory = connectionFactory;
        this.serializer = new PlayerInventorySerializer(new Bs64InventorySerializer());
        this.statementProcessor = connectionFactory.getStatementProcessor().compose(s -> s.replace("{prefix}", tablePrefix));
    }

    @Override
    public NoPlayerDataSync getPlugin() {
        return this.plugin;
    }

    @Override
    public String getImplementationName() {
        return this.connectionFactory.getImplementationName();
    }

    public ConnectionFactory getConnectionFactory() {
        return this.connectionFactory;
    }

    public Function<String, String> getStatementProcessor() {
        return this.statementProcessor;
    }

    @Override
    public void init() throws Exception {
        this.connectionFactory.init(this.plugin);

        boolean tableExists;
        try (Connection c = this.connectionFactory.getConnection()) {
            tableExists = tableExists(c, this.statementProcessor.apply("{prefix}users"));
        }

        if (!tableExists) {
            applySchema();
        }
    }

    @Override
    public PlayerData loadPlayerData(UUID uniqueId, String username) throws Exception {
        PlayerData data = new PlayerData(uniqueId, username);
        try (Connection c = this.connectionFactory.getConnection()) {
            try (PreparedStatement ps = c.prepareStatement(this.statementProcessor.apply(GET_PLAYER_DATA))) {
                ps.setString(1, uniqueId.toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        return data;
                    }

                    data.setHealth(rs.getDouble("health"));
                    data.setXpLevel(rs.getInt("xp_level"));
                    data.setXpProgress(data.getXpProgress());
                    data.setFoodLevel(rs.getInt("food_level"));
                    data.setGameMode(GameMode.valueOf(rs.getString("game_mode")));

                    data.setPotionEffects(PotionEffectsSerializer.deserialize(rs.getString("potion_effects")));

                    data.setSavedInventory(createSavedInventory(rs.getString("inventory")));
                    data.setEnderChest(serializer.deserializeItemStack(rs.getString("ender_chest")));

                    data.setHeldItemSlot(rs.getInt("held_item_slot"));
                    data.setFlight(rs.getBoolean("flight"));
                }
            }
        }

        return data;
    }

    private SavedPlayerInventory createSavedInventory(String contents) throws IOException {
        JsonObject jsonInventory = (JsonObject) new JsonParser().parse(contents);
        List<ItemStack[]> list = serializer.deserializeInventory(jsonInventory);
        return new SavedPlayerInventory(list.get(0), list.get(1));
    }

    private void applySchema() throws IOException, SQLException {
        List<String> statements;

        String schemaFileName = "org/nocraft/loperd/playerdatasync/" + this.connectionFactory.getImplementationName().toLowerCase() + ".sql";
        try (InputStream is = this.plugin.getResourceStream(schemaFileName)) {
            if (is == null) {
                throw new IOException("Couldn't locate schema file for " + this.connectionFactory.getImplementationName());
            }

            statements = SchemaReader.getStatements(is).stream()
                    .map(this.statementProcessor)
                    .collect(Collectors.toList());
        }

        try (Connection connection = this.connectionFactory.getConnection()) {
            boolean utf8mb4Unsupported = false;

            try (Statement s = connection.createStatement()) {
                for (String query : statements) {
                    s.addBatch(query);
                }

                try {
                    s.executeBatch();
                } catch (BatchUpdateException e) {
                    if (e.getMessage().contains("Unknown character set")) {
                        utf8mb4Unsupported = true;
                    } else {
                        throw e;
                    }
                }
            }

            // try again
            if (utf8mb4Unsupported) {
                try (Statement s = connection.createStatement()) {
                    for (String query : statements) {
                        s.addBatch(query.replace("utf8mb4", "utf8"));
                    }

                    s.executeBatch();
                }
            }
        }
    }

    @Override
    public void shutdown() {
        try {
            this.connectionFactory.shutdown();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void savePlayerData(PlayerData data) throws SQLException {
        try (Connection c = this.connectionFactory.getConnection()) {
            try (PreparedStatement ps = c.prepareStatement(this.statementProcessor.apply(GET_USERNAME))) {
                ps.setString(1, data.getUuid().toString());
                try (ResultSet rs = ps.executeQuery()) {
                    savePlayerData(c, data, !rs.next() ? INSERT_PLAYER_DATA : UPDATE_PLAYER_DATA);
                }
            }
        }
    }

    private void savePlayerData(Connection c, PlayerData data, String query) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(this.statementProcessor.apply(query))) {
            ps.setString(1, data.getUuid().toString());
            ps.setString(2, data.getName());
            ps.setDouble(3, data.getHealth());
            ps.setFloat(4, data.getFoodLevel());
            ps.setInt(5, data.getXpLevel());
            ps.setDouble(6, data.getXpProgress());
            ps.setString(7, data.getGameMode().toString());

            JsonArray effects = PotionEffectsSerializer.serialize(data.getPotionEffects());
            ps.setString(8, effects.toString());

            JsonObject inventory = serializer.serializeInventory(data.getSavedInventory());
            String enderChest = serializer.serializeItemStack(data.getEnderChest());

            ps.setString(9, inventory.toString());
            ps.setString(10, enderChest);
            ps.setInt(11, data.getHeldItemSlot());
            ps.setBoolean(12, data.isFlight());

            if (query.equals(UPDATE_PLAYER_DATA)) {
                ps.setString(13, data.getUuid().toString());
            }

            ps.execute();
        }
    }

    @Override
    public UUID getPlayerUniqueId(String username) throws SQLException {
        return null;
    }

    @Override
    public String getPlayerName(UUID uniqueId) throws SQLException {
        return null;
    }

    private static boolean tableExists(Connection connection, String table) throws SQLException {
        try (ResultSet rs = connection.getMetaData().getTables(null, null, "%", null)) {
            while (rs.next()) {
                if (rs.getString(3).equalsIgnoreCase(table)) {
                    return true;
                }
            }
            return false;
        }
    }
}
