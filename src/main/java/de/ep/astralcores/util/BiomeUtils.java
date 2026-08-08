package de.ep.astralcores.util;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;

public class BiomeUtils {

    // Checks if the player is currently inside a nature-themed biome
    public static boolean isInNatureBiome(Player player) {
        BlockPos pos = player.blockPosition();
        Level level = player.level();

        // Gets the biome holder at the player's current block position
        Holder<Biome> biome = level.getBiome(pos);

        // Validates if the biome matches forest, jungle, taiga, or swamp types
        return biome.is(BiomeTags.IS_FOREST)
                || biome.is(BiomeTags.IS_JUNGLE)
                || biome.is(BiomeTags.IS_TAIGA)
                || biome.is(Biomes.SWAMP)
                || biome.is(Biomes.MANGROVE_SWAMP);
    }
}
