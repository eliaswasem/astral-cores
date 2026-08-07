package de.ep.astralcores.core.cores;

import de.ep.astralcores.core.Core;
import de.ep.astralcores.core.CoreType;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Items;

import java.util.List;

/**
 * Gravity Core Module.
 * Grants permanent 50% knockback resistance while equipped.
 */
public class GravityCore extends Core {

    private static final Identifier GRAVITY_MODIFIER_ID = Identifier.fromNamespaceAndPath("astralcores", "gravity_heavy_presence");

    public GravityCore() {
        super(
                CoreType.GRAVITY_CORE,
                "§5Gravity Core",
                Items.HEAVY_CORE,
                List.of(
                        "§7Control the fabric of mass.",
                        "§6[Active: Gravity Pull]"
                ),
                10004,
                25,
                0
        );
    }

    @Override
    public void applyPassive(ServerPlayer player) {
        AttributeInstance attribute = player.getAttribute(Attributes.KNOCKBACK_RESISTANCE);
        if (attribute != null) {
            // Guard clause to avoid duplicate stacking in the twentyTickLoop
            if (!attribute.hasModifier(GRAVITY_MODIFIER_ID)) {
                // Adds +0.5 to the base player knockback resistance (0.0)
                attribute.addTransientModifier(new AttributeModifier(
                        GRAVITY_MODIFIER_ID,
                        0.5,
                        AttributeModifier.Operation.ADD_VALUE
                ));
            }
        }
    }

    @Override
    public void onRemoved(ServerPlayer player) {
        AttributeInstance attribute = player.getAttribute(Attributes.KNOCKBACK_RESISTANCE);
        if (attribute != null) {
            // Wipes the modifier instantly on unequip, core swap, or death
            if (attribute.hasModifier(GRAVITY_MODIFIER_ID)) {
                attribute.removeModifier(GRAVITY_MODIFIER_ID);
            }
        }
    }

    @Override
    public void activate(ServerPlayer player) {
        // Will be implemented later
    }
}
