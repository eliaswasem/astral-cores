package de.ep.astralcores.structure;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import de.ep.astralcores.core.CoreType;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class StructureDataManager extends SavedData {

    // Stores all currently known structures by their unique UUID
    private final Map<UUID, StructureInstance> structures = new HashMap<>();

    // Defines the data codec instance matching the required constructor type signatures
    public static final Codec<StructureDataManager> CODEC =
            CompoundTag.CODEC.flatXmap(
                    compound -> DataResult.success(StructureDataManager.load(compound)),
                    manager -> DataResult.success(manager.save(new CompoundTag()))
            );


    // Defines the Minecraft saved data type used for loading and saving
    public static final SavedDataType<StructureDataManager> TYPE =
            new SavedDataType<>(
                    Identifier.fromNamespaceAndPath(
                            "astral_cores",
                            "structures"
                    ),
                    StructureDataManager::new,
                    StructureDataManager.CODEC,
                    DataFixTypes.LEVEL
            );


    // Gets the structure data manager from the world data storage
    public static StructureDataManager get(
            ServerLevel level
    ) {

        // Loads existing data or creates a new data file
        return level.getDataStorage()
                .computeIfAbsent(TYPE);
    }


    // Loads saved structure data from persistent world storage
    public static StructureDataManager load(
            CompoundTag tag
    ) {

        StructureDataManager manager =
                new StructureDataManager();


        // Reads the stored structure list from the NBT data
        ListTag list = tag.getList(
                "structures"
        ).orElse(new ListTag());


        // Recreates every stored structure entry
        for (int i = 0; i < list.size(); i++) {

            CompoundTag structureTag =
                    list.getCompound(i)
                            .orElse(new CompoundTag());


            // Reads the unique structure UUID
            String uuidString =
                    structureTag.getString("uuid")
                            .orElse(null);


            if (uuidString == null) {
                continue;
            }


            UUID uuid;

            try {

                uuid = UUID.fromString(uuidString);

            } catch (IllegalArgumentException exception) {

                continue;
            }


            // Reads the structure type
            String typeString =
                    structureTag.getString("type")
                            .orElse(null);


            if (typeString == null) {
                continue;
            }


            StructureType type;

            try {

                type = StructureType.valueOf(
                        typeString
                );

            } catch (IllegalArgumentException exception) {

                continue;
            }


            // Reads the structure world position safely from the compound
            // Reads the structure world position safely by fetching the raw underlying tag element
            BlockPos position =
                    BlockPos.CODEC
                            .parse(
                                    NbtOps.INSTANCE,
                                    structureTag.get("position")
                            )
                            .result()
                            .orElse(BlockPos.ZERO);



            // Reads whether the structure is currently active
            boolean active =
                    structureTag.getBoolean("active")
                            .orElse(true);


            // Adds the loaded structure back into memory
            manager.structures.put(
                    uuid,
                    new StructureInstance(
                            uuid,
                            type,
                            position,
                            active
                    )
            );
        }


        return manager;
    }


    // Saves all active and inactive structure data into NBT
    public CompoundTag save(
            CompoundTag tag
    ) {

        ListTag list =
                new ListTag();


        // Writes every stored structure into the NBT list
        for (StructureInstance instance :
                structures.values()) {

            CompoundTag structureTag =
                    new CompoundTag();


            // Saves the unique core UUID corresponding to the structure
            structureTag.putString(
                    "core_uuid",
                    instance.coreUuid().toString()
            );


            // Saves the structure type
            structureTag.putString(
                    "type",
                    instance.type().name()
            );


            // Saves the structure world position safely using the results callback map mapping
            BlockPos.CODEC
                    .encodeStart(
                            NbtOps.INSTANCE,
                            instance.position()
                    )
                    .result()
                    .ifPresent(nbt ->
                            structureTag.put("position", nbt)
                    );


            // Saves whether the structure is currently active
            structureTag.putBoolean(
                    "has_linked_core",
                    instance.has_linked_core
            );


            list.add(
                    structureTag
            );
        }


        // Stores the structure list inside the main NBT compound
        tag.put(
                "structures",
                list
        );


        return tag;
    }


    // Adds a newly spawned structure to the persistent data
    public void addStructure(
            UUID coreUuid,
            StructureType type,
            BlockPos position
    ) {

        structures.put(
                coreUuid,
                new StructureInstance(
                        coreUuid,
                        type,
                        position,
                        true
                )
        );


        // Marks the data as changed so Minecraft saves it
        setDirty();
    }


    // Disables a structure without deleting its persistent data
    public void delinkStructureByUUID(
            UUID coreUuid
    ) {

        StructureInstance instance =
                structures.get(coreUuid);


        if (instance == null) {
            return;
        }


        structures.put(
                coreUuid,
                new StructureInstance(
                        instance.coreUuid(),
                        instance.type(),
                        instance.position(),
                        false
                )
        );


        // Marks the data as changed so Minecraft saves it
        setDirty();
    }


    // Removes a structure completely from persistent data
    public void removeStructure(
            UUID coreUuid
    ) {

        structures.remove(
                coreUuid
        );


        // Marks the data as changed so Minecraft saves it
        setDirty();
    }


    // Returns the amount of active structures of a specific type
    public long countLinkedStructures(
            StructureType type
    ) {

        return structures.values()
                .stream()
                .filter(instance ->
                        instance.has_linked_core
                                &&
                                instance.type() == type
                )
                .count();
    }


    // Returns the stored structure for a specific UUID
    public StructureInstance getStructure(
            UUID coreUuid
    ) {

        return structures.get(
                coreUuid
        );
    }


    // Returns all currently stored structures
    public Map<UUID, StructureInstance> getStructures() {

        return structures;
    }


    // Defines the data format of one structure instance
    public record StructureInstance(
            UUID coreUuid,
            StructureType type,
            BlockPos position,
            boolean has_linked_core
    ) {
        // Resolves the associated CoreType mapping for this specific instance
        public CoreType coreType() {
            return CoreToStructureLookup.getCoreType(this.type)
                    .orElseThrow(() -> new IllegalStateException("Missing core type mapping for structure: " + this.type));
        }
    }
}
