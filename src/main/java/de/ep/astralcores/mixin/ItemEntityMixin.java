package de.ep.astralcores.mixin;

import de.ep.astralcores.core.CoreFactory;
import de.ep.astralcores.structure.StructureManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;
import java.util.UUID;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin {

    @Shadow
    public abstract ItemStack getItem();

    @Shadow
    private int age;

    // Set despawn timer for core items to 0 every tick
    @Inject(method = "tick", at = @At("HEAD"))
    private void astralcores$preventCoreDespawn(CallbackInfo ci) {
        ItemStack stack = this.getItem();

        if (CoreFactory.isCore(stack)) {
            this.age = 0;
        }
    }

    // Prevent Cores from being destroyed by everything except the void
    @Inject(method = "hurtServer", at = @At("HEAD"), cancellable = true)
    private void astralcores$handleCoreDamageAndVoid(ServerLevel level, DamageSource source, float damage, CallbackInfoReturnable<Boolean> cir) {
        ItemEntity entity = (ItemEntity) (Object) this;
        ItemStack stack = this.getItem();

        if (!CoreFactory.isCore(stack)) {
            return;
        }

        // Core falls into the void
        if (source == entity.damageSources().fellOutOfWorld()) {

            Optional<UUID> coreUuid =
                    CoreFactory.getCoreUuid(stack);

            entity.discard();

            coreUuid.ifPresent(uuid ->
                    StructureManager.onCoreRemoved(
                            level,
                            uuid
                    )
            );

            cir.setReturnValue(false);
            return;
        }

        // Cancel all other damage sources for invulnerability
        cir.setReturnValue(false);
    }
}
