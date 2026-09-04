package de.ep.astralcores.mixin;

import de.ep.astralcores.manager.CoreActivateManager;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerGamePacketListenerImpl.class)
public class ServerGamePacketListenerImpMixin {

    @Shadow
    public ServerPlayer player;

    @Inject(
            method = "handlePlayerAction",
            at = @At("HEAD"),
            cancellable = true
    )
    private void onPlayerAction(ServerboundPlayerActionPacket packet, CallbackInfo ci) {
        // Checks if the Player uses Shift + F at the same time
        if (packet.getAction() == ServerboundPlayerActionPacket.Action.SWAP_ITEM_WITH_OFFHAND
                && this.player.isShiftKeyDown()) {

            var result = CoreActivateManager.attemptActivation(player);

            if (!result.isSuccess() && result.errorMessage() != null) {
                player.sendSystemMessage(result.errorMessage());
            }

            // Blocks the normal item switch
            ci.cancel();
        }
    }
}
