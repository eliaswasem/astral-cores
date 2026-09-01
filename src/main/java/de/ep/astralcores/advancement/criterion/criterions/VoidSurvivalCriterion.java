package de.ep.astralcores.advancement.criterion.criterions;

import com.mojang.serialization.Codec;
import net.minecraft.advancements.predicates.ContextAwarePredicate;
import net.minecraft.advancements.triggers.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class VoidSurvivalCriterion extends SimpleCriterionTrigger<VoidSurvivalCriterion.Conditions> {

    // Track players in void
    private final Set<UUID> playersInVoid = new HashSet<>();

    @Override
    public Codec<Conditions> codec() {
        return Conditions.CODEC;
    }

    // Process player tracking and trigger advancement
    public void trigger(ServerPlayer player) {
        UUID uuid = player.getUUID();

        // Mojimap uses getMinY() instead of getMinBuildHeight()
        int minBuildHeight = player.level().getMinY();

        // Add player to void tracking
        if (player.getY() < minBuildHeight) {
            playersInVoid.add(uuid);
            return;
        }

        // Trigger if player survived and returned alive
        if (playersInVoid.remove(uuid) && player.isAlive()) {
            this.trigger(player, conditions -> conditions.requirementsMet(player));
        }
    }

    // Remove player tracking data
    public void removePlayer(UUID uuid) {
        playersInVoid.remove(uuid);
    }

    public record Conditions(
            Optional<ContextAwarePredicate> playerPredicate
    ) implements SimpleCriterionTrigger.SimpleInstance {

        // JSON configuration codec
        public static final Codec<Conditions> CODEC =
                ContextAwarePredicate.CODEC
                        .optionalFieldOf("player")
                        .xmap(Conditions::new, Conditions::player)
                        .codec();

        @Override
        public Optional<ContextAwarePredicate> player() {
            return this.playerPredicate;
        }

        // Validate criterion conditions
        public boolean requirementsMet(ServerPlayer player) {
            return true;
        }
    }
}
