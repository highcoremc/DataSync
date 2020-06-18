package org.nocraft.loperd.datasync.common.storage.implementation.sql.connection.hikari;

import com.zaxxer.hikari.HikariConfig;
import org.nocraft.loperd.datasync.common.storage.misc.StorageCredentials;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MariaDbConnectionFactory extends HikariConnectionFactory {
    public MariaDbConnectionFactory(StorageCredentials configuration) {
        super(configuration);
    }

    @Override
    public String getImplementationName() {
        return "MariaDB";
    }

    @Override
    protected String getDriverClass() {
        return "org.mariadb.jdbc.MariaDbDataSource";
    }

    @Override
    protected void appendProperties(HikariConfig config, Map<String, String> properties) {
        String propertiesString = properties.entrySet().stream().map(e -> e.getKey() + "=" + e.getValue()).collect(Collectors.joining(";"));

        // kinda hacky. this will call #setProperties on the datasource, which will append these options
        // onto the connections.
        config.addDataSourceProperty("properties", propertiesString);
    }

    @Override
    public Function<String, String> getStatementProcessor() {
        return s -> s.replace("'", "`"); // use backticks for quotes
    }

}
