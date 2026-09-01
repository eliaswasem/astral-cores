package de.ep.astralcores.command.withdraw;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;

public class WithdrawCommand {

    public static void register(
            CommandDispatcher<CommandSourceStack> dispatcher
    ) {
        dispatcher.register(
                Commands.literal("withdraw")
                        .requires(
                                source ->
                                        source.getEntity()
                                                instanceof ServerPlayer
                        )
                        .executes(
                                WithdrawCommandLogic::withdraw
                        )
        );
    }
}