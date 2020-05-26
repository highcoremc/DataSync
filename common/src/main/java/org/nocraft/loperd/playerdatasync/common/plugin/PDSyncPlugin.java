package org.nocraft.loperd.playerdatasync.common.plugin;

import org.nocraft.loperd.playerdatasync.common.config.Configuration;
import org.nocraft.loperd.playerdatasync.common.PlayerDataFactory;
import org.nocraft.loperd.playerdatasync.common.scheduler.SchedulerAdapter;

public interface PDSyncPlugin {
    PluginLogger getLogger();

    SchedulerAdapter getScheduler();

    PDSyncBootstrap getBootstrap();

    Configuration getConfiguration();
}
