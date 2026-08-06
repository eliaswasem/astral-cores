package de.ep.astralrelics.util;

import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

public class Effects {


    public static void applyEffect(
            LivingEntity entity,
            Holder<MobEffect> effect,
            int duration,
            int level
    ) {
        applyEffect(entity, effect, duration, level,false, false, true);
    }


    public static void applyEffect(
            LivingEntity entity,
            Holder<MobEffect> effect,
            int duration,
            int level,
            boolean ambient,
            boolean showParticles,
            boolean showIcon
    ) {

        entity.addEffect(
                new MobEffectInstance(
                        effect,
                        duration,
                        level - 1,
                        false,
                        showParticles,
                        showIcon
                )
        );

    }


    public static void removeEffect(
            LivingEntity entity,
            Holder<MobEffect> effect
    ) {

        entity.removeEffect(effect);

    }

}