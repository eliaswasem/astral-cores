package de.ep.astralcores.event;

import de.ep.astralcores.AstralCores;
import de.ep.astralcores.advancement.criterion.CriterionRegistry;
import de.ep.astralcores.core.CoreFactory;
import de.ep.astralcores.core.CoreRegistry;
import de.ep.astralcores.core.CoreType;
import de.ep.astralcores.core.cores.logic.*;
import de.ep.astralcores.core.respawn.CoreRespawnManager;
import de.ep.astralcores.event.logic.CoreDeathLogic;
import de.ep.astralcores.event.logic.CoreInteractLogic;
import de.ep.astralcores.manager.CooldownManager;
import de.ep.astralcores.playerdata.PlayerData;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;

public class PlayerEventsListener {

    // Register all server events
    public static void register() {

        // Handle player join data loading
        ServerPlayConnectionEvents.JOIN.register(
                (handler, sender, server) -> {
                    ServerPlayer player = handler.player;

                    AstralCores.PLAYER_DATA.load(player);
                    CoreRespawnManager.addPlayer(player);
                }
        );

        // Handle player disconnect cleanup safely
        ServerPlayConnectionEvents.DISCONNECT.register(
                (handler, server) -> {
                    ServerPlayer player = handler.player;
                    if (player == null) return;

                    server.execute(
                            () -> {
                                // Process player data unloading
                                if (AstralCores.PLAYER_DATA != null) {
                                    PlayerData data = AstralCores.PLAYER_DATA.get(player);

                                    if (data != null) {
                                        CoreType coreType = data.getEquippedCore();

                                        if (coreType != null) {
                                            CoreRegistry.get(coreType).ifPresent(
                                                    core -> core.onPlayerDisconnect(player)
                                            );
                                        }
                                    }

                                    AstralCores.PLAYER_DATA.unload(player);
                                }

                                // Remove player from active core managers
                                CoreRespawnManager.removePlayer(player);

                                // Remove player from void tracking data
                                CriterionRegistry.VOID_SURVIVAL.removePlayer(player.getUUID());
                            }
                    );
                }
        );

        // Clear tracking data when player respawns after death
        ServerPlayerEvents.AFTER_RESPAWN.register(
                (oldPlayer, newPlayer, alive) -> {
                    CriterionRegistry.VOID_SURVIVAL.removePlayer(newPlayer.getUUID());
                }
        );

        // Handle core equipment on item interaction
        UseItemCallback.EVENT.register((player, level, hand) -> {
            if (level.isClientSide()) {
                return InteractionResult.PASS;
            }

            ServerPlayer serverPlayer = (ServerPlayer) player;
            ItemStack stack = serverPlayer.getItemInHand(hand);

            // Execute core equip logic if item is a core
            return CoreFactory.getCoreFromItem(stack)
                    .map(core -> CoreInteractLogic.executeEquip(serverPlayer, stack, core, hand))
                    .orElse(InteractionResult.PASS);
        });

        // Evaluate custom timeline mechanics on death
        ServerLivingEntityEvents.ALLOW_DEATH.register((entity, source, damageAmount) -> {
            if (entity instanceof ServerPlayer serverPlayer) {
                return ChronoCoreLogic.handleSecondTimeline(serverPlayer, source, damageAmount);
            }
            return true;
        });

        // Handle defensive core mechanics during damage
        ServerLivingEntityEvents.ALLOW_DAMAGE.register((entity, source, amount) -> {
            if (!(entity instanceof ServerPlayer player)) {
                return true;
            }
            if (!AeroCoreLogic.handleFallShockwave(player, source)) {
                return false;
            }

            if (!IllusionCoreLogic.handleMirrorImage(player, source)) {
                return false;
            }
            return true;
        });

        // Trigger active core abilities immediately after death
        ServerLivingEntityEvents.AFTER_DEATH.register((entity, damageSource) -> {
            if (entity instanceof ServerPlayer serverPlayer) {
                CoreDeathLogic.executeDeathDrop(serverPlayer);
                BerserkerCoreLogic.handleBloodlust(serverPlayer, damageSource);
            }
        });

        // Reveal shadow players on attacking an entity
        AttackEntityCallback.EVENT.register((player, level, hand, entity, hitResult) -> {
            if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {

                PlayerData data = AstralCores.PLAYER_DATA.get(serverPlayer);

                if (data != null && data.getEquippedCore() == CoreType.SHADOW_CORE) {
                    ShadowCoreLogic.revealPlayer(serverPlayer);
                }
            }
            return InteractionResult.PASS;
        });

        // Process offensive core triggers after damage calculation
        ServerLivingEntityEvents.AFTER_DAMAGE.register(
                (entity, source, baseDamage, damageTaken, blocked) -> {

                    if (entity instanceof ServerPlayer serverPlayer) {
                        ShadowCoreLogic.handleDamageReveal(serverPlayer, source);
                    }

                    if (source.getEntity() instanceof ServerPlayer attacker) {
                        FrostCoreLogic.handleFrostLock(attacker, entity);
                    }
                }
        );
    }
}
