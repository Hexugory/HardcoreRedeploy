package net.touhoudiscord.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.touhoudiscord.HardcoreRedeploy;
import net.touhoudiscord.RedeployStateSaver;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class SetRevivesCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(literal("setrevives")
                .requires(source -> source.hasPermission(4))
                .then(argument("player", EntityArgument.player())
                .then(argument("revives", IntegerArgumentType.integer(0))
                .executes(context -> {
                    if (context.getSource().getPlayer() == null) return 0;

                    ServerPlayer player = EntityArgument.getPlayer(context, "player");

                    RedeployStateSaver.getPlayerState(player).timesRevived = IntegerArgumentType.getInteger(context, "revives");

                    context.getSource().getServer().getPlayerList().getPlayers().forEach(player1 -> {
                        HardcoreRedeploy.syncRevives(context.getSource().getServer(), player1, player.getUUID());
                    });
                    context.getSource().sendSuccess(() -> Component.literal("Set revives for ").append(player.getName()), true);

                    return 1;
                }))));
    }
}
