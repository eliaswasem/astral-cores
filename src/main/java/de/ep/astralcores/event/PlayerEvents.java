package de.ep.astralcores.event;

import de.ep.astralcores.AstralCores;
import de.ep.astralcores.core.CoreType;
import de.ep.astralcores.core.cores.ShadowCore;
import de.ep.astralcores.event.logic.ChronoCorePassiveLogic;
import de.ep.astralcores.event.logic.CoreDeathLogic;
import de.ep.astralcores.event.logic.CoreInteractLogic;
import de.ep.astralcores.core.CoreFactory;
import de.ep.astralcores.event.logic.ShadowCorePassiveLogic;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;

public class PlayerEvents {

    /**
     * Registers all global player-centric hooks including network sessions and interaction mappings.
     * Combines connection lifecycles with physical right-click input packet interceptions.
     */
    public static void register() {

        /* Triggered immediately when a player completes the handshake and joins the game world */
        ServerPlayConnectionEvents.JOIN.register(
                (handler, sender, server) -> {
                    AstralCores.PLAYER_DATA.load(handler.player);
                }
        );

        /* Triggered immediately when a player logs out or loses connection to the server cluster */
        ServerPlayConnectionEvents.DISCONNECT.register(
                (handler, server) -> {
                    AstralCores.PLAYER_DATA.unload(handler.player);
                }
        );

        /**
         * Intercepts all vanilla player right-click item interactions globally on the server.
         * Extracts the active stack, validates it through our 1.21+ data component factory,
         * and routes the result directly into the profile account storage sequence.
         */
        UseItemCallback.EVENT.register((player, level, hand) -> {
            /* Safely bypasses client-side multi-execution loops to guarantee strict server-side state enforcement */
            if (level.isClientSide()) {
                return InteractionResult.PASS;
            }

            ServerPlayer serverPlayer = (ServerPlayer) player;
            ItemStack stack = serverPlayer.getItemInHand(hand);

            /**
             * Inspects the item's Custom Data mapping array for our custom namespace identifier tag.
             * If resolved successfully, handles validation and slots the core into PlayerData.
             * If not a custom core, returns PASS to allow regular vanilla action sequences to resume.
             */
            return CoreFactory.getCoreFromItem(stack)
                    .map(core -> CoreInteractLogic.executeEquip(serverPlayer, stack, core, hand))
                    .orElse(InteractionResult.PASS);
        });

        /**
         * Intercepts the death lifecycle sequence BEFORE damage finalization triggers.
         * Redirects queries into core logic modules to check for active anti-death mechanics.
         */
        ServerLivingEntityEvents.ALLOW_DEATH.register((entity, damageSource, damageAmount) -> {
            if (entity instanceof ServerPlayer serverPlayer) {
                return ChronoCorePassiveLogic.handleSecondTimeline(serverPlayer, damageSource, damageAmount);
            }
            return true;
        });

        /**
         * Intercepts the exact millisecond of a player's death lifecycle.
         * Invokes immediate item dropping mechanics alongside vanilla inventory drops.
         */
        ServerLivingEntityEvents.AFTER_DEATH.register((entity, damageSource) -> {
            /* Validates that the dying entity is safely a real physical ServerPlayer instance */
            if (entity instanceof ServerPlayer serverPlayer) {
               CoreDeathLogic.executeDeathDrop(serverPlayer);
            }
        });

        AttackEntityCallback.EVENT.register((player, level, hand, entity, hitResult) -> {
            if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {

                /* Fetch the specific attacker profile metadata structure from the registry */
                de.ep.astralcores.playerdata.PlayerData data = AstralCores.PLAYER_DATA.get(serverPlayer);

                /* Only trigger revelation sequences if the attacker explicitly owns and carries the shadow core */
                if (data != null && data.getEquippedCore() == CoreType.SHADOW_CORE) {
                   ShadowCore.revealPlayer(serverPlayer);
                }
            }
            return InteractionResult.PASS;
        });

        ServerLivingEntityEvents.AFTER_DAMAGE.register(
                (entity, damageSource, baseDamage, damageTaken, blocked) -> {

                    if (entity instanceof ServerPlayer serverPlayer) {

                        if (!ShadowCore.isPlayerHidden(serverPlayer.getUUID())) {
                            return;
                        }

                        ShadowCorePassiveLogic.handleDamageReveal(
                                serverPlayer,
                                damageSource
                        );
                    }
                }
        );
    }
}
