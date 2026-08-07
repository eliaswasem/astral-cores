package de.ep.astralcores.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;

public class CropUtils {

    public static void growNearbyCrops(ServerPlayer player, int radius, float chance) {

        if (player.getRandom().nextFloat() > chance) {
            return;
        }

        ServerLevel level = player.level();
        BlockPos playerPos = player.blockPosition();

        for (int i = 0; i < 3; i++) {
            int x = playerPos.getX() + player.getRandom().nextInt(radius * 2 + 1) - radius;
            int y = playerPos.getY() + player.getRandom().nextInt(3) - 1; // -1 bis +1 Höhe
            int z = playerPos.getZ() + player.getRandom().nextInt(radius * 2 + 1) - radius;

            BlockPos targetPos = new BlockPos(x, y, z);
            BlockState state = level.getBlockState(targetPos);
            Block block = state.getBlock();

            if (block instanceof BonemealableBlock bonemealable) {
                if (bonemealable.isValidBonemealTarget(level, targetPos, state)) {

                    bonemealable.performBonemeal(level, level.getRandom(), targetPos, state);

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
