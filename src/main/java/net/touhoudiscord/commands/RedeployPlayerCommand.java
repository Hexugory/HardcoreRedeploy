package net.touhoudiscord.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.level.GameType;
import net.touhoudiscord.RedeployPlayer;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class RedeployPlayerCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(literal("redeployplayer")
        .requires(source -> source.hasPermission(4))
        .then(argument("player", EntityArgument.player())
        .suggests((context, builder) -> {
            PlayerList playerManager = (context.getSource()).getServer().getPlayerList();
            return SharedSuggestionProvider.suggest(playerManager.getPlayers().stream().filter(
                (player) -> player.gameMode.getGameModeForPlayer() == GameType.SPECTATOR
            ).map(
                (player) -> player.getGameProfile().getName()
            ), builder);
        })
        .executes(context -> {
            if (context.getSource().getPlayer() == null) return 0;

            ServerPlayer player = EntityArgument.getPlayer(context, "player");
            RedeployPlayer.redeploy(player, context.getSource().getPlayer());
            context.getSource().sendSuccess(() -> Component.literal("Redeployed ").append(player.getName()), true);

            return 1;
        })));
    }
}
