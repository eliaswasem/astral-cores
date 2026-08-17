package de.ep.astralcores.mixin;

import net.minecraft.world.entity.decoration.Mannequin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Mannequin.class)
public interface MannequinAccessor {

    @Invoker("setHideDescription")
    void astralcores$setHideDescription(boolean hide);
}