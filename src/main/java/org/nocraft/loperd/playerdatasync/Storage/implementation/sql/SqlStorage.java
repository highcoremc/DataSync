package org.nocraft.loperd.playerdatasync.Storage.implementation.sql;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.bukkit.GameMode;
import org.bukkit.potion.PotionEffect;
import org.nocraft.loperd.playerdatasync.NoPlayerDataSync;
import org.nocraft.loperd.playerdatasync.PlayerData;
import org.nocraft.loperd.playerdatasync.Storage.implementation.StorageImplementation;
import org.nocraft.loperd.playerdatasync.Storage.implementation.sql.connection.ConnectionFactory;
import org.nocraft.loperd.playerdatasync.Serializer.PlayerInventorySerializer;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SqlStorage implements StorageImplementation {

    private static final String INSERT_PLAYERDATA = "INSERT INTO {prefix}users (uuid,name,health,foodLevel,xpLevel,xpProgress,gameMode,potionEffects,inventory,enderChest,heldItemSlot,flight) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String UPDATE_PLAYERDATA = "UPDATE {prefix}users (uuid,name,health,foodLevel,xpLevel,xpProgress,gameMode,potionEffects,inventory,enderChest,heldItemSlot,flight) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String GET_PLAYERDATA = "SELECT * FROM {prefix}users WHERE uuid = ?"; // AND username = ?
    private final NoPlayerDataSync plugin;

    private final Function<String, String> statementProcessor;
    private final ConnectionFactory connectionFactory;
    private final PlayerInventorySerializer serializer;

    public SqlStorage(NoPlayerDataSync plugin, ConnectionFactory connectionFactory, String tablePrefix) {
        this.plugin = plugin;
        this.connectionFactory = connectionFactory;
        this.serializer = plugin.getPlayerInventorySerializer();
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
            try (PreparedStatement ps = c.prepareStatement(this.statementProcessor.apply(GET_PLAYERDATA))) {
                ps.setString(1, uniqueId.toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        data.setHealth(rs.getDouble("health"));
                        data.setFoodLevel(rs.getInt("foodLevel"));
                        data.setXpLevel(rs.getInt("xpLevel"));
                        data.setXpProgress(data.getXpProgress());
                        data.setGameMode(GameMode.valueOf(rs.getString("gameMode")));

                        data.setString(8, potionEffects.getAsString());

                        JsonObject inventory = serializer.serializeInventory(data.getSavedInventory());
                        String enderChest = serializer.serializeItemStack(data.getEnderChest());

                        data.setString(9, inventory.getAsString());
                        data.setString(10, enderChest);
                        data.setInt(11, data.getHeldItemSlot());
                        data.setBoolean(12, data.isFlight());
                    } else {
                        return null;
                    }
                }
            }
        }
        return null;
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

    private void createPlayerData(PlayerData data) throws SQLException {
        savePlayerData(data, INSERT_PLAYERDATA);
    }

    private void updatePlayerdata(PlayerData data) throws SQLException {
        savePlayerData(data, UPDATE_PLAYERDATA);
    }

    public void savePlayerData(PlayerData data, String query) throws SQLException {
        try (Connection c = this.connectionFactory.getConnection()) {
            try (PreparedStatement ps = c.prepareStatement(this.statementProcessor.apply(query))) {
                ps.setString(1, data.getUuid().toString());
                ps.setString(2, data.getName());
                ps.setDouble(3, data.getHealth());
                ps.setFloat(4, data.getFoodLevel());
                ps.setInt(5, data.getXpLevel());
                ps.setDouble(6, data.getXpProgress());
                ps.setString(7, data.getGameMode().toString());

                JsonArray potionEffects = new JsonArray();
                for (PotionEffect potionEffect : data.getPotionEffects()) {
                    potionEffects.add(potionEffect.toString());
                }
                ps.setString(8, potionEffects.getAsString());

                JsonObject inventory = serializer.serializeInventory(data.getSavedInventory());
                String enderChest = serializer.serializeItemStack(data.getEnderChest());

                ps.setString(9, inventory.getAsString());
                ps.setString(10, enderChest);
                ps.setInt(11, data.getHeldItemSlot());
                ps.setBoolean(12, data.isFlight());
                ps.execute();
            }
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
