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

public class AeroCore extends Core {

    private static final Identifier AERO_MODIFIER_ID = Identifier.fromNamespaceAndPath("astralcores", "aero_air_cushion");

    public AeroCore() {
        super(
                CoreType.AERO_CORE,
                "§bAero Core",
                Items.FEATHER,
                List.of(
                        "§7Forged in shifting air currents.",
                        "§6[Active: Aero Jump]"
                ),
                10001,
                15,
                0
        );
    }

    @Override
    public void applyPassive(ServerPlayer player) {
        AttributeInstance attribute = player.getAttribute(Attributes.FALL_DAMAGE_MULTIPLIER);
        if (attribute != null) {
            if (!attribute.hasModifier(AERO_MODIFIER_ID)) {
                // -0.70 cuts the base 1.0 multiplier down to 0.30 (70% damage reduction)
                attribute.addTransientModifier(new AttributeModifier(
                        AERO_MODIFIER_ID,
                        -0.70,
                        AttributeModifier.Operation.ADD_VALUE
                ));
            }
        }
    }

    @Override
    public void onRemoved(ServerPlayer player) {
        AttributeInstance attribute = player.getAttribute(Attributes.FALL_DAMAGE_MULTIPLIER);
        if (attribute != null) {
            if (attribute.hasModifier(AERO_MODIFIER_ID)) {
                attribute.removeModifier(AERO_MODIFIER_ID);
            }
        }
    }

    @Override
    public void activate(ServerPlayer player) {
        // Will be implemented later
    }
}
