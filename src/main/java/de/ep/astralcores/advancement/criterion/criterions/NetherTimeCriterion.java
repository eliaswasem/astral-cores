package de.ep.astralcores.advancement.criterion.criterions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.predicates.ContextAwarePredicate;
import net.minecraft.advancements.triggers.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;

import java.awt.*;
import java.util.Optional;

public class NetherTimeCriterion
        extends SimpleCriterionTrigger<NetherTimeCriterion.Conditions> {

    public static final NetherTimeCriterion INSTANCE = new NetherTimeCriterion();

    @Override
    public Codec<Conditions> codec() {
        return Conditions.CODEC;
    }

    public void trigger(ServerPlayer player , long ticks) {
        this.trigger(player, conditions ->
                player.level().dimensionTypeRegistration().is(BuiltinDimensionTypes.NETHER)
                        && ticks >= conditions.requiredTicks()
        );
    }

    public record Conditions(
            Optional<ContextAwarePredicate> playerPredicate,
            long requiredTicks
    ) implements SimpleCriterionTrigger.SimpleInstance {

        public static final Codec<Conditions> CODEC = RecordCodecBuilder.create(instance ->
                instance.group(
                        ContextAwarePredicate.CODEC.optionalFieldOf("player")
                                .forGetter(Conditions::player),
                        Codec.LONG.fieldOf("required_ticks")
                                .forGetter(Conditions::requiredTicks)
                ).apply(instance, Conditions::new)
        );

        @Override
        public Optional<ContextAwarePredicate> player() {
            return playerPredicate;
        }

        public static Conditions create(long ticks) {
            return new Conditions(Optional.empty(), ticks);
        }
    }
}