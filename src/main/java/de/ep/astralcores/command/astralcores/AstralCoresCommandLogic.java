package de.ep.astralcores.command.astralcores;

import com.mojang.brigadier.context.CommandContext;
import de.ep.astralcores.AstralCores;
import de.ep.astralcores.core.respawn.data.AltarData;
import de.ep.astralcores.manager.AltarManager;
import de.ep.astralcores.manager.CooldownManager;
import de.ep.astralcores.playerdata.PlayerData;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.Optional;

import static de.ep.astralcores.manager.AltarManager.PlaceResult.SUCCESS;

public class AstralCoresCommandLogic {

    public static int placeAltar(
            CommandContext<CommandSourceStack> context
    ) {
        CommandSourceStack source =
                context.getSource();

        ServerLevel level =
                source.getLevel();

        BlockPos pos =
                BlockPosArgument.getBlockPos(
                        context,
                        "pos"
                );

        AltarManager.PlaceResult result =
                AltarManager.place(
                        level,
                        pos
                );

        switch (result) {

            case SUCCESS -> {
                source.sendSuccess(
                        () -> Component.literal(
                                "Placed core altar at ")
                                        .append(String.valueOf(pos.getX()))
                                        .append(", ")
                                        .append(String.valueOf(pos.getY()))
                                        .append(", ")
                                        .append(String.valueOf(pos.getZ()))
                                        .append(".")
                                .withStyle(ChatFormatting.GREEN),
                        false
                );

                return 1;
            }

            case ALREADY_EXISTS -> {
                AltarData altar =
                        AstralCores.CORE_RESPAWN_DATA
                                .getAltar();

                source.sendFailure(
                        Component.literal(
                                "An altar already exists at ")
                                    .append(String.valueOf(pos.getX()))
                                    .append(", ")
                                    .append(String.valueOf(pos.getY()))
                                    .append(", ")
                                    .append(String.valueOf(pos.getZ()))
                                    .append(" in ")
                                    .append(String.valueOf(altar.dimension()))
                                    .append(".")
                );

                return 0;
            }

            case STRUCTURE_NOT_FOUND -> {
                source.sendFailure(
                        Component.literal(
                                "Core altar structure template not found."
                        )
                );

                return 0;
            }

            case PLACEMENT_FAILED -> {
                source.sendFailure(
                        Component.literal(
                                "Failed to place core altar."
                        )
                );

                return 0;
            }
        }

        return 0;
    }

    public static int removeAltar(
            CommandContext<CommandSourceStack> context
    ) {
        CommandSourceStack source = context.getSource();
        ServerLevel level = source.getLevel();

        AltarData altar =
                AstralCores.CORE_RESPAWN_DATA.getAltar();

        // No altar exists
        if (altar == null) {
            source.sendFailure(
                    Component.literal(
                            "There is no altar placed."
                    )
            );
            return 0;
        }


        BlockPos center = altar.pos();


        BlockPos min = new BlockPos(
                center.getX(),
                center.getY(),
                center.getZ()
        );

        BlockPos max = new BlockPos(
                center.getX() + 5,
                center.getY() + 5,
                center.getZ() + 5
        );

        try {
            for (BlockPos pos : BlockPos.betweenClosed(min, max)) {

                if (!level.getBlockState(pos).isAir()) {

                    level.setBlock(
                            pos,
                            Blocks.AIR.defaultBlockState(),
                            Block.UPDATE_ALL
                    );

                }
            }

            AstralCores.CORE_RESPAWN_DATA.removeAltar();

            source.sendSuccess(
                    () -> Component.literal(
                            "Removed altar."
                    ).withStyle(ChatFormatting.GREEN),
                    false
            );

            return 1;

        } catch (Exception e) {

            AstralCores.LOGGER.error(
                    "Failed to remove altar",
                    e
            );

            source.sendFailure(
                    Component.literal(
                            "Failed to remove altar."
                    )
            );

            return 0;
        }
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