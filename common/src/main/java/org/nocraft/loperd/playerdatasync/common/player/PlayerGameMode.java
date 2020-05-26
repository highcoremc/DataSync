package org.nocraft.loperd.playerdatasync.common.player;

public enum PlayerGameMode {
    CREATIVE(1),
    SURVIVAL(0),
    ADVENTURE(2),
    SPECTATOR(3);

    private final int value;

    PlayerGameMode() {
        this.value = 0;
    }

    PlayerGameMode(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }
}
