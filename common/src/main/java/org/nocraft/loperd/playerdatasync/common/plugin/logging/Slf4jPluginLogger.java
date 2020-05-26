package org.nocraft.loperd.playerdatasync.common.plugin.logging;

import org.nocraft.loperd.playerdatasync.common.plugin.PluginLogger;
import org.slf4j.Logger;

public class Slf4jPluginLogger implements PluginLogger {
    private final Logger logger;

    public Slf4jPluginLogger(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void info(String s) {
        this.logger.info(s);
    }

    @Override
    public void warn(String s) {
        this.logger.warn(s);
    }

    @Override
    public void severe(String s) {
        this.logger.error(s);
    }
}