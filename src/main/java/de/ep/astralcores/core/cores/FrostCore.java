package de.ep.astralcores.core.cores;

import de.ep.astralcores.AstralCores;
import de.ep.astralcores.core.Core;
import de.ep.astralcores.core.CoreType;
import de.ep.astralcores.playerdata.PlayerData;
import de.ep.astralcores.util.Effects;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Display.BlockDisplay;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public final class FrostCore extends Core {

    // Temporary modifier used to completely prevent knockback during Frost Lock.
    private static final Identifier FROST_MODIFIER_ID = Identifier.fromNamespaceAndPath("astralcores", "frost_lock_resistance");

    // Players waiting for their next valid hit to trigger Frost Lock.
    public static final Set<UUID> armedPlayers = new HashSet<>();

    // Currently active Frost Locks, indexed by entity UUID.
    private static final Map<UUID, FrostLock> activeLocks = new HashMap<>();

    public FrostCore() {
        super(
                CoreType.FROST_CORE,
                "§bFrost Core",
                Items.CLAY_BALL,
                List.of(
                        "§b[Passive: Frost Aura]",
                        "§b[Active: Frost Lock]"
                ),
                10008,
                0,
                0,
                "Frost Lock",
                "Frost Aura",
                "\uE005"
        );
    }

    @Override
    public void applyPassive(ServerPlayer player) {
        PlayerData data = AstralCores.PLAYER_DATA.get(player);

        // Search for entities within the 6 block Frost Aura.
        AABB box = player.getBoundingBox().inflate(6.0);

        for (LivingEntity entity : player.level().getEntitiesOfClass(LivingEntity.class, box, entity ->
                entity != player && entity.isAlive()
        )) {
            if (data != null && data.isTrusted(entity.getUUID())) {
                continue;
            }

            double distance = player.distanceTo(entity);

            // Stronger Slowness is applied at closer distances.
            int effectLevel = distance <= 3.5 ? 3 : distance <= 5.0 ? 2 : 1;

            Effects.applyEffect(entity, MobEffects.SLOWNESS, 40, effectLevel, false, false, false);
        }
    }

    @Override
    public void activate(ServerPlayer player) {
        if (player.isAlive() && !player.isRemoved()) {
            // The next valid player attack will consume the Frost Lock.
            armedPlayers.add(player.getUUID());
        }
    }

    public static void tryLockEntity(ServerPlayer attacker, LivingEntity target) {
        if (!target.isAlive() || target.isRemoved()) {
            return;
        }

        PlayerData data = AstralCores.PLAYER_DATA.get(attacker);

        // Trusted entities cannot be frozen by Frost Lock.
        if (data != null && data.isTrusted(target.getUUID())) {
            return;
        }

        // Do not replace an existing Frost Lock.
        if (!activeLocks.containsKey(target.getUUID())) {
            activeLocks.put(target.getUUID(), new FrostLock(target));
        }
    }

    @Override
    public void tick(ServerPlayer player) {
        // Update all active locks and remove the ones that have expired.
        activeLocks.values().removeIf(FrostLock::tick);
    }

    private static final class FrostLock {

        private final LivingEntity entity;
        private final Vec3 position;
        private final float yaw;
        private final float pitch;
        private final boolean noGravity;

        private final BlockDisplay bottom;
        private final BlockDisplay top;

        private int ticks = 40;

        private FrostLock(LivingEntity entity) {
            this.entity = entity;
            position = entity.position();
            yaw = entity.getYRot();
            pitch = entity.getXRot();
            noGravity = entity.isNoGravity();

            ServerLevel level = (ServerLevel) entity.level();

            bottom = createDisplay(level);
            top = createDisplay(level);

            addKnockbackResistance();
            update();
        }

        private boolean tick() {
            if (!entity.isAlive() || entity.isRemoved()) {
                cleanup();
                return true;
            }

            update();

            if (--ticks <= 0) {
                cleanup();
                return true;
            }

            return false;
        }

        private void update() {
            // Keep the entity locked to its original position and rotation.
            entity.setPos(position.x, position.y, position.z);
            entity.setYRot(yaw);
            entity.setXRot(pitch);
            entity.setNoGravity(true);
            entity.setDeltaMovement(Vec3.ZERO);
            entity.setTicksFrozen(40);

            // Center the ice blocks using the entity's actual bounding box.
            AABB box = entity.getBoundingBox();
            double x = (box.minX + box.maxX) * 0.5 - 0.5;
            double z = (box.minZ + box.maxZ) * 0.5 - 0.5;

            bottom.setPos(x, box.minY, z);
            top.setPos(x, box.minY + 1, z);
        }

        private void addKnockbackResistance() {
            AttributeInstance attribute = entity.getAttribute(Attributes.KNOCKBACK_RESISTANCE);

            if (attribute != null && !attribute.hasModifier(FROST_MODIFIER_ID)) {
                attribute.addTransientModifier(new AttributeModifier(
                        FROST_MODIFIER_ID,
                        1.0,
                        AttributeModifier.Operation.ADD_VALUE
                ));
            }
        }

        private void removeKnockbackResistance() {
            AttributeInstance attribute = entity.getAttribute(Attributes.KNOCKBACK_RESISTANCE);

            if (attribute != null && attribute.hasModifier(FROST_MODIFIER_ID)) {
                attribute.removeModifier(FROST_MODIFIER_ID);
            }
        }

        private void cleanup() {
            bottom.discard();
            top.discard();

            removeKnockbackResistance();

            // Restore the gravity state the entity had before being frozen.
            if (entity.isAlive() && !entity.isRemoved()) {
                entity.setNoGravity(noGravity);
            }
        }

        private static BlockDisplay createDisplay(ServerLevel level) {
            BlockDisplay display = new BlockDisplay(EntityTypes.BLOCK_DISPLAY, level);

            display.setBlockState(Blocks.PACKED_ICE.defaultBlockState());

            level.addFreshEntity(display);

            return display;
        }
    }
}