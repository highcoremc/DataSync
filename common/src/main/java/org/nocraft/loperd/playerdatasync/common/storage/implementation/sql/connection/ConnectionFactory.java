package org.nocraft.loperd.playerdatasync.common.storage.implementation.sql.connection;

import org.nocraft.loperd.playerdatasync.common.plugin.PDSyncPlugin;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Map;
import java.util.function.Function;

public interface ConnectionFactory {

    String getImplementationName();

    void init(PDSyncPlugin plugin);

    void shutdown() throws Exception;

    default Map<String, String> getMeta() {
        return Collections.emptyMap();
    }

    Function<String, String> getStatementProcessor();

    Connection getConnection() throws SQLException;

}
