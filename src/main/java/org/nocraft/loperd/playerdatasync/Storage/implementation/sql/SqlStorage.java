/*
 * This file is part of LuckPerms, licensed under the MIT License.
 *
 *  Copyright (c) lucko (Luck) <luck@lucko.me>
 *  Copyright (c) contributors
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package org.nocraft.loperd.playerdatasync.Storage.implementation.sql;

import org.nocraft.loperd.playerdatasync.Domain.Player.PlayerData;
import org.nocraft.loperd.playerdatasync.NoPlayerDataSync;
import org.nocraft.loperd.playerdatasync.Storage.implementation.StorageImplementation;
import org.nocraft.loperd.playerdatasync.Storage.implementation.sql.connection.ConnectionFactory;

import java.io.IOException;
import java.io.InputStream;
import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SqlStorage implements StorageImplementation {

    private final NoPlayerDataSync plugin;
    
    private final ConnectionFactory connectionFactory;
    private final Function<String, String> statementProcessor;

    public SqlStorage(NoPlayerDataSync plugin, ConnectionFactory connectionFactory, String tablePrefix) {
        this.plugin = plugin;
        this.connectionFactory = connectionFactory;
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
            tableExists = tableExists(c, this.statementProcessor.apply("{prefix}players"));
        }

        if (!tableExists) {
            applySchema();
        }
    }

    @Override
    public PlayerData loadPlayerData(UUID uniqueId, String username) throws Exception {
        return null;
    }

    private void applySchema() throws IOException, SQLException {
        List<String> statements;

        String schemaFileName = "me/lucko/luckperms/schema/" + this.connectionFactory.getImplementationName().toLowerCase() + ".sql";
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

    @Override
    public void savePlayerData(PlayerData playerData) throws SQLException {
    }

    @Override
    public UUID getPlayerUniqueId(String username) throws SQLException {
        return null;
    }

    @Override
    public String getPlayerName(UUID uniqueId) throws SQLException {
        return null;
    }

    private void insertPlayerData(Connection c, UUID user, PlayerData data) throws SQLException {
    }

    private static boolean tableExists(Connection connection, String table) throws SQLException {
        return true;
    }
}
