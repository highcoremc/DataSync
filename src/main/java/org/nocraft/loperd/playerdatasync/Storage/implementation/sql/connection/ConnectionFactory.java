package org.nocraft.loperd.playerdatasync.Storage.implementation.sql.connection;

import org.nocraft.loperd.playerdatasync.NoPlayerDataSync;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Map;
import java.util.function.Function;

public interface ConnectionFactory {

    String getImplementationName();

    void init(NoPlayerDataSync plugin);

    void shutdown() throws Exception;

    default Map<String, String> getMeta() {
        return Collections.emptyMap();
    }

    Function<String, String> getStatementProcessor();

    Connection getConnection() throws SQLException;

}
