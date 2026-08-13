package de.ep.astralcores.core.cores;

import de.ep.astralcores.core.Core;
import de.ep.astralcores.core.CoreType;
import de.ep.astralcores.util.Effects;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class GaleCore extends Core {

    public GaleCore() {
        super(
                CoreType.GALE_CORE,
                "§7Gale Core",
                Items.BREEZE_ROD,
                List.of(
                        "§f[Active: Sonic Dash]"
                ),
                10005,
                0,
                0,
                "Sonic Dash",
                "Lightfeet",
                "\uE002"
        );
    }

    @Override
    public void applyPassive(ServerPlayer player) {
        if (player.isSprinting()) {
            Effects.applyEffect(player, MobEffects.SPEED, 25, 1);
        }
    }

    @Override
    public void activate(ServerPlayer player) {



        Vec3 lookDirection = player.getLookAngle();

        Vec3 boost = new Vec3(
                lookDirection.x * 5,
                0.2,
                lookDirection.z * 5
        );

        player.setDeltaMovement(boost);
        player.hurtMarked = true;

        ServerLevel level = player.level();

        java.util.concurrent.CompletableFuture
                .delayedExecutor(500, TimeUnit.MILLISECONDS)
                .execute(() -> {

                    level.getServer().execute(() -> {

                        if (player.isRemoved()) {
                            return;
                        }

                        Vec3 pos = player.position();

                        level.explode(
                                player,
                                pos.x,
                                pos.y,
                                pos.z,
                                4.0F,
                                false,
                                Level.ExplosionInteraction.NONE
                        );

                        level.sendParticles(
                                ParticleTypes.SOUL_FIRE_FLAME,
                                pos.x,
                                pos.y + 1,
                                pos.z,
                                200,
                                2.0,
                                1.0,
                                2.0,
                                0.08
                        );

                        level.sendParticles(
                                ParticleTypes.SONIC_BOOM,
                                pos.x,
                                pos.y + 1,
                                pos.z,
                                10,
                                1.33,
                                0.66,
                                1.33,
                                0.05
                        );

                        level.playSound(
                                null,
                                player.blockPosition(),
                                SoundEvents.WARDEN_SONIC_CHARGE,
                                SoundSource.PLAYERS,
                                1.33F,
                                0.8F
                        );
                    });
                });
    }
}