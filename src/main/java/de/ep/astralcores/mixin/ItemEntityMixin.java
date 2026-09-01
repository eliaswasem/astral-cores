package de.ep.astralcores.mixin;

import de.ep.astralcores.core.Core;
import de.ep.astralcores.core.CoreFactory;
import de.ep.astralcores.core.respawn.CoreRespawnManager;
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

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin {

    @Shadow
    public abstract ItemStack getItem();

    @Shadow
    private int age;

    // Set despawn timer for core items to 0 every tick
    @Inject(method = "tick", at = @At("HEAD"))
    private void astralcores$preventCoreDespawn(
            CallbackInfo ci
    ) {
        ItemStack stack =
                this.getItem();

        if (CoreFactory.isCore(stack)) {
            this.age = 0;
        }
    }

    // Prevent cores from being destroyed by normal damage
    @Inject(
            method = "hurtServer",
            at = @At("HEAD"),
            cancellable = true
    )
    private void astralcores$handleCoreDamageAndVoid(
            ServerLevel level,
            DamageSource source,
            float damage,
            CallbackInfoReturnable<Boolean> cir
    ) {
        ItemStack stack =
                this.getItem();

        if (!CoreFactory.isCore(stack)) {
            return;
        }

        // Cancel all normal damage sources for core items
        cir.setReturnValue(false);
    }

    // Handles core-specific item ticking and void respawns
    @Inject(method = "tick", at = @At("HEAD"))
    private void astralcores$handleCoreTick(
            CallbackInfo ci
    ) {
        ItemEntity entity =
                (ItemEntity) (Object) this;

        ItemStack stack =
                this.getItem();

        if (!CoreFactory.isCore(stack)) {
            return;
        }

        // Core has fallen into the void
        if (!entity.level().isClientSide()
                && entity.getY()
                < entity.level().getMinY() - 64) {

            Optional<Core> core =
                    CoreFactory.getCoreFromItem(stack);

            if (core.isEmpty()) {
                return;
            }

            CoreRespawnManager.addRespawn(
                    core.get().getType()
            );


            entity.discard();

            return;
        }

        // Prevent normal item despawn
        this.age = 0;
    }
}