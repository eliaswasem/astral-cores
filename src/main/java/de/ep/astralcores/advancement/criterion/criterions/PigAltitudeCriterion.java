package de.ep.astralcores.advancement.criterion.criterions;

import com.mojang.serialization.Codec;
import net.minecraft.advancements.predicates.ContextAwarePredicate;
import net.minecraft.advancements.triggers.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.animal.pig.Pig;

import java.util.Optional;

public class PigAltitudeCriterion extends SimpleCriterionTrigger<PigAltitudeCriterion.Conditions> {

    @Override
    public Codec<Conditions> codec() {
        return Conditions.CODEC;
    }

    public void trigger(ServerPlayer player) {
        this.trigger(player, conditions -> conditions.requirementsMet(player));
    }

    public record Conditions(
            Optional<ContextAwarePredicate> playerPredicate
    ) implements SimpleCriterionTrigger.SimpleInstance {

        public static final Codec<Conditions> CODEC =
                ContextAwarePredicate.CODEC.optionalFieldOf("player")
                        .xmap(Conditions::new, Conditions::player)
                        .codec();

        @Override
        public Optional<ContextAwarePredicate> player() {
            return this.playerPredicate;
        }

        public boolean requirementsMet(ServerPlayer player) {
            return player.getVehicle() instanceof Pig pig
                    && pig.getY() >= 4000;
        }
    }
}