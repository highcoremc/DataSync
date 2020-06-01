package org.nocraft.loperd.playerdatasync.common.storage.implementation;

import org.nocraft.loperd.playerdatasync.common.DataSyncPlayer;
import org.nocraft.loperd.playerdatasync.common.plugin.DataSyncPlugin;
import org.nocraft.loperd.playerdatasync.common.storage.implementation.sql.SchemaReader;
import org.nocraft.loperd.playerdatasync.common.storage.implementation.sql.connection.ConnectionFactory;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SqlStorage implements StorageImplementation {

    private static final String INSERT_PLAYER_DATA = "INSERT INTO {prefix}users (uuid,name,data) VALUES (?, ?, ?)";
    private static final String UPDATE_PLAYER_DATA = "UPDATE {prefix}users SET uuid = ?, name = ?, data = ? WHERE uuid = ?";
    private static final String GET_PLAYER_DATA = "SELECT * FROM {prefix}users WHERE uuid = ?"; // AND username = ?
    private static final String GET_USERNAME = "SELECT name FROM {prefix}users WHERE uuid = ?";

    private final DataSyncPlugin plugin;

    private final Function<String, String> statementProcessor;
    private final ConnectionFactory connectionFactory;

    public SqlStorage(DataSyncPlugin plugin, ConnectionFactory connectionFactory, String tablePrefix) {
        this.plugin = plugin;
        this.connectionFactory = connectionFactory;
        this.statementProcessor = connectionFactory.getStatementProcessor().compose(s -> s.replace("{prefix}", tablePrefix));
    }

    @Override
    public DataSyncPlugin getPlugin() {
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
    public Optional<String> loadPlayerData(UUID uniqueId, String username) throws Exception {
        try (Connection c = this.connectionFactory.getConnection()) {
            try (PreparedStatement ps = c.prepareStatement(this.statementProcessor.apply(GET_PLAYER_DATA))) {
                ps.setString(1, uniqueId.toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        return Optional.empty();
                    }

                    return Optional.of(rs.getString("data"));
                }
            }
        }
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

    @Override
    public void savePlayerData(DataSyncPlayer player) throws SQLException {
        try (Connection c = this.connectionFactory.getConnection()) {
            try (PreparedStatement ps = c.prepareStatement(this.statementProcessor.apply(GET_USERNAME))) {
                ps.setString(1, player.getPlayerId().toString());
                try (ResultSet rs = ps.executeQuery()) {
                    savePlayerData(c, player, !rs.next() ? INSERT_PLAYER_DATA : UPDATE_PLAYER_DATA);
                }
            }
        }
    }

    private void savePlayerData(Connection c, DataSyncPlayer player, String query) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(this.statementProcessor.apply(query))) {
            ps.setString(1, player.getPlayerId().toString());
            ps.setString(2, player.getUsername());
            ps.setString(3, player.getData());

            if (query.equals(UPDATE_PLAYER_DATA)) {
                ps.setString(13, player.getPlayerId().toString());
            }

            ps.execute();
        }
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
