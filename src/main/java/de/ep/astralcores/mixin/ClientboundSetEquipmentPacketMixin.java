package de.ep.astralcores.mixin;

import com.mojang.datafixers.util.Pair;
import de.ep.astralcores.AstralCores;
import de.ep.astralcores.core.cores.ShadowCore;
import net.minecraft.network.protocol.game.ClientboundSetEquipmentPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ClientboundSetEquipmentPacket.class)
public class ClientboundSetEquipmentPacketMixin {

    @Shadow
    @Final
    private int entity;

    @Shadow
    @Final
    private List<Pair<EquipmentSlot, ItemStack>> slots;


    @Inject(
            method = "<init>(ILjava/util/List;)V",
            at = @At("TAIL")
    )
    private void hideShadowEquipment(
            int entityId,
            List<Pair<EquipmentSlot, ItemStack>> originalSlots,
            CallbackInfo ci
    ) {

        MinecraftServer server = AstralCores.getServer();

        if (server == null) {
            return;
        }


        ServerPlayer targetPlayer = null;

        for (ServerPlayer player : server.getPlayerList().getPlayers()) {

            if (player.getId() == this.entity) {
                targetPlayer = player;
                break;
            }
        }


        if (targetPlayer == null) {
            return;
        }


        if (!ShadowCore.isPlayerHidden(targetPlayer.getUUID())) {
            return;
        }


        /*
         * Replace all visible equipment with empty stacks.
         * Vanilla continues sending the packet normally.
         */
        this.slots.replaceAll(
                pair -> Pair.of(
                        pair.getFirst(),
                        ItemStack.EMPTY
                )
        );
    }
}