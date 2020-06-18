package org.nocraft.loperd.datasync.common.storage.implementation.sql.connection;

import org.nocraft.loperd.datasync.common.plugin.DataSyncPlugin;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Map;
import java.util.function.Function;

public interface ConnectionFactory {

    String getImplementationName();

    void init(DataSyncPlugin plugin);

    void shutdown() throws Exception;

    default Map<String, String> getMeta() {
        return Collections.emptyMap();
    }

    Function<String, String> getStatementProcessor();

    Connection getConnection() throws SQLException;

}
