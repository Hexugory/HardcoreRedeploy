package net.touhoudiscord;

import net.minecraft.server.level.ServerPlayer;

public interface TimerAccess {
    void hardcoreredeploy_redeployInTicks(ServerPlayer spectator, ServerPlayer target, Long ticks);
}
