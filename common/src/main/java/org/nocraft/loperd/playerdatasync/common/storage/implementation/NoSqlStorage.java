package org.nocraft.loperd.playerdatasync.common.storage.implementation;

import com.lambdaworks.redis.api.StatefulRedisConnection;
import org.nocraft.loperd.playerdatasync.common.DataSyncPlayer;
import org.nocraft.loperd.playerdatasync.common.plugin.DataSyncPlugin;
import org.nocraft.loperd.playerdatasync.common.storage.implementation.nosql.RedisConnectionFactory;

import java.util.Optional;
import java.util.UUID;

public class NoSqlStorage implements StorageImplementation {

    private final static String STORAGE_KEY = "datasync";

    private final RedisConnectionFactory connectionFactory;
    private final DataSyncPlugin plugin;

    public NoSqlStorage(DataSyncPlugin plugin, RedisConnectionFactory factory) {
        this.plugin = plugin;
        this.connectionFactory = factory;
    }

    @Override
    public DataSyncPlugin getPlugin() {
        return this.plugin;
    }

    @Override
    public String getImplementationName() {
        return this.connectionFactory.getImplementationName();
    }

    @Override
    public void init() {
        this.connectionFactory.init(this.plugin);
        try (StatefulRedisConnection<String, String> conn = this.connectionFactory.getConnection()) {
            String result = conn.sync().ping();
            if (!result.equals("PONG")) {
                throw new IllegalStateException("Can not get connection.");
            }
        }
    }

    @Override
    public Optional<String> loadPlayerData(UUID uniqueId, String username) {
        try (StatefulRedisConnection<String, String> conn = this.connectionFactory.getConnection()) {
            String result = conn.sync().hget(STORAGE_KEY, uniqueId.toString());

            if (result == null) {
                return Optional.empty();
            }

            return Optional.of(result);
        }
    }

    @Override
    public void savePlayerData(DataSyncPlayer player) {
        try (StatefulRedisConnection<String, String> conn = this.connectionFactory.getConnection()) {
            boolean result = conn.sync().hset(STORAGE_KEY, player.getPlayerId().toString(), player.getData());
            if (result) {
//                call SuccessfulSavedEvent
                return;
            } else {
                // call FailureSaveEvent
                return;
            }
        }
    }

    @Override
    public void shutdown() {
        this.connectionFactory.shutdown();
    }
}
