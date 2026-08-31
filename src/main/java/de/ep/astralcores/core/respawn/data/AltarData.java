package de.ep.astralcores.core.respawn.data;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;

public record AltarData(
        Identifier dimension,
        BlockPos pos
) {
}