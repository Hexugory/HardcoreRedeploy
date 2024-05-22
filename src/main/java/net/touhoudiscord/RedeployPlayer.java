package net.touhoudiscord;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.level.GameType;

public class RedeployPlayer {
    public static void redeploy(ServerPlayer spectator, ServerPlayer target) {
        if (!(spectator.gameMode.getGameModeForPlayer() == GameType.SPECTATOR)) return;

        if (!target.serverLevel().dimensionType().hasCeiling()) spectator.addEffect(new MobEffectInstance(HardcoreRedeploy.REDEPLOYING, 20*20, 0));
        spectator.teleportTo(target.serverLevel(), target.position().x, target.serverLevel().dimensionType().hasCeiling() ? target.position().y : 320, target.position().z, 0, 30);
        spectator.setGameMode(GameType.SURVIVAL);

        RedeployStateSaver.getPlayerState(spectator).timesRevived++;
        spectator.server.getPlayerList().getPlayers().forEach(player -> {
            HardcoreRedeploy.syncRevives(player.server, player, spectator.getUUID());
        });
    }
}
