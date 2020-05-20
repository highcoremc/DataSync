package org.nocraft.loperd.playerdatasync.Domain;

public interface Shutdownable
{
    /**
     * This method clears all internal, owned data structures, nullifies references, and
     * performs all functions necessary to cleanup the object making it unusable after
     * this call.
     */
    void shutdown();
}