package de.ep.astralcores.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;

public class CropUtils {

    // Attempts to apply a bonemeal effect to random blocks around the player
    public static void growNearbyCrops(ServerPlayer player, int radius, float chance) {

        // Rolls a random chance to determine if the growth attempt triggers
        if (player.getRandom().nextFloat() > chance) {
            return;
        }

        ServerLevel level = player.level();
        BlockPos playerPos = player.blockPosition();

        // Tries to find and grow up to 3 blocks within the specified area
        for (int i = 0; i < 3; i++) {
            // Calculates random coordinates within the radius (height range: -1 to +1)
            int x = playerPos.getX() + player.getRandom().nextInt(radius * 2 + 1) - radius;
            int y = playerPos.getY() + player.getRandom().nextInt(3) - 1;
            int z = playerPos.getZ() + player.getRandom().nextInt(radius * 2 + 1) - radius;

            BlockPos targetPos = new BlockPos(x, y, z);
            BlockState state = level.getBlockState(targetPos);
            Block block = state.getBlock();

            // Checks if the block can be grown using bonemeal
            if (block instanceof BonemealableBlock bonemealable) {
                if (bonemealable.isValidBonemealTarget(level, targetPos, state)) {

                    // Advances the block growth stage
                    bonemealable.performBonemeal(level, level.getRandom(), targetPos, state);

                    // Spawns green happy villager particles at the crop position
                    level.sendParticles(
                            ParticleTypes.HAPPY_VILLAGER,
                            x + 0.5, y + 0.5, z + 0.5,
                            4, 0.2, 0.2, 0.2, 0.0
                    );
                }
            }
        }
    }
}
