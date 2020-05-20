package org.nocraft.loperd.playerdatasync;

import com.lambdaworks.redis.api.StatefulRedisConnection;
import org.bukkit.plugin.java.JavaPlugin;
import org.nocraft.loperd.playerdatasync.Domain.Composer;
import org.nocraft.loperd.playerdatasync.Domain.LockedPlayerManager;
import org.nocraft.loperd.playerdatasync.Domain.SchedulerAdapter;
import org.nocraft.loperd.playerdatasync.Listener.LockedPlayerListener;
import org.nocraft.loperd.playerdatasync.Listener.NoListener;
import org.nocraft.loperd.playerdatasync.Listener.PlayerLoadListener;

import java.io.InputStream;

public final class NoPlayerDataSync extends JavaPlugin {

    private final Composer<NoListener> listeners = new Composer<>();
    private SchedulerAdapter scheduler;

    @Override
    public void onEnable() {
        LockedPlayerManager lockedManager = new LockedPlayerManager(this);

        listeners.add(new PlayerLoadListener(this, lockedManager));
        listeners.add(new LockedPlayerListener(this, lockedManager));

        listeners.register();
    }

    @Override
    public void onDisable() {
        listeners.unregister();
        listeners.shutdown();
    }

    public InputStream getResourceStream(String path) {
        return getResource(path);
    }

    public String identifyClassLoader(ClassLoader loader) {
        if (loader instanceof org.bukkit.plugin.java.PluginClassLoader) {
            return ((org.bukkit.plugin.java.PluginClassLoader) loader).getPlugin().getName();
        }
        return null;
    }

    public SchedulerAdapter getScheduler() {
        return this.scheduler;
    }
}
