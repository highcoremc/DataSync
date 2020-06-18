package org.nocraft.loperd.datasync.common.storage.implementation.nosql;

import com.lambdaworks.redis.RedisClient;
import com.lambdaworks.redis.RedisURI;
import com.lambdaworks.redis.api.StatefulRedisConnection;
import com.lambdaworks.redis.codec.StringCodec;
import org.nocraft.loperd.datasync.common.plugin.DataSyncPlugin;
import org.nocraft.loperd.datasync.common.storage.misc.StorageCredentials;

public class RedisConnectionFactory implements ConnectionFactory<String> {

    private StatefulRedisConnection<String, String> connection;
    private final StorageCredentials credentials;
    private RedisClient client;

    public RedisConnectionFactory(StorageCredentials credentials) {
        this.credentials = credentials;
    }

    @Override
    public String getImplementationName() {
        return "Redis";
    }

    @Override
    public void init(DataSyncPlugin plugin) {
        RedisURI uri = new RedisURI();

        String address = this.credentials.getAddress();
        String[] addressSplit = address.split(":");
        address = addressSplit[0];
        int port = addressSplit.length > 1 ? Integer.parseInt(addressSplit[1]) : 6379;

        String password = this.credentials.getPassword();

        uri.setHost(address);
        uri.setPort(port);
        uri.setTimeout(this.credentials.getConnectionTimeout());

        if (null != password && 0 != password.length()) {
            uri.setPassword(password);
        }

        client = RedisClient.create(uri);
    }

    @Override
    public void shutdown() {
        this.connection.close();
        this.client.shutdown();
    }

    @Override
    public StatefulRedisConnection<String, String> getConnection() {
        if (connection == null || !connection.isOpen()) {
            connection = client.connect(new StringCodec());
        }

        return this.connection;
    }
}
