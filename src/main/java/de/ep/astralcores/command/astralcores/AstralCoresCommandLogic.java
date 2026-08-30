package de.ep.astralcores.command.astralcores;

import com.mojang.brigadier.context.CommandContext;
import de.ep.astralcores.AstralCores;
import de.ep.astralcores.manager.CooldownManager;
import de.ep.astralcores.playerdata.PlayerData;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import java.util.Optional;

public class AstralCoresCommandLogic {

    public static int placeAltar(
            CommandContext<CommandSourceStack> context
    ) {
            ServerLevel level = context.getSource().getLevel();

            CommandSourceStack source = context.getSource();

            StructurePlaceSettings structurePlaceSettings =
                    new StructurePlaceSettings()
                            .setKnownShape(true);

            Optional<StructureTemplate> template =
                    level.getStructureManager().get(Identifier.fromNamespaceAndPath("astralcores", "core_meteor"));

            if (template.isEmpty()) {

                source.sendFailure(Component.literal(
                        "Core Altar structure template not found. "
                ));
                return 0;
            }

            StructureTemplate altarTemplate = template.get();

        BlockPos pos =
                BlockPosArgument.getBlockPos(context, "pos");


        boolean placed =
                altarTemplate.placeInWorld(
                        level,
                        pos,
                        pos,
                        structurePlaceSettings,
                        level.getRandom(),
                        2
                );

        if (!placed) {
            source.sendFailure(Component.literal(
                    "Failed to place core altar."
            ));

        }

        source.sendSystemMessage(Component.literal("Placed core altar at")
                        .append((Component) pos)
        );

        // Todo: CoreRespawnDataManger.addAltar(level, pos)

        return 1;
    }

    public static int resetCooldowns(
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
}