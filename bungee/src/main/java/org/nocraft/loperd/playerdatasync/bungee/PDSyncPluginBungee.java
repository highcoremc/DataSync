package org.nocraft.loperd.playerdatasync.bungee;

import org.nocraft.loperd.playerdatasync.bungee.Listener.PDSyncListenerBungee;
import org.nocraft.loperd.playerdatasync.common.Composer;
import org.nocraft.loperd.playerdatasync.common.config.Adapter.ConfigurationAdapter;
import org.nocraft.loperd.playerdatasync.common.config.Configuration;
import org.nocraft.loperd.playerdatasync.common.config.PluginConfiguration;
import org.nocraft.loperd.playerdatasync.common.plugin.PDSyncBootstrap;
import org.nocraft.loperd.playerdatasync.common.plugin.PDSyncPlugin;
import org.nocraft.loperd.playerdatasync.common.plugin.PluginLogger;
import org.nocraft.loperd.playerdatasync.common.scheduler.SchedulerAdapter;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public class PDSyncPluginBungee implements PDSyncPlugin {

    private final Composer<PDSyncListenerBungee> listeners = new Composer<>();
    private final PDSyncBootstrapBungee bootstrap;
    private Configuration configuration;

    public PDSyncPluginBungee(PDSyncBootstrapBungee bootstrap) {
        this.bootstrap = bootstrap;
    }

    public final void enable() {
        this.configuration = new PluginConfiguration(this, provideConfigurationAdapter());

//        this.listeners.add();
    }

    public final void disable() {
        this.listeners.unregister();
        this.listeners.shutdown();
    }

    private ConfigurationAdapter provideConfigurationAdapter() {
        return new BungeeConfigAdapter(this, resolveConfig());
    }

    private File resolveConfig() {
        File configFile = new File(this.bootstrap.getDataFolder(), "config.yml");

        if (!configFile.exists()) {
            this.bootstrap.getDataFolder().mkdirs();
            try (InputStream is = this.bootstrap.getResourceAsStream("config.yml")) {
                Files.copy(is, configFile.toPath());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return configFile;
    }

    @Override
    public PluginLogger getLogger() {
        return getBootstrap().getPluginLogger();
    }

    @Override
    public SchedulerAdapter getScheduler() {
        return getBootstrap().getSchedulerAdapter();
    }

    @Override
    public PDSyncBootstrap getBootstrap() {
        return this.bootstrap;
    }

    @Override
    public Configuration getConfiguration() {
        return this.configuration;
    }

    public void registerEvents(PDSyncListenerBungee listener) {
        this.bootstrap.getProxy().getPluginManager().registerListener(this.bootstrap, listener);
    }

    public void unregisterEvents(PDSyncListenerBungee listener) {
        this.bootstrap.getProxy().getPluginManager().unregisterListener(listener);
    }
}
