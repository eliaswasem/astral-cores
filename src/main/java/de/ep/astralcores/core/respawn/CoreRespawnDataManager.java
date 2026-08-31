package de.ep.astralcores.core.respawn;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import de.ep.astralcores.AstralCores;
import de.ep.astralcores.core.CoreType;
import de.ep.astralcores.core.respawn.data.AltarData;
import de.ep.astralcores.core.respawn.data.CoreRespawnData;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.Identifier;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

import java.util.ArrayList;
import java.util.List;

public class CoreRespawnDataManager extends SavedData {

    private static final String ALTAR_TAG = "altar";
    private static final String DIMENSION_TAG = "dimension";
    private static final String POS_TAG = "pos";

    private static final String RESPAWNS_TAG = "respawns";
    private static final String TYPE_TAG = "type";
    private static final String START_TIMESTAMP_TAG = "startTimestamp";
    private static final String END_TIMESTAMP_TAG = "endTimestamp";

    public static final Codec<CoreRespawnDataManager> CODEC =
            CompoundTag.CODEC.flatXmap(
                    tag ->
                            DataResult.success(
                                    CoreRespawnDataManager.load(tag)
                            ),
                    manager ->
                            DataResult.success(
                                    manager.save(new CompoundTag())
                            )
            );

    public static final SavedDataType<CoreRespawnDataManager> TYPE =
            new SavedDataType<>(
                    Identifier.fromNamespaceAndPath(
                            "astralcores",
                            "core_respawns"
                    ),
                    CoreRespawnDataManager::new,
                    CODEC,
                    DataFixTypes.LEVEL
            );

    private AltarData altar;

    private final List<CoreRespawnData> respawns =
            new ArrayList<>();

    public CoreRespawnDataManager() {
    }

    public static CoreRespawnDataManager get() {
        return AstralCores.getServer()
                .getDataStorage()
                .computeIfAbsent(TYPE);
    }

    public AltarData getAltar() {
        return altar;
    }

    public boolean altarExists() {
        return altar != null;
    }

    public void setAltar(
            Identifier dimension,
            BlockPos pos
    ) {
        altar = new AltarData(
                dimension,
                pos
        );

        setDirty();
    }

    public void removeAltar() {
        if (altar == null) {
            return;
        }

        altar = null;

        setDirty();
    }

    public void addRespawn(
            CoreType type,
            long startTimestamp,
            long endTimestamp
    ) {
        respawns.add(
                new CoreRespawnData(
                        type,
                        startTimestamp,
                        endTimestamp
                )
        );

        setDirty();
    }

    public List<CoreRespawnData> getAllRespawns() {
        return List.copyOf(respawns);
    }

    public boolean hasRespawns() {
        return !respawns.isEmpty();
    }

    public void removeRespawn(
            CoreRespawnData respawn
    ) {
        if (respawns.remove(respawn)) {
            setDirty();
        }
    }

    public static CoreRespawnDataManager load(
            CompoundTag tag
    ) {
        CoreRespawnDataManager manager =
                new CoreRespawnDataManager();

        loadAltar(
                manager,
                tag
        );

        loadRespawns(
                manager,
                tag
        );

        return manager;
    }

    private static void loadAltar(
            CoreRespawnDataManager manager,
            CompoundTag tag
    ) {
        tag.getCompound(ALTAR_TAG)
                .ifPresent(
                        altarTag -> {

                            String dimensionString =
                                    altarTag
                                            .getString(
                                                    DIMENSION_TAG
                                            )
                                            .orElse(null);

                            if (dimensionString == null) {
                                return;
                            }

                            Identifier dimension;

                            try {
                                dimension =
                                        Identifier.parse(
                                                dimensionString
                                        );
                            } catch (IllegalArgumentException exception) {
                                return;
                            }

                            BlockPos pos =
                                    BlockPos.CODEC
                                            .parse(
                                                    NbtOps.INSTANCE,
                                                    altarTag.get(POS_TAG)
                                            )
                                            .result()
                                            .orElse(null);

                            if (pos == null) {
                                return;
                            }

                            manager.altar =
                                    new AltarData(
                                            dimension,
                                            pos
                                    );
                        }
                );
    }

    private static void loadRespawns(
            CoreRespawnDataManager manager,
            CompoundTag tag
    ) {
        ListTag respawnsTag =
                tag.getList(RESPAWNS_TAG)
                        .orElse(new ListTag());

        for (int i = 0;
             i < respawnsTag.size();
             i++) {

            CompoundTag respawnTag =
                    respawnsTag
                            .getCompound(i)
                            .orElse(null);

            if (respawnTag == null) {
                continue;
            }

            String typeString =
                    respawnTag
                            .getString(TYPE_TAG)
                            .orElse(null);

            if (typeString == null) {
                continue;
            }

            CoreType type;

            try {
                type =
                        CoreType.valueOf(
                                typeString
                        );
            } catch (IllegalArgumentException exception) {
                continue;
            }

            long startTimestamp =
                    respawnTag
                            .getLong(
                                    START_TIMESTAMP_TAG
                            )
                            .orElse(0L);

            long endTimestamp =
                    respawnTag
                            .getLong(
                                    END_TIMESTAMP_TAG
                            )
                            .orElse(0L);

            manager.respawns.add(
                    new CoreRespawnData(
                            type,
                            startTimestamp,
                            endTimestamp
                    )
            );
        }
    }

    public CompoundTag save(
            CompoundTag tag
    ) {
        saveAltar(tag);
        saveRespawns(tag);

        return tag;
    }

    private void saveAltar(
            CompoundTag tag
    ) {
        if (altar == null) {
            return;
        }

        CompoundTag altarTag =
                new CompoundTag();

        altarTag.putString(
                DIMENSION_TAG,
                altar.dimension().toString()
        );

        BlockPos.CODEC
                .encodeStart(
                        NbtOps.INSTANCE,
                        altar.pos()
                )
                .result()
                .ifPresent(
                        nbt ->
                                altarTag.put(
                                        POS_TAG,
                                        nbt
                                )
                );

        tag.put(
                ALTAR_TAG,
                altarTag
        );
    }

    private void saveRespawns(
            CompoundTag tag
    ) {
        ListTag respawnsTag =
                new ListTag();

        for (CoreRespawnData respawn :
                respawns) {

            CompoundTag respawnTag =
                    new CompoundTag();

            respawnTag.putString(
                    TYPE_TAG,
                    respawn.type().name()
            );

            respawnTag.putLong(
                    START_TIMESTAMP_TAG,
                    respawn.startTimestamp()
            );

            respawnTag.putLong(
                    END_TIMESTAMP_TAG,
                    respawn.endTimestamp()
            );

            respawnsTag.add(
                    respawnTag
            );
        }

        tag.put(
                RESPAWNS_TAG,
                respawnsTag
        );
    }
}