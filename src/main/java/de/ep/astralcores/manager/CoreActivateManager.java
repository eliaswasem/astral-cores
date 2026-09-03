package de.ep.astralcores.manager;

import de.ep.astralcores.AstralCores;
import de.ep.astralcores.actionbar.ActionBarManager;
import de.ep.astralcores.core.data.CoreActivationResult;
import de.ep.astralcores.playerdata.PlayerData;
import de.ep.astralcores.core.Core;
import de.ep.astralcores.core.CoreRegistry;
import de.ep.astralcores.core.CoreType;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class CoreActivateManager {

    // Validates player profiles, evaluates timers, and triggers active core ability execution sequences
    public static ActivationResult attemptActivation(ServerPlayer player) {
        PlayerData data = AstralCores.PLAYER_DATA.get(player);
        if (data == null) {
            return new ActivationResult(false, Component.literal("Failed to access your database profile."));
        }

        CoreType targetedType = data.getEquippedCore();
        if (targetedType == null) {
            return new ActivationResult(false, Component.literal("You do not have a core equipped."));
        }

        Core core = CoreRegistry.get(targetedType).orElse(null);
        if (core == null) {
            return new ActivationResult(false, Component.literal("Your stored core type doesn't exist."));
        }

        // Rejects execution sequence if the specific core capacity is currently locked on cooldown
        if (!CoreCooldownManager.isActiveReady(data, targetedType)) {
            int remaining = CoreCooldownManager.getActiveRemaining(data, targetedType);

            String abilityName = core.getActiveAbilityName();
            return new ActivationResult(false, Component.literal(abilityName)
                    .append(" is on cooldown for another ")
                    .append(String.valueOf(remaining))
                    .append("s."));
        }

        // Executes the custom capability features bound to the target core instance
        CoreActivationResult result = core.activate(player);

        if (result == CoreActivationResult.EXECUTED) {
            CoreCooldownManager.startActiveCooldown(
                    data,
                    targetedType,
                    core.getActiveCooldown()
            );
        }

        ActionBarManager.tick(player, data);

        return new ActivationResult(
                result != CoreActivationResult.FAILED,
                null
        );
    }

    // Transfers action status signals and failure feedback details through execution streams
    public record ActivationResult(boolean isSuccess, Component errorMessage) {}
}
