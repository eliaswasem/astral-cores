package de.ep.astralcores.core.cores.logic;

import de.ep.astralcores.AstralCores;
import de.ep.astralcores.playerdata.PlayerData;
import de.ep.astralcores.util.TickTimer;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class GravityCoreLogic {

    private static final Identifier GRAVITY_MODIFIER_ID =
            Identifier.fromNamespaceAndPath(
                    "astralcores",
                    "gravity_heavy_presence"
            );

    // Active Gravity Pull timers.
    private static final Map<UUID, TickTimer> activePulls = new HashMap<>();

    public static void applyPassive(ServerPlayer player) {
        AttributeInstance attribute =
                player.getAttribute(Attributes.KNOCKBACK_RESISTANCE);

        if (attribute != null
                && !attribute.hasModifier(GRAVITY_MODIFIER_ID)) {

            attribute.addTransientModifier(
                    new AttributeModifier(
                            GRAVITY_MODIFIER_ID,
                            0.5,
                            AttributeModifier.Operation.ADD_VALUE
                    )
            );
        }
    }

    public static void onRemoved(ServerPlayer player) {
        cleanup(player);
    }

    public static void onPlayerDisconnect(ServerPlayer player) {
        cleanup(player);
    }

    private static void cleanup(ServerPlayer player) {
        AttributeInstance attribute =
                player.getAttribute(Attributes.KNOCKBACK_RESISTANCE);

        if (attribute != null
                && attribute.hasModifier(GRAVITY_MODIFIER_ID)) {

            attribute.removeModifier(GRAVITY_MODIFIER_ID);
        }

        // Cancel any active Gravity Pull.
        activePulls.remove(player.getUUID());
    }

    public static void activate(ServerPlayer player) {
        if (!player.isAlive() || player.isRemoved()) {
            return;
        }

        // 40 ticks = 2 seconds.
        activePulls.put(
                player.getUUID(),
                new TickTimer(40)
        );
    }

    public static void tick(ServerPlayer player) {
        // Query the active pull timer directly via the ServerPlayer instance.
        TickTimer timer = activePulls.get(player.getUUID());

        if (timer == null) {
            return;
        }

        if (!player.isAlive() || player.isRemoved()) {
            activePulls.remove(player.getUUID());
            return;
        }

        spawnPullParticles(player);

        AABB box =
                player.getBoundingBox().inflate(6.0);

        PlayerData data =
                AstralCores.PLAYER_DATA.get(player);

        for (ServerPlayer target :
                player.level().getEntitiesOfClass(
                        ServerPlayer.class,
                        box,
                        target ->
                                target != player
                                        && target.isAlive()
                )) {

            // Trusted players are not affected.
            if (data != null
                    && data.isTrusted(target.getUUID())) {
                continue;
            }

            pull(target, player);
        }

        if (timer.tick()) {
            activePulls.remove(player.getUUID());
        }
    }

    private static void pull(
            ServerPlayer target,
            ServerPlayer source
    ) {
        // Direction from the target toward the caster.
        Vec3 direction =
                source.position()
                        .subtract(target.position());

        if (direction.lengthSqr() < 0.25) {
            return;
        }

        direction = direction.normalize();

        Vec3 velocity =
                target.getDeltaMovement();

        // Pulls the target toward the caster
        // and prevents jumping away.
        target.setDeltaMovement(
                direction.x * 0.65,
                Math.min(velocity.y, 0.05),
                direction.z * 0.65
        );

        target.hurtMarked = true;
    }

    private static void spawnPullParticles(
            ServerPlayer player
    ) {
        ServerLevel level = (ServerLevel) player.level();

        Vec3 center =
                player.position().add(0, 0.15, 0);

        double[] rings = {
                3.5,
                5.5
        };

        for (double radius : rings) {

            // Draws the stationary ring.
            for (int i = 0; i < 32; i++) {

                double angle =
                        Math.PI * 2 * i / 32;

                double x =
                        center.x
                                + Math.cos(angle) * radius;

                double z =
                        center.z
                                + Math.sin(angle) * radius;

                level.sendParticles(
                        ParticleTypes.ELECTRIC_SPARK,
                        x,
                        center.y,
                        z,
                        1,
                        0,
                        0,
                        0,
                        0
                );
            }

            // Creates particles moving toward the center.
            for (int i = 0; i < 3; i++) {

                double angle =
                        Math.random() * Math.PI * 2;

                double x =
                        center.x
                                + Math.cos(angle) * radius;

                double z =
                        center.z
                                + Math.sin(angle) * radius;

                Vec3 particlePos =
                        new Vec3(
                                x,
                                center.y,
                                z
                        );

                Vec3 direction =
                        center
                                .subtract(particlePos)
                                .normalize();

                level.sendParticles(
                        ParticleTypes.ELECTRIC_SPARK,
                        particlePos.x,
                        particlePos.y,
                        particlePos.z,
                        0,
                        direction.x,
                        direction.y,
                        direction.z,
                        0.35
                );
            }
        }
    }
}