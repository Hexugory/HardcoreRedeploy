package net.touhoudiscord.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.touhoudiscord.HardcoreRedeploy;
import net.touhoudiscord.RedeployStateSaver;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class SetRevivesCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("setrevives")
                .requires(source -> source.hasPermissionLevel(4))
                .then(argument("player", EntityArgumentType.player())
                .then(argument("revives", IntegerArgumentType.integer(0))
                .executes(context -> {
                    if (context.getSource().getPlayer() == null) return 0;

                    ServerPlayerEntity player = EntityArgumentType.getPlayer(context, "player");

                    RedeployStateSaver.getPlayerState(player).timesRevived = IntegerArgumentType.getInteger(context, "revives");

                    context.getSource().getServer().getPlayerManager().getPlayerList().forEach(player1 -> {
                        HardcoreRedeploy.syncRevives(context.getSource().getServer(), player1, player.getUuid());
                    });
                    context.getSource().sendFeedback(() -> Text.literal("Set revives for ").append(player.getName()), true);

                    return 1;
                }))));
    }
}
