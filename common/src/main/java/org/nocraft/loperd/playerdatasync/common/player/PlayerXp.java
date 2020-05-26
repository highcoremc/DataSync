package org.nocraft.loperd.playerdatasync.common.player;

public class PlayerXp {

    private final float xpProgress;
    private final int xpLevel;

    public PlayerXp() {
        this.xpProgress = 0.00F;
        this.xpLevel = 0;
    }

    public PlayerXp(int xpLevel, float xpProgress) {
        this.xpProgress = xpProgress;
        this.xpLevel = xpLevel;
    }

    public float getProgress() {
        return xpProgress;
    }

    public int getLevel() {
        return xpLevel;
    }
}
