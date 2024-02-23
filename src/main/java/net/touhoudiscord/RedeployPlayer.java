package net.touhoudiscord;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.GameMode;

public class RedeployPlayer {
    public static void redeploy(ServerPlayerEntity spectator, ServerPlayerEntity target) {
        if (!(spectator.interactionManager.getGameMode() == GameMode.SPECTATOR)) return;

        if (!target.getServerWorld().getDimension().hasCeiling()) spectator.addStatusEffect(new StatusEffectInstance(HardcoreRedeploy.REDEPLOYING, 20*20, 0));
        spectator.teleport(target.getServerWorld(), target.getPos().x, target.getServerWorld().getDimension().hasCeiling() ? target.getPos().y : 320, target.getPos().z, 0, 30);
        spectator.changeGameMode(GameMode.SURVIVAL);

        RedeployStateSaver.getPlayerState(spectator).timesRevived++;
        spectator.server.getPlayerManager().getPlayerList().forEach(player -> {
            HardcoreRedeploy.syncRevives(player.server, player, spectator.getUuid());
        });
    }
}
