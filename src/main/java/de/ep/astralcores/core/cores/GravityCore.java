package de.ep.astralcores.core.cores;

import de.ep.astralcores.AstralCores;
import de.ep.astralcores.core.Core;
import de.ep.astralcores.core.CoreType;
import de.ep.astralcores.playerdata.PlayerData;
import de.ep.astralcores.util.TickTimer;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class GravityCore extends Core {

    private static final Identifier GRAVITY_MODIFIER_ID =
            Identifier.fromNamespaceAndPath("astralcores", "gravity_heavy_presence");

    // Stores the active Gravity Pull timer for each player.
    private static final Map<UUID, TickTimer> activePulls = new HashMap<>();

    public GravityCore() {
        super(
                CoreType.GRAVITY_CORE,
                "§5Gravity Core",
                Items.HEAVY_CORE,
                List.of(
                        "§7Control the fabric of mass.",
                        "§6[Active: Gravity Pull]"
                ),
                10004,
                25,
                0,
                "Gravity Pull",
                "Heavy Presence",
                "\uE004"
        );
    }

    @Override
    public void applyPassive(ServerPlayer player) {
        AttributeInstance attribute = player.getAttribute(Attributes.KNOCKBACK_RESISTANCE);

        if (attribute != null && !attribute.hasModifier(GRAVITY_MODIFIER_ID)) {
            // Gives the player 50% knockback resistance.
            attribute.addTransientModifier(new AttributeModifier(
                    GRAVITY_MODIFIER_ID,
                    0.5,
                    AttributeModifier.Operation.ADD_VALUE
            ));
        }
    }

    @Override
    public void onRemoved(ServerPlayer player) {
        AttributeInstance attribute = player.getAttribute(Attributes.KNOCKBACK_RESISTANCE);

        if (attribute != null && attribute.hasModifier(GRAVITY_MODIFIER_ID)) {
            // Removes the knockback resistance when the core is removed.
            attribute.removeModifier(GRAVITY_MODIFIER_ID);
        }

        activePulls.remove(player.getUUID());
    }

    @Override
    public void activate(ServerPlayer player) {
        if (!player.isAlive() || player.isRemoved()) {
            return;
        }

        // 40 ticks = 2 seconds.
        activePulls.put(player.getUUID(), new TickTimer(40));
    }

    @Override
    public void tick(ServerPlayer player) {
        UUID uuid = player.getUUID();
        TickTimer timer = activePulls.get(uuid);

        if (timer == null) {
            return;
        }

        if (!player.isAlive() || player.isRemoved()) {
            activePulls.remove(uuid);
            return;
        }

        spawnPullParticles(player);

        AABB box = player.getBoundingBox().inflate(6.0);
        PlayerData data = AstralCores.PLAYER_DATA.get(player);

        for (ServerPlayer target : player.level().getEntitiesOfClass(
                ServerPlayer.class,
                box,
                target -> target != player && target.isAlive()
        )) {
            // Trusted players are not affected.
            if (data != null && data.isTrusted(target.getUUID())) {
                continue;
            }

            pull(target, player);
        }

        if (timer.tick()) {
            activePulls.remove(uuid);
        }
    }

    private void pull(ServerPlayer target, ServerPlayer source) {
        // Direction from the target toward the caster.
        Vec3 direction = source.position().subtract(target.position());

        if (direction.lengthSqr() < 0.25) {
            return;
        }

        direction = direction.normalize();

        Vec3 velocity = target.getDeltaMovement();

        // Pulls the target toward the caster and prevents jumping away.
        target.setDeltaMovement(
                direction.x * 0.65,
                Math.min(velocity.y, 0.05),
                direction.z * 0.65
        );

        target.hurtMarked = true;
    }

    private void spawnPullParticles(ServerPlayer player) {
        ServerLevel level = player.level();
        Vec3 center = player.position().add(0, 0.15, 0);

        // Two permanent-looking rings around the caster.
        double[] rings = {3.5, 5.5};

        for (double radius : rings) {

            // Draws the stationary ring.
            for (int i = 0; i < 32; i++) {
                double angle = Math.PI * 2 * i / 32;

                double x = center.x + Math.cos(angle) * radius;
                double z = center.z + Math.sin(angle) * radius;

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

            // Creates particles that visibly fly from the ring toward the center.
            for (int i = 0; i < 3; i++) {
                double angle = Math.random() * Math.PI * 2;

                double x = center.x + Math.cos(angle) * radius;
                double z = center.z + Math.sin(angle) * radius;

                Vec3 particlePos = new Vec3(x, center.y, z);

                // Direction from the ring directly toward the center.
                Vec3 direction = center.subtract(particlePos).normalize();

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