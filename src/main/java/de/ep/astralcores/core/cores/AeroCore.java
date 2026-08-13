package de.ep.astralcores.core.cores;

import de.ep.astralcores.AstralCores;
import de.ep.astralcores.core.Core;
import de.ep.astralcores.core.CoreType;
import de.ep.astralcores.util.Effects;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.WeakHashMap;

public class AeroCore extends Core {

    // Tracks which players currently have this core's passive effect active
    public static final Set<ServerPlayer> activePlayers = Collections.newSetFromMap(new WeakHashMap<>());

    public AeroCore() {
        super(
                CoreType.AERO_CORE,
                "§bAero Core",
                Items.FEATHER,
                List.of(
                        "§7Forged in shifting air currents.",
                        "§6[Active: Tornado Lift]"
                ),
                10001,
                15,
                0,
                "Tornado Lift",
                "Featherweight",
                "\uE001"
        );
    }

    @Override
    public void applyPassive(ServerPlayer player) {
        // Just add the player to the active map
        activePlayers.add(player);
    }

    @Override
    public void onRemoved(ServerPlayer player) {
        // Just remove the player from the active map
        activePlayers.remove(player);
    }

    @Override
    public void activate(ServerPlayer player) {
        ServerLevel world = player.level();
        Vec3 center = player.position();

        // Play the explosive wind sound effect at the player's location.
        world.playSound(
                null,
                player.getX(), player.getY(), player.getZ(),
                SoundEvents.WIND_CHARGE_BURST,
                SoundSource.PLAYERS,
                2.0F,
                1.0F
        );

        // Define a 5-block radius bounding box around the player to look for targets.
        AABB area = new AABB(
                center.x - 5, center.y - 2, center.z - 5,
                center.x + 5, center.y + 5, center.z + 5
        );

        // Retrieve all living entities within the specified area, excluding the core user.
        List<LivingEntity> targets = world.getEntitiesOfClass(LivingEntity.class, area, entity -> entity != player);

        // Load the core owner's player data so their trust list can be checked.
        var data = AstralCores.PLAYER_DATA.get(player);

        for (LivingEntity entity : targets) {
            // Players trusted by the Aero Core owner are not affected by Tornado Lift.
            if (data != null && data.isTrusted(entity.getUUID())) {
                continue;
            }

            // Launch qualifying targets into the air using heavy Levitation for 10 ticks.
            Effects.applyEffect(entity, MobEffects.LEVITATION, 10, 45, false, false, false);
        }

        // Spawn a rigid, 25-block high particle cylinder that climbs from the ground up.
        new Thread(() -> {
            for (double yOffset = 0; yOffset < 25; yOffset += 1.0) {
                for (int degree = 0; degree < 360; degree += 8) {
                    double radians = Math.toRadians(degree);
                    double x = 5.0 * Math.cos(radians);
                    double z = 5.0 * Math.sin(radians);

                    // Send stationary cloud particles with a high render distance.
                    world.sendParticles(
                            ParticleTypes.CLOUD,
                            true,
                            true,
                            center.x + x,
                            center.y + yOffset,
                            center.z + z,
                            1,
                            0.0,
                            0.0,
                            0.0,
                            0.0
                    );
                }

                // Short delay between layers to simulate a rising tornado wave.
                try {
                    Thread.sleep(15);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }, "AeroCoreThread").start();
    }
}
