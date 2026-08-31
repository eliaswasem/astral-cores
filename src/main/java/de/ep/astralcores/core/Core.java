package de.ep.astralcores.core;

import de.ep.astralcores.core.CoreType;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;
import net.minecraft.world.item.Item;

import java.util.List;
import java.util.Locale;

// Abstract base class for all custom core modules
public abstract class Core {

    // Unique core type enum identifier
    private final CoreType type;

    // In-game display name for the core
    private final String name;

    // Vanilla item used to back the core physically
    private final Item baseItem;

    // Item lore description text lines
    private final List<String> lore;

    // Custom model data index for resource pack textures
    private final int customModelData;

    // Active ability cooldown duration in seconds
    private final int activeCooldown;

    // Passive ability cooldown duration in seconds
    private final int passiveCooldown;

    // Respawn duration for this core in seconds
    private final long respawnDuration;

    // Display name of the active ability
    private final String activeAbilityName;

    // Display name of the passive ability
    private final String passiveAbilityName;

    // Custom font character used for the action bar icon
    private final String customChar;

    // Boss bar color used while this core is respawning
    private final BossEvent.BossBarColor bossBarColor;

    // Constructor to define baseline core configurations
    public Core(
            CoreType type,
            String name,
            Item baseItem,
            List<String> lore,
            int customModelData,
            int activeCooldown,
            int passiveCooldown,
            long respawnDuration,
            String activeAbilityName,
            String passiveAbilityName,
            String customChar,
            BossEvent.BossBarColor bossBarColor
    ) {
        this.type = type;
        this.name = name;
        this.baseItem = baseItem;
        this.lore = lore;
        this.customModelData = customModelData;
        this.activeCooldown = activeCooldown;
        this.passiveCooldown = passiveCooldown;
        this.respawnDuration = respawnDuration;
        this.activeAbilityName = activeAbilityName;
        this.passiveAbilityName = passiveAbilityName;
        this.customChar = customChar;
        this.bossBarColor = bossBarColor;
    }

    // Gets the core type enum
    public CoreType getType() {
        return type;
    }

    // Converts the core enum name to a lowercase string key
    public String getCoreId() {
        return type.name().toLowerCase(Locale.ROOT);
    }

    // Gets the core display name
    public String getName() {
        return name;
    }

    // Gets the vanilla backing item
    public Item getBaseItem() {
        return baseItem;
    }

    // Gets the item description lore lines
    public List<String> getLore() {
        return lore;
    }

    // Gets the custom model data index
    public int getCustomModelData() {
        return customModelData;
    }

    // Gets the active ability cooldown time
    public int getActiveCooldown() {
        return activeCooldown;
    }

    // Gets the passive ability cooldown time
    public int getPassiveCooldown() {
        return passiveCooldown;
    }

    // Gets the respawn duration in seconds
    public long getRespawnDuration() {
        return respawnDuration;
    }

    // Gets the name of the active ability
    public String getActiveAbilityName() {
        return activeAbilityName;
    }

    // Gets the name of the passive ability
    public String getPassiveAbilityName() {
        return passiveAbilityName;
    }

    // Gets the action bar icon character code
    public String getCustomChar() {
        return customChar;
    }

    // Gets the boss bar color used for this core's respawn timer
    public BossEvent.BossBarColor getBossBarColor() {
        return bossBarColor;
    }

    // Runs on every single server tick for continuous tracking
    public void tick(ServerPlayer player) {
    }

    // Runs cleanup actions when the core is unequipped or lost on death
    public void onRemoved(ServerPlayer player) {
    }

    // Runs if a Player disconnects
    public void onPlayerDisconnect(ServerPlayer player) {
    }

    // Applies permanent background attributes or continuous buff checks
    public void applyPassive(ServerPlayer player) {
    }

    // Triggers the main active ability spell logic
    public abstract void activate(ServerPlayer player);
}
