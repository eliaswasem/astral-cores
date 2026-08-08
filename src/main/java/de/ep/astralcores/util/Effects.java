package de.ep.astralcores.util;

import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

public class Effects {

    // Applies a status effect with default visibility settings
    public static void applyEffect(
            LivingEntity entity,
            Holder<MobEffect> effect,
            int duration,
            int level
    ) {
        applyEffect(entity, effect, duration, level, false, false, true);
    }

    // Applies a status effect with fully customized visibility settings
    public static void applyEffect(
            LivingEntity entity,
            Holder<MobEffect> effect,
            int duration,
            int level,
            boolean ambient,
            boolean showParticles,
            boolean showIcon
    ) {
        // Applies the effect
        entity.addEffect(
                new MobEffectInstance(
                        effect,
                        duration,
                        level - 1, // level - 1 is used because Minecraft amplifier indices start at 0
                        ambient,
                        showParticles,
                        showIcon
                )
        );
    }
}
