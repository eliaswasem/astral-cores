package de.ep.astralcores.core;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;

import java.util.List;

/**
 * Represents the abstract base template structure for all custom Astral Cores.
 * Manages foundational registry properties, passive tick behaviors, and active skill execution paths.
 */
public abstract class Core {

    /** The unique registration type categorizing this specific core module instance. */
    private final CoreType type;

    /** The formatted display name used across item names, system messages, and GUIs. */
    private final String name;

    /** The underlying Minecraft item asset mapping that represents this core physically. */
    private final Item baseItem;

    /** The descriptive tooltip lore displayed below the item stack in inventory views. */
    private final List<String> lore;

    /** The custom model data NBT index hook utilized for specific resource pack textures. */
    private final int customModelData;

    /** The raw static cooldown interval limitation enforced on the active trigger capability. */
    private final int activeCooldown;

    /** The raw static cooldown interval limitation enforced on the passive recovery channel. */
    private final int passiveCooldown;

    /**
     * Constructs a unified core instance template configuration block.
     *
     * @param type             The unique module registry type identifier track.
     * @param name             The colorized in-game display name formatting string.
     * @param baseItem         The vanilla item instance chosen to back this asset container.
     * @param lore             The list of unformatted or colorized lore text tooltip array indices.
     * @param customModelData  The integer value matching custom resource pack predicate rules.
     * @param activeCooldown   The default active spell casting cooldown limit in seconds.
     * @param passiveCooldown  The default passive utility processing cooldown limit in seconds.
     */
    public Core(
            CoreType type,
            String name,
            Item baseItem,
            List<String> lore,
            int customModelData,
            int activeCooldown,
            int passiveCooldown
    ) {
        this.type = type;
        this.name = name;
        this.baseItem = baseItem;
        this.lore = lore;
        this.customModelData = customModelData;
        this.activeCooldown = activeCooldown;
        this.passiveCooldown = passiveCooldown;
    }

    /**
     * Retrieves the structural core type registry key binding.
     *
     * @return The immutable enum type entry wrapper.
     */
    public CoreType getType() {
        return type;
    }

    /**
     * Converts the registration enum moniker into a lowercase data identification key.
     * Often used for internal data serializations or file naming structures.
     *
     * @return A sanitized lower-case string identifier representation.
     */
    public String getCoreId() {
        return type.name().toLowerCase();
    }

    /**
     * Retrieves the formatted user-facing interface name string.
     *
     * @return The colorized name string asset block.
     */
    public String getName() {
        return name;
    }

    /**
     * Retrieves the underlying backing material reference mapped to this item configuration.
     *
     * @return The native vanilla item definition.
     */
    public Item getBaseItem() {
        return baseItem;
    }

    /**
     * Retrieves the complete lines of textual description embedded into the custom lore list.
     *
     * @return An immutable or mutable collection list of localized tooltips.
     */
    public List<String> getLore() {
        return lore;
    }

    /**
     * Retrieves the persistent resource pack integer model index attachment layer identifier.
     *
     * @return An integer representing custom model data parameters.
     */
    public int getCustomModelData() {
        return customModelData;
    }

    /**
     * Retrieves the threshold boundary tracking limit assigned for active skill uses.
     *
     * @return The baseline activation cooldown integer value metric tracker.
     */
    public int getActiveCooldown() {
        return activeCooldown;
    }

    /**
     * Retrieves the threshold boundary tracking limit assigned for passive skill ticks.
     *
     * @return The baseline structural ticking cooldown integer value metric tracker.
     */
    public int getPassiveCooldown() {
        return passiveCooldown;
    }

    /**
     * Processes execution loops on every game engine cycle heartbeat if registered via active ticking hooks.
     *
     * @param player The active ServerPlayer target receiving continuous loop attention updates.
     */
    public void tick(ServerPlayer player) {
    }

    /**
     * Triggered automatically whenever this core is unequipped, swapped, or lost upon death.
     * Override this method in specific core classes to clean up long-running potion effects,
     * custom attribute modifiers, or continuous background tasks.
     *
     * @param player The ServerPlayer entity who is losing this core module.
     */
    public void onRemoved(ServerPlayer player) {
        // Left blank intentionally. Cores that do not manage persistent
        // status effect buffers do not need to override this.
    }

    /**
     * Enforces continuous passive attribute buffs or environmental checks.
     * Typically evaluated regularly via loops like the twentyTickLoop cycle tracker.
     *
     * @param player The active ServerPlayer target receiving the permanent passive applications.
     */
    public abstract void applyPassive(ServerPlayer player);

    /**
     * Fires the major primary active capability spell effect directly at or around the executor position.
     *
     * @param player The active ServerPlayer source triggering the core skill execution command path.
     */
    public abstract void activate(ServerPlayer player);
}
