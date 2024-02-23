package net.touhoudiscord.mixin;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.touhoudiscord.PlayerTimer;
import net.touhoudiscord.RedeployPlayer;
import net.touhoudiscord.TimerAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.UUID;

@Mixin(MinecraftServer.class)
public abstract class RedeployTimerMixin implements TimerAccess {
    @Shadow public abstract PlayerManager getPlayerManager();

    @Unique
    private final HashMap<UUID, PlayerTimer> playerTimers = new HashMap<>();

    @Inject(method = "tick", at = @At("TAIL"))
    private void onTick(CallbackInfo ci) {
        this.playerTimers.forEach((uuid, timer) -> {
            if (--timer.ticks == 0L) {
                ServerPlayerEntity spectator = this.getPlayerManager().getPlayer(uuid);
                ServerPlayerEntity target = this.getPlayerManager().getPlayer(timer.target);
                if (spectator != null && target != null) RedeployPlayer.redeploy(spectator, target);
            }
        });
    }

    @Override
    public void hardcoreredeploy_redeployInTicks(ServerPlayerEntity spectator, ServerPlayerEntity target, Long ticks) {
        this.playerTimers.put(spectator.getUuid(), new PlayerTimer(target.getUuid(), ticks));
    }
}