package net.touhoudiscord;

import java.util.UUID;

public class PlayerTimer {
    public UUID target;
    public Long ticks;

    public PlayerTimer(UUID target, Long ticks) {
        this.target = target;
        this.ticks = ticks;
    }
}