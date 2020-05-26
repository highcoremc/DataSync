package org.nocraft.loperd.playerdatasync.common.player;

public class PlayerHealth {

    private double health = 20.00D;

    private int foodLevel = 20;

    public PlayerHealth() {
    }

    public PlayerHealth(double health) {
        this.health = health;
    }

    public PlayerHealth(int foodLevel) {
        this.foodLevel = foodLevel;
    }

    public PlayerHealth(double health, int foodLevel) {
        this.foodLevel = foodLevel;
        this.health = health;
    }

    public PlayerHealth(int foodLevel, double health) {
        this.foodLevel = foodLevel;
        this.health = health;
    }

    public double getHealth() {
        return health;
    }

    public int getFoodLevel() {
        return foodLevel;
    }
}
