package de.ep.astralcores.core.cores.logic;

import de.ep.astralcores.AstralCores;
import de.ep.astralcores.core.CoreType;
import de.ep.astralcores.core.data.CoreActivationResult;
import de.ep.astralcores.playerdata.PlayerData;
import de.ep.astralcores.util.BiomeUtils;
import de.ep.astralcores.util.CropUtils;
import de.ep.astralcores.util.Effects;
import de.ep.astralcores.util.FoodUtils;
import de.ep.astralcores.util.TickTimer;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Map;

public final class NatureCoreLogic {

    // Temporary knockback resistance while Root Trap is active.
    private static final Identifier ROOT_TRAP_MODIFIER_ID =
            Identifier.fromNamespaceAndPath(
                    "astralcores",
                    "root_trap_resistance"
            );

    // Active Root Trap clouds and their owners.
    private static final Map<AreaEffectCloud, ServerPlayer> activeTraps =
            new HashMap<>();

    // Rooted entities and their trap state.
    private static final Map<LivingEntity, RootTrap> rootedEntities =
            new HashMap<>();

    public static void applyPassive(ServerPlayer player) {

        // Nature passive only works in Nature biomes.
        if (!BiomeUtils.isInNatureBiome(player)) {
            return;
        }

        Effects.applyEffect(
                player,
                MobEffects.REGENERATION,
                25,
                1
        );

        Effects.applyEffect(
                player,
                MobEffects.SPEED,
                25,
                1
        );

        CropUtils.growNearbyCrops(
                player,
                4,
                0.05f
        );

        handleFoodHealing(player);
    }

    public static CoreActivationResult activate(ServerPlayer player) {

        // Prevent activation for invalid players.
        if (!player.isAlive() || player.isRemoved()) {
            return CoreActivationResult.FAILED;
        }

        ServerLevel level = player.level();
        Vec3 position = player.position();
        float radius = BiomeUtils.isInNatureBiome(player) ? 6.0f : 4.0f;

        // Create the visual and detection cloud.
        AreaEffectCloud cloud = new AreaEffectCloud(
                level,
                position.x,
                position.y + 0.5,
                position.z
        );

        cloud.setOwner(player);
        cloud.setRadius(radius);
        cloud.setDuration(120);
        cloud.setWaitTime(0);

        // Use green leaves for the Nature visual.
        cloud.setCustomParticle(
                ColorParticleOption.create(
                        ParticleTypes.TINTED_LEAVES,
                        0xFF3D7A2E
                )
        );

        level.addFreshEntity(cloud);
        activeTraps.put(cloud, player);

        player.sendSystemMessage(
                Component.literal("You summoned a Root Trap!")
                        .withStyle(ChatFormatting.GREEN)
        );

        return CoreActivationResult.EXECUTED;
    }

    public static void tick(ServerPlayer player) {

        // Detect targets inside active clouds.
        activeTraps.entrySet().removeIf(entry -> {

            AreaEffectCloud cloud = entry.getKey();
            ServerPlayer owner = entry.getValue();

            if (!cloud.isAlive() || cloud.isRemoved()) {
                return true;
            }

            detectTargets(cloud, owner);
            return false;
        });

        // Update all active roots.
        rootedEntities.values().removeIf(RootTrap::tick);
    }

    private static void detectTargets(
            AreaEffectCloud cloud,
            ServerPlayer owner
    ) {
        ServerLevel level = owner.level();
        Vec3 center = cloud.position();
        double radius = cloud.getRadius();
        AABB box = cloud.getBoundingBox();
        PlayerData data = AstralCores.PLAYER_DATA.get(owner);

        for (LivingEntity entity : level.getEntitiesOfClass(
                LivingEntity.class,
                box,
                target ->
                        target != owner &&
                                target.isAlive() &&
                                !target.isRemoved()
        )) {

            // Ignore entities outside the circular cloud radius.
            if (entity.distanceToSqr(center) > radius * radius) {
                continue;
            }

            // Ignore trusted entities.
            if (data != null && data.isTrusted(entity.getUUID())) {
                continue;
            }

            // Ignore entities that are already rooted.
            if (rootedEntities.containsKey(entity)) {
                continue;
            }

            rootedEntities.put(
                    entity,
                    new RootTrap(entity)
            );
        }
    }

    public static void onRemoved(ServerPlayer player) {
        // Remove clouds owned by the removed player.
        removeOwnedClouds(player);
    }

    public static void onPlayerDisconnect(ServerPlayer player) {
        // Remove clouds owned by the disconnected player.
        removeOwnedClouds(player);
    }

    private static void removeOwnedClouds(ServerPlayer player) {

        activeTraps.entrySet().removeIf(entry -> {

            if (entry.getValue() != player) {
                return false;
            }

            entry.getKey().discard();
            return true;
        });
    }

    private static void handleFoodHealing(ServerPlayer player) {

        // Only heal after eating has finished.
        if (!FoodUtils.isFinishedEating(player)) {
            return;
        }

        player.setHealth(
                Math.min(
                        player.getMaxHealth(),
                        player.getHealth() + 8.0f
                )
        );
    }

    private static final class RootTrap {

        private final LivingEntity entity;
        private final Vec3 position;
        private final TickTimer timer = new TickTimer(40);

        private RootTrap(LivingEntity entity) {

            this.entity = entity;
            this.position = entity.position();

            addKnockbackResistance();
            update();
        }

        private boolean tick() {

            if (!entity.isAlive() || entity.isRemoved()) {
                cleanup();
                return true;
            }

            update();

            if (timer.tick()) {
                cleanup();
                return true;
            }

            return false;
        }

        private void update() {
            entity.setPos(
                    position.x,
                    position.y,
                    position.z
            );

            entity.setDeltaMovement(Vec3.ZERO);
        }

        private void addKnockbackResistance() {

            AttributeInstance attribute =
                    entity.getAttribute(Attributes.KNOCKBACK_RESISTANCE);

            if (attribute == null ||
                    attribute.hasModifier(ROOT_TRAP_MODIFIER_ID)) {
                return;
            }

            attribute.addTransientModifier(
                    new AttributeModifier(
                            ROOT_TRAP_MODIFIER_ID,
                            1.0,
                            AttributeModifier.Operation.ADD_VALUE
                    )
            );
        }

        private void removeKnockbackResistance() {

            AttributeInstance attribute =
                    entity.getAttribute(Attributes.KNOCKBACK_RESISTANCE);

            if (attribute != null &&
                    attribute.hasModifier(ROOT_TRAP_MODIFIER_ID)) {
                attribute.removeModifier(ROOT_TRAP_MODIFIER_ID);
            }
        }

        private void cleanup() {
            removeKnockbackResistance();
        }
    }
}