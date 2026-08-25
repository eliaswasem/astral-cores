package de.ep.astralcores.command.astralcores;

import com.mojang.brigadier.context.CommandContext;
import de.ep.astralcores.AstralCores;
import de.ep.astralcores.manager.CooldownManager;
import de.ep.astralcores.playerdata.PlayerData;
import de.ep.astralcores.structure.StructureDefinition;
import de.ep.astralcores.structure.StructureRegistry;
import de.ep.astralcores.structure.StructureType;
import de.ep.astralcores.structure.spawners.MeteorSpawner;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.IdentifierArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;

public class AstralCoresCommandLogic {

    public static int execute(
            AstralCoresCommandType commandType,
            CommandContext<CommandSourceStack> context
    ) {
        return switch (commandType) {
            case DEBUG_COOLDOWN -> executeCooldown(context);
            case STRUCTURE_PLACE -> executePlace(context);
        };
    }

    private static int executePlace(
            CommandContext<CommandSourceStack> context
    ) {
        Optional<StructureType> structureType =
                StructureRegistry.getByStructureType(IdentifierArgument.getId(context, "structureType"));

        if (structureType.isEmpty()) {
            context.getSource().sendFailure(
                    Component.literal("StructureType not found!")
            );
            return 0;
        }

        StructureType type = structureType.get();

        StructureDefinition definition =
                StructureRegistry.get(type);

        BlockPos pos =
                BlockPosArgument.getBlockPos(context, "pos");

        MeteorSpawner.spawn(
                context.getSource().getLevel(),
                definition,
                pos
        );

        return 1;
    }

    private static int executeCooldown(
            CommandContext<CommandSourceStack> context
    ) {
        CommandSourceStack source = context.getSource();

        ServerPlayer player;
        try {
            player = source.getPlayerOrException();
        } catch (Exception e) {
            return 0;
        }

        PlayerData data = AstralCores.PLAYER_DATA.get(player);

        CooldownManager.resetCooldowns(data);

        source.sendSuccess(
                () -> Component.literal("All core cooldowns have been reset."),
                false
        );

        return 1;
    }

    public enum AstralCoresCommandType {
        DEBUG_COOLDOWN,
        STRUCTURE_PLACE
    }
}