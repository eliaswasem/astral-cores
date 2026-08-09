package de.ep.astralcores.core.cores;

import de.ep.astralcores.AstralCores;
import de.ep.astralcores.core.Core;
import de.ep.astralcores.core.CoreType;
import de.ep.astralcores.playerdata.PlayerData;
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
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class PhoenixCore extends Core {

    private static final Identifier BURNING_TIME_MODIFIER_ID = Identifier.fromNamespaceAndPath("astralcores", "phoenix_flameborn");

    public PhoenixCore() {
        super(
                CoreType.PHOENIX_CORE,
                "§cPhoenix Core",
                Items.MAGMA_CREAM,
                List.of(
                        "§7Forged in cosmic radiation.",
                        "§6[Active: Phoenix Burst]"
                ),
                10003,
                30,
                0,
                "Phoenix Burst",
                "Flameborn",
                "\uE006"
        );
    }

    @Override
    public void applyPassive(ServerPlayer player) {
        // Base fire protection
        Effects.applyEffect(player, MobEffects.FIRE_RESISTANCE, 25, 1);

        // Nether specific regen buff
        if (player.level().dimension().equals(Level.NETHER)) {
            Effects.applyEffect(player, MobEffects.REGENERATION, 25, 1);
        }

        AttributeInstance attribute = player.getAttribute(Attributes.BURNING_TIME);
        if (attribute != null) {
            // Guard clause to avoid duplicate stacking in the twentyTickLoop
            if (!attribute.hasModifier(BURNING_TIME_MODIFIER_ID)) {
                // Adds +0.5 to the base player knockback resistance (0.0)
                attribute.addTransientModifier(new AttributeModifier(
                        BURNING_TIME_MODIFIER_ID,
                        -1,
                        AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
                ));
            }
        }
    }

    @Override
    public void activate(ServerPlayer player) {
        ServerLevel level = player.level();

        player.sendSystemMessage(
                Component.literal("§cYou unleashed a " + getActiveAbilityName() + "!")
        );

        Vec3 pos = player.position();

        // Spawn explosion without block damage
        level.explode(
                player,
                pos.x,
                pos.y,
                pos.z,
                4.0F,
                true,
                Level.ExplosionInteraction.NONE
        );

        // Visual FX
        level.sendParticles(
                ParticleTypes.FLAME,
                pos.x,
                pos.y + 1,
                pos.z,
                200,
                2,
                1.0,
                2,
                0.08
        );

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

        // Audio FX
        level.playSound(
                null,
                player.blockPosition(),
                SoundEvents.BLAZE_SHOOT,
                SoundSource.PLAYERS,
                1.33F,
                0.8F
        );
    }

    @Override
    public void tick(ServerPlayer player) {
        if (player.getRemainingFireTicks() > 0) {
            player.setRemainingFireTicks(0);
        }
    }

    @Override
    public void onRemoved(ServerPlayer player) {
        AttributeInstance attribute = player.getAttribute(Attributes.BURNING_TIME);
        if (attribute != null) {
            // Wipes the modifier instantly on unequip, core swap, or death
            if (attribute.hasModifier(BURNING_TIME_MODIFIER_ID)) {
                attribute.removeModifier(BURNING_TIME_MODIFIER_ID);
            }
        }
    }
}
