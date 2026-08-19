package de.ep.astralcores.core;

import net.minecraft.world.item.ItemStack;

import java.util.UUID;

public record CoreStackResult(
        ItemStack stack,
        UUID uuid
) {}