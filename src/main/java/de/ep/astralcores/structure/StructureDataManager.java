package de.ep.astralcores.structure;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

import java.util.*;
import java.util.stream.Collectors;

public class StructureDataManager extends SavedData {

    private final Map<UUID, StructureInstance> structures = new HashMap<>();
    private final List<PlannedStructure> plannedStructures = new ArrayList<>();

    public static final Codec<StructureDataManager> CODEC =
            CompoundTag.CODEC.flatXmap(
                    compound -> DataResult.success(StructureDataManager.load(compound)),
                    manager -> DataResult.success(manager.save(new CompoundTag()))
            );

    public static final SavedDataType<StructureDataManager> TYPE =
            new SavedDataType<>(
                    Identifier.fromNamespaceAndPath("astralcores", "structures"),
                    StructureDataManager::new,
                    StructureDataManager.CODEC,
                    DataFixTypes.LEVEL
            );

    public static StructureDataManager get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(TYPE);
    }

    public static StructureDataManager load(CompoundTag tag) {
        StructureDataManager manager = new StructureDataManager();

        ListTag list = tag.getList("structures").orElse(new ListTag());
        for (int i = 0; i < list.size(); i++) {
            CompoundTag structureTag = list.getCompound(i).orElse(new CompoundTag());

            String uuidString = structureTag.getString("core_uuid").orElse(null);
            if (uuidString == null) continue;

            UUID uuid;
            try {
                uuid = UUID.fromString(uuidString);
            } catch (IllegalArgumentException e) {
                continue;
            }

            String typeString = structureTag.getString("type").orElse(null);
            if (typeString == null) continue;

            StructureType type;
            try {
                type = StructureType.valueOf(typeString);
            } catch (IllegalArgumentException e) {
                continue;
            }

            BlockPos position = BlockPos.CODEC
                    .parse(NbtOps.INSTANCE, structureTag.get("position"))
                    .result()
                    .orElse(BlockPos.ZERO);

            boolean hasLinkedCore = structureTag.getBoolean("has_linked_core").orElse(true);
            manager.structures.put(uuid, new StructureInstance(uuid, type, position, hasLinkedCore));
        }

        ListTag plannedList = tag.getList("planned_structures").orElse(new ListTag());
        for (int i = 0; i < plannedList.size(); i++) {
            CompoundTag plannedTag = plannedList.getCompound(i).orElse(new CompoundTag());

            String typeString = plannedTag.getString("type").orElse(null);
            if (typeString == null) continue;

            StructureType type;
            try {
                type = StructureType.valueOf(typeString);
            } catch (IllegalArgumentException e) {
                continue;
            }

            BlockPos position = BlockPos.CODEC
                    .parse(NbtOps.INSTANCE, plannedTag.get("position"))
                    .result()
                    .orElse(BlockPos.ZERO);

            manager.plannedStructures.add(new PlannedStructure(type, position));
        }

        return manager;
    }

    public CompoundTag save(CompoundTag tag) {
        ListTag list = new ListTag();
        for (StructureInstance instance : structures.values()) {
            CompoundTag structureTag = new CompoundTag();
            structureTag.putString("core_uuid", instance.coreUuid().toString());
            structureTag.putString("type", instance.type().name());

            BlockPos.CODEC.encodeStart(NbtOps.INSTANCE, instance.position())
                    .result()
                    .ifPresent(nbt -> structureTag.put("position", nbt));

            structureTag.putBoolean("has_linked_core", instance.hasLinkedCore());
            list.add(structureTag);
        }
        tag.put("structures", list);

        ListTag plannedList = new ListTag();
        for (PlannedStructure planned : plannedStructures) {
            CompoundTag plannedTag = new CompoundTag();
            plannedTag.putString("type", planned.type().name());

            BlockPos.CODEC.encodeStart(NbtOps.INSTANCE, planned.position())
                    .result()
                    .ifPresent(nbt -> plannedTag.put("position", nbt));

            plannedList.add(plannedTag);
        }
        tag.put("planned_structures", plannedList);

        return tag;
    }

    public void addPlannedStructure(StructureType type, BlockPos position) {
        this.plannedStructures.add(new PlannedStructure(type, position));
        setDirty();
    }

    public long countPlannedStructures(StructureType type) {
        return plannedStructures.stream().filter(p -> p.type() == type).count();
    }

    public List<PlannedStructure> getPlannedStructuresInChunk(int chunkX, int chunkZ) {
        return plannedStructures.stream()
                .filter(p -> (p.position().getX() >> 4) == chunkX && (p.position().getZ() >> 4) == chunkZ)
                .collect(Collectors.toList());
    }

    public List<BlockPos> getAllStructurePositions() {
        List<BlockPos> positions = new ArrayList<>();
        for (StructureInstance instance : structures.values()) {
            if (instance.hasLinkedCore()) {
                positions.add(instance.position());
            }
        }
        for (PlannedStructure planned : plannedStructures) {
            positions.add(planned.position());
        }
        return positions;
    }

    public void convertPlannedToActive(PlannedStructure planned, BlockPos finalPos) {
        this.plannedStructures.remove(planned);
        UUID newCoreUuid = UUID.randomUUID();
        this.structures.put(newCoreUuid, new StructureInstance(newCoreUuid, planned.type(), finalPos, true));
        setDirty();
    }

    public void removePlannedStructure(PlannedStructure planned) {
        if (this.plannedStructures.remove(planned)) {
            setDirty();
        }
    }

    public Map<UUID, StructureInstance> getAllStructures() {
        return this.structures;
    }

    public StructureInstance getStructure(UUID coreUuid) {
        return this.structures.get(coreUuid);
    }

    public boolean delinkStructureByUUID(UUID coreUuid) {
        StructureInstance instance = structures.get(coreUuid);
        if (instance == null) return false;

        structures.put(coreUuid, new StructureInstance(instance.coreUuid(), instance.type(), instance.position(), false));
        setDirty();
        return true;
    }

    public long countLinkedStructures(StructureType type) {
        return structures.values().stream()
                .filter(instance -> instance.hasLinkedCore() && instance.type() == type)
                .count();
    }

    public void addStructure(UUID coreUuid, StructureType type, BlockPos position) {
        this.structures.put(coreUuid, new StructureInstance(coreUuid, type, position, true));
        setDirty();
    }

}
