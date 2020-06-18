package org.nocraft.loperd.datasync.common.scheduler;

/**
 * Represents a scheduled task
 */
public interface SchedulerTask {

    /**
     * Cancels the task.
     */
    void cancel();

}
