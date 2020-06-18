package org.nocraft.loperd.datasync.common.plugin;

import org.nocraft.loperd.datasync.common.config.Configuration;
import org.nocraft.loperd.datasync.common.scheduler.SchedulerAdapter;

public interface DataSyncPlugin {
    PluginLogger getLogger();

    SchedulerAdapter getScheduler();

    DataSyncBootstrap getBootstrap();

    Configuration getConfiguration();

    String getName();
}
