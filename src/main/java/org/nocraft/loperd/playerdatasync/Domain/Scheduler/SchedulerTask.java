package org.nocraft.loperd.playerdatasync.Domain.Scheduler;

/**
 * Represents a scheduled task
 */
public interface SchedulerTask {

    /**
     * Cancels the task.
     */
    void cancel();

}
