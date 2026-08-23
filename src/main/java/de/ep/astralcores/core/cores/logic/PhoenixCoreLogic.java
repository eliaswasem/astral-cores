package de.ep.astralcores.core.cores.logic;

import de.ep.astralcores.util.Effects;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public final class PhoenixCoreLogic {

    private static final Identifier BURNING_TIME_MODIFIER_ID =
            Identifier.fromNamespaceAndPath(
                    "astral_cores",
                    "phoenix_flameborn"
            );

    public static void applyPassive(ServerPlayer player) {
        // Base fire protection.
        Effects.applyEffect(
                player,
                MobEffects.FIRE_RESISTANCE,
                25,
                1
        );

        // Nether-specific regeneration.
        if (player.level().dimension().equals(Level.NETHER)) {
            Effects.applyEffect(
                    player,
                    MobEffects.REGENERATION,
                    25,
                    1
            );
        }

        // Prevents the player from having a burning-time multiplier.
        AttributeInstance attribute =
                player.getAttribute(Attributes.BURNING_TIME);

        if (attribute != null
                && !attribute.hasModifier(BURNING_TIME_MODIFIER_ID)) {

            attribute.addTransientModifier(
                    new AttributeModifier(
                            BURNING_TIME_MODIFIER_ID,
                            -1,
                            AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
                    )
            );
        }
    }

    public static void activate(ServerPlayer player) {
        if (!player.isAlive() || player.isRemoved()) {
            return;
        }

        ServerLevel level = player.level();
        Vec3 pos = player.position();

        player.sendSystemMessage(
                Component.literal("§cYou unleashed a Phoenix Burst!")
        );

        // Explosion without block damage.
        level.explode(
                player,
                pos.x,
                pos.y,
                pos.z,
                4.0F,
                true,
                Level.ExplosionInteraction.NONE
        );

        // Flame particles.
        level.sendParticles(
                ParticleTypes.FLAME,
                pos.x,
                pos.y + 1,
                pos.z,
                200,
                2.0,
                1.0,
                2.0,
                0.08
        );

        // Lava particles.
        level.sendParticles(
                ParticleTypes.LAVA,
                pos.x,
                pos.y + 1,
                pos.z,
                80,
                1.33,
                0.66,
                1.33,
                0.05
        );

        // Sound.
        level.playSound(
                null,
                player.blockPosition(),
                SoundEvents.BLAZE_SHOOT,
                SoundSource.PLAYERS,
                1.33F,
                0.8F
        );
    }

    public static void tick(ServerPlayer player) {
        if (player.getRemainingFireTicks() > 0) {
            player.setRemainingFireTicks(0);
        }
    }

    public static void onRemoved(ServerPlayer player) {
        AttributeInstance attribute =
                player.getAttribute(Attributes.BURNING_TIME);

        if (attribute != null
                && attribute.hasModifier(BURNING_TIME_MODIFIER_ID)) {

            attribute.removeModifier(BURNING_TIME_MODIFIER_ID);
        }
    }
}