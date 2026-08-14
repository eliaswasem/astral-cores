package de.ep.astralcores.core.cores.logic;

import de.ep.astralcores.AstralCores;
import de.ep.astralcores.playerdata.PlayerData;
import de.ep.astralcores.util.Effects;
import de.ep.astralcores.util.TickTimer;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

public class FrostCoreLogic {

    // Temporary modifier that prevents knockback while Frost Lock is active.
    private static final Identifier FROST_MODIFIER_ID = Identifier.fromNamespaceAndPath("astralcores", "frost_lock_resistance");

    // Players whose next valid attack will trigger Frost Lock.
    public static final Set<ServerPlayer> armedPlayers = Collections.newSetFromMap(new WeakHashMap<>());

    // Currently frozen entities.
    private static final Map<LivingEntity, FrostLock> activeLocks = Collections.synchronizedMap(new WeakHashMap<>());

    public static void applyPassive(ServerPlayer player) {
        PlayerData data = AstralCores.PLAYER_DATA.get(player);

        // Applies Frost Aura to nearby non-trusted entities.
        AABB box = player.getBoundingBox().inflate(6.0);

        for (LivingEntity entity : player.level().getEntitiesOfClass(
                LivingEntity.class,
                box,
                entity -> entity != player && entity.isAlive())) {

            if (data != null && data.isTrusted(entity.getUUID())) {
                continue;
            }

            double distance = player.distanceTo(entity);
            int effectLevel = distance <= 3.5 ? 3 : distance <= 5.0 ? 2 : 1;

            Effects.applyEffect(entity, MobEffects.SLOWNESS, 40, effectLevel, false, false, false);
        }
    }

    public static void activate(ServerPlayer player) {
        if (player.isAlive() && !player.isRemoved()) {
            // The next valid player hit will trigger Frost Lock.
            armedPlayers.add(player);
        }
    }

    public static void onRemoved(ServerPlayer player) {
        // Cancel a pending Frost Lock activation.
        armedPlayers.remove(player);
    }

    public static void tick(ServerPlayer player) {
        // Updates all active Frost Locks and removes expired ones.
        activeLocks.values().removeIf(FrostLock::tick);
    }

    public static void handleFrostLock(ServerPlayer attacker, LivingEntity entity) {
        if (armedPlayers.remove(attacker)) {
            tryLockEntity(attacker, entity);
        }
    }

    public static void tryLockEntity(ServerPlayer attacker, LivingEntity target) {
        if (!target.isAlive() || target.isRemoved()) {
            return;
        }

        PlayerData data = AstralCores.PLAYER_DATA.get(attacker);

        // Trusted entities cannot be frozen.
        if (data != null && data.isTrusted(target.getUUID())) {
            return;
        }

        // Do not replace an existing Frost Lock.
        activeLocks.putIfAbsent(target, new FrostLock(target));
    }

    private static final class FrostLock {

        private final LivingEntity entity;
        private final Vec3 position;
        private final float yaw;
        private final float pitch;
        private final boolean noGravity;

        private final Display.BlockDisplay bottom;
        private final Display.BlockDisplay top;
        private final TickTimer timer = new TickTimer(40);

        private FrostLock(LivingEntity entity) {
            this.entity = entity;
            position = entity.position();
            yaw = entity.getYRot();
            pitch = entity.getXRot();
            noGravity = entity.isNoGravity();

            ServerLevel level = (ServerLevel) entity.level();

            bottom = createDisplay(level);
            top = createDisplay(level);

            // Prevents knockback during the freeze.
            addKnockbackResistance();

            update();
        }

        private boolean tick() {
            if (!entity.isAlive() || entity.isRemoved()) {
                cleanup();
                return true;
            }

            update();

            // Frost Lock lasts 40 ticks.
            if (timer.tick()) {
                cleanup();
                return true;
            }

            return false;
        }

        private void update() {
            // Keeps the entity at its original position and rotation.
            entity.setPos(position.x, position.y, position.z);
            entity.setYRot(yaw);
            entity.setXRot(pitch);
            entity.setNoGravity(true);
            entity.setDeltaMovement(Vec3.ZERO);
            entity.setTicksFrozen(40);

            // Centers the ice displays around the entity.
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
            // Remove the visual ice blocks.
            bottom.discard();
            top.discard();

            // Remove the temporary knockback resistance.
            removeKnockbackResistance();

            // Restore the entity's original gravity state.
            if (entity.isAlive() && !entity.isRemoved()) {
                entity.setNoGravity(noGravity);
            }
        }

        private static Display.BlockDisplay createDisplay(ServerLevel level) {
            Display.BlockDisplay display = new Display.BlockDisplay(EntityTypes.BLOCK_DISPLAY, level);
            display.setBlockState(Blocks.PACKED_ICE.defaultBlockState());
            level.addFreshEntity(display);
            return display;
        }
    }


}
