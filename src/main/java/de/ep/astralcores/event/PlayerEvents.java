package de.ep.astralcores.event;

import de.ep.astralcores.AstralCores;
import de.ep.astralcores.core.CoreType;
import de.ep.astralcores.core.cores.ShadowCore;
import de.ep.astralcores.event.logic.*;
import de.ep.astralcores.core.CoreFactory;
import de.ep.astralcores.playerdata.PlayerData;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;

public class PlayerEvents {

    // Registers game event listeners related to player actions and lifecycles
    public static void register() {

        // Loads player profile data from the database when they join the server
        ServerPlayConnectionEvents.JOIN.register(
                (handler, sender, server) -> {
                    AstralCores.PLAYER_DATA.load(handler.player);
                }
        );

        // Unloads player profile data from RAM when they disconnect from the server
        ServerPlayConnectionEvents.DISCONNECT.register(
                (handler, server) -> {
                    AstralCores.PLAYER_DATA.unload(handler.player);
                }
        );

        // Intercepts item right-click actions to handle custom core equipment
        UseItemCallback.EVENT.register((player, level, hand) -> {
            if (level.isClientSide()) {
                return InteractionResult.PASS;
            }

            ServerPlayer serverPlayer = (ServerPlayer) player;
            ItemStack stack = serverPlayer.getItemInHand(hand);

            // Equips the core if the item is recognized as a valid core type
            return CoreFactory.getCoreFromItem(stack)
                    .map(core -> CoreInteractLogic.executeEquip(serverPlayer, stack, core, hand))
                    .orElse(InteractionResult.PASS);
        });

        // Intercepts the death check to evaluate anti-death mechanics like the chrono core
        ServerLivingEntityEvents.ALLOW_DEATH.register((entity, source, damageAmount) -> {
            if (entity instanceof ServerPlayer serverPlayer) {
                return ChronoCorePassiveLogic.handleSecondTimeline(serverPlayer, source, damageAmount);
            }
            return true;
        });

        ServerLivingEntityEvents.ALLOW_DAMAGE.register((entity, source, amount) -> {
            if (entity instanceof ServerPlayer serverPlayer) {
                return AeroCorePassiveLogic.handleFallShockwave(serverPlayer, source);
            }
            return true;
        });

        // Handles custom item dropping actions immediately after a player dies
        ServerLivingEntityEvents.AFTER_DEATH.register((entity, damageSource) -> {
            if (entity instanceof ServerPlayer serverPlayer) {
                CoreDeathLogic.executeDeathDrop(serverPlayer);
                BerserkerCorePassivLogic.handleBloodlust(serverPlayer, damageSource);
            }
        });

        // Reveals hidden shadow core players if they attack an entity
        AttackEntityCallback.EVENT.register((player, level, hand, entity, hitResult) -> {
            if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {

                PlayerData data = AstralCores.PLAYER_DATA.get(serverPlayer);

                if (data != null && data.getEquippedCore() == CoreType.SHADOW_CORE) {
                    ShadowCore.revealPlayer(serverPlayer);
                }
            }
            return InteractionResult.PASS;
        });

        // Evaluates if a hidden shadow core player should be revealed after taking damage
        ServerLivingEntityEvents.AFTER_DAMAGE.register(
                (entity, source, baseDamage, damageTaken, blocked) -> {

                    if (entity instanceof ServerPlayer serverPlayer) {

                        IllusionCorePassiveLogic.handleMirrorImage(serverPlayer, source);

                        if (!ShadowCore.isPlayerHidden(serverPlayer.getUUID())) {
                            return;
                        }

                        ShadowCorePassiveLogic.handleDamageReveal(
                                serverPlayer,
                                source
                        );
                    }
                }
        );
    }
}
