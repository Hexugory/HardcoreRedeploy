package net.touhoudiscord.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.world.GameMode;
import net.touhoudiscord.RedeployPlayer;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class RedeployPlayerCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("redeployplayer")
        .requires(source -> source.hasPermissionLevel(4))
        .then(argument("player", EntityArgumentType.player())
        .suggests((context, builder) -> {
            PlayerManager playerManager = (context.getSource()).getServer().getPlayerManager();
            return CommandSource.suggestMatching(playerManager.getPlayerList().stream().filter(
                (player) -> player.interactionManager.getGameMode() == GameMode.SPECTATOR
            ).map(
                (player) -> player.getGameProfile().getName()
            ), builder);
        })
        .executes(context -> {
            if (context.getSource().getPlayer() == null) return 0;

            ServerPlayerEntity player = EntityArgumentType.getPlayer(context, "player");
            RedeployPlayer.redeploy(player, context.getSource().getPlayer());
            context.getSource().sendFeedback(() -> Text.literal("Redeployed ").append(player.getName()), false);

            return 1;
        })));
    }
}
