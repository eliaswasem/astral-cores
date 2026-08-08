package de.ep.astralcores.manager;

import de.ep.astralcores.AstralCores;
import de.ep.astralcores.playerdata.PlayerData;
import de.ep.astralcores.core.Core;
import de.ep.astralcores.core.CoreRegistry;
import de.ep.astralcores.core.CoreType;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class CoreActivateManager {

    /**
     * Orchestrates the entire core activation sequence. Handles validation,
     * core mapping, cooldown checks, execution, and cooldown enforcement.
     *
     * @param player The ServerPlayer executing the core ability.
     * @return An ActivationResult detailing success or failure reasons.
     */
    public static ActivationResult attemptActivation(ServerPlayer player) {
        PlayerData data = AstralCores.PLAYER_DATA.get(player);
        if (data == null) {
            return new ActivationResult(false, Component.literal("§cFailed to access your database profile."));
        }

        CoreType targetedType = data.getEquippedCore();
        if (targetedType == null) {
            return new ActivationResult(false, Component.literal("§cYou do not have a core equipped."));
        }

        Core relic = CoreRegistry.get(targetedType).orElse(null);
        if (relic == null) {
            return new ActivationResult(false, Component.literal("§cCritical: Stored core type mapping resolution failure."));
        }

        // Evaluate active capability execution lock restrictions via CooldownManager
        if (!CooldownManager.isActiveReady(player, targetedType)) {
            int remaining = CooldownManager.getActiveRemaining(player, targetedType);

            // Fetch the dedicated active capability identity text string dynamically
            String abilityName = relic.getActiveAbilityName();
            return new ActivationResult(false, Component.literal("§c" + abilityName + " §cis on cooldown for another " + remaining + "s."));
        }

        // Trigger the active core ability safely
        relic.activate(player);

        // Enforce active cooldown tracking boundaries onto the profile instance
        CooldownManager.startActiveCooldown(player, targetedType, relic.getActiveCooldown());

        return new ActivationResult(true, null);
    }

    /**
     * Simple immutable wrapper object to safely transport execution status feedback.
     */
    public record ActivationResult(boolean isSuccess, Component errorMessage) {}
}
