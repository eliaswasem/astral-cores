package de.ep.astralcores.advancement.criterion.criterions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.predicates.ContextAwarePredicate;
import net.minecraft.advancements.triggers.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

import java.util.Optional;

public class NetherTimeCriterion
        extends SimpleCriterionTrigger<NetherTimeCriterion.Conditions> {

    @Override
    public Codec<Conditions> codec() {
        return Conditions.CODEC;
    }

    public void trigger(
            ServerPlayer player,
            long elapsedTime
    ) {
        this.trigger(
                player,
                conditions ->
                        player.level().dimension() == Level.NETHER
                                && conditions.requirementsMet(elapsedTime)
        );
    }

    public record Conditions(
            Optional<ContextAwarePredicate> player,
            long requiredTime
    ) implements SimpleCriterionTrigger.SimpleInstance {

        public static final Codec<Conditions> CODEC =
                RecordCodecBuilder.create(instance ->
                        instance.group(
                                ContextAwarePredicate.CODEC
                                        .optionalFieldOf("player")
                                        .forGetter(Conditions::player),

                                Codec.LONG
                                        .fieldOf("required_time")
                                        .forGetter(Conditions::requiredTime)

                        ).apply(instance, Conditions::new)
                );

        public boolean requirementsMet(
                long elapsedTime
        ) {
            return elapsedTime >= requiredTime;
        }

        public static Conditions create(
                long milliseconds
        ) {
            return new Conditions(
                    Optional.empty(),
                    milliseconds
            );
        }
    }
}