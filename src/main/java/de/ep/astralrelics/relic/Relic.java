package de.ep.astralrelics.relic;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;

import java.util.List;

public abstract class Relic {

    private final RelicType type;
    private final String name;
    private final Item baseItem;
    private final List<String> lore;
    private final int customModelData;
    private final int activeCooldown;
    private final int passiveCooldown;


    public Relic(
            RelicType type,
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


    public RelicType getType() {
        return type;
    }


    public String getAstralId() {
        return type.name().toLowerCase();
    }


    public String getName() {
        return name;
    }


    public Item getBaseItem() {
        return baseItem;
    }


    public List<String> getLore() {
        return lore;
    }


    public int getCustomModelData() {
        return customModelData;
    }


    public int getActiveCooldown() {
        return activeCooldown;
    }


    public int getPassiveCooldown() {
        return passiveCooldown;
    }


    public void tick(ServerPlayer player) {
    }


    public void applyPassive(ServerPlayer player) {
    }


    public abstract void activate(ServerPlayer player);
}