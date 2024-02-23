package net.touhoudiscord;

import net.minecraft.server.network.ServerPlayerEntity;

public interface TimerAccess {
    void hardcoreredeploy_redeployInTicks(ServerPlayerEntity spectator, ServerPlayerEntity target, Long ticks);
}
