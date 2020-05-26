package org.nocraft.loperd.playerdatasync.common.storage.implementation;

import org.jetbrains.annotations.Nullable;
import org.nocraft.loperd.playerdatasync.common.plugin.PDSyncPlugin;
import org.nocraft.loperd.playerdatasync.common.player.PlayerData;
import org.nocraft.loperd.playerdatasync.common.PlayerDataFactory;
import org.nocraft.loperd.playerdatasync.common.storage.implementation.sql.SchemaReader;
import org.nocraft.loperd.playerdatasync.common.storage.implementation.sql.connection.ConnectionFactory;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SqlStorage implements StorageImplementation {

    private static final String INSERT_PLAYER_DATA = "INSERT INTO {prefix}users (uuid,name,health,food_level,xp_level,xp_progress,game_mode,potion_effects,inventory,ender_chest,held_item_slot,flight) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String UPDATE_PLAYER_DATA = "UPDATE {prefix}users SET uuid = ?, name = ?, health = ?, food_level = ?, xp_level = ?, xp_progress = ?, game_mode = ?, potion_effects = ?, inventory = ?, ender_chest = ?, held_item_slot = ?, flight = ? WHERE uuid = ?";
    private static final String GET_PLAYER_DATA = "SELECT * FROM {prefix}users WHERE uuid = ?"; // AND username = ?
    private static final String GET_USERNAME = "SELECT name FROM {prefix}users WHERE uuid = ?";

    private final PDSyncPlugin plugin;

    private final Function<String, String> statementProcessor;
    private final ConnectionFactory connectionFactory;

    public SqlStorage(PDSyncPlugin plugin, ConnectionFactory connectionFactory, String tablePrefix) {
        this.plugin = plugin;
        this.connectionFactory = connectionFactory;
        this.statementProcessor = connectionFactory.getStatementProcessor().compose(s -> s.replace("{prefix}", tablePrefix));
    }

    @Override
    public PDSyncPlugin getPlugin() {
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
    @Nullable
    public PlayerData loadPlayerData(UUID uniqueId, String username) throws Exception {
        PlayerData playerData = PlayerDataFactory.create(uniqueId, username);
        try (Connection c = this.connectionFactory.getConnection()) {
            try (PreparedStatement ps = c.prepareStatement(this.statementProcessor.apply(GET_PLAYER_DATA))) {
                ps.setString(1, uniqueId.toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        return null;
                    }

                    PlayerDataFactory.applyFromDataBase(playerData, rs);
                }
            }
        }

        return playerData;
    }

    private void applySchema() throws IOException, SQLException {
        List<String> statements;

        String schemaFileName = "org/nocraft/loperd/playerdatasync/" + this.connectionFactory.getImplementationName().toLowerCase() + ".sql";
        try (InputStream is = this.plugin.getBootstrap().getResourceStream(schemaFileName)) {
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
                ps.setString(1, data.uuid().toString());
                try (ResultSet rs = ps.executeQuery()) {
                    savePlayerData(c, data, !rs.next() ? INSERT_PLAYER_DATA : UPDATE_PLAYER_DATA);
                }
            }
        }
    }

    private void savePlayerData(Connection c, PlayerData data, String query) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(this.statementProcessor.apply(query))) {
            ps.setString(1, data.uuid().toString());
            ps.setString(2, data.username());
            ps.setDouble(3, data.health().getHealth());
            ps.setFloat(4, data.health().getFoodLevel());
            ps.setInt(5, data.xp().getLevel());
            ps.setDouble(6, data.xp().getProgress());
            ps.setString(7, data.gameMode().toString());

            ps.setString(8, data.potionEffects());

            ps.setString(9, data.savedInventory());
            ps.setString(10, data.enderChest());
            ps.setInt(11, data.heldItemSlot());
            ps.setBoolean(12, data.isFlight());

            if (query.equals(UPDATE_PLAYER_DATA)) {
                ps.setString(13, data.uuid().toString());
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
