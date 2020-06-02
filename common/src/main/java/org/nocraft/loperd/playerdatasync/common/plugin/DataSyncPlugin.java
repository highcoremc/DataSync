package org.nocraft.loperd.playerdatasync.common.plugin;

import org.nocraft.loperd.playerdatasync.common.config.Configuration;
import org.nocraft.loperd.playerdatasync.common.scheduler.SchedulerAdapter;

public interface DataSyncPlugin {
    PluginLogger getLogger();

    SchedulerAdapter getScheduler();

    DataSyncBootstrap getBootstrap();

    Configuration getConfiguration();

    String getName();
}
