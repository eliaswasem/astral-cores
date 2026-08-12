package de.ep.astralcores.event.logic;

import de.ep.astralcores.core.cores.MagnetCore;
import de.ep.astralcores.util.Effects;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;

import java.util.concurrent.CompletableFuture;

public class MagnetCorePassiveLogic {
    public static boolean handleMagneticDisarm(ServerPlayer victim, DamageSource source) {

        if (!MagnetCore.activePlayers.contains(victim)) {
            return true;
        }

        if (!(victim instanceof ServerPlayer serverVictim)) {
            return false;
        }

        Entity attacker = source.getEntity();

        if (attacker instanceof ServerPlayer serverAttacker) {

            boolean isHitting = serverAttacker.swinging && serverAttacker.getAttackStrengthScale(0.5F) > 0.9F;

            boolean isCrit = serverAttacker.fallDistance > 0.0F
                    && !serverAttacker.onGround()
                    && !serverAttacker.isInWater()
                    && !serverAttacker.hasEffect(MobEffects.BLINDNESS)
                    && !serverAttacker.isPassenger();

            boolean holdsMace = serverAttacker.getMainHandItem().is(Items.MACE);
            boolean isMaceSmash = holdsMace && serverAttacker.fallDistance >= 1.5F && !serverAttacker.onGround();

            if (isHitting && isCrit || isMaceSmash) {
                    Effects.applyEffect(victim, MobEffects.MINING_FATIGUE, 25, 255, false, false, false);
                    Effects.applyEffect(victim, MobEffects.WEAKNESS, 25, 255, false, false, false);

                        victim.sendSystemMessage(Component.literal("You're Weapon is Magnetized").withStyle(ChatFormatting.BOLD,ChatFormatting.WHITE));
                        spawnWeaponParticles(serverVictim);

                return true;
            }
        }

        return false;
    }
    private static void spawnWeaponParticles(ServerPlayer player) {
        ServerLevel level = player.level();

        CompletableFuture.runAsync(() -> {
            long startTime = System.currentTimeMillis();

            while (System.currentTimeMillis() - startTime < 1500) {

                if (player.isRemoved()) break;

                Vec3 lookDirection = player.getLookAngle();
                double x = player.getX() + lookDirection.x * 0.6;
                double y = player.getEyeY() - 0.3 + lookDirection.y * 0.4;
                double z = player.getZ() + lookDirection.z * 0.6;


                level.getServer().execute(() -> {
                    level.sendParticles(ParticleTypes.FIREWORK, x, y, z, 3, 0.1, 0.1, 0.1, 0.0);
                });

                try {

                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
    }
}
