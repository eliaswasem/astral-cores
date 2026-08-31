package de.ep.astralcores.manager;

import de.ep.astralcores.AstralCores;
import de.ep.astralcores.core.respawn.data.AltarData;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import java.util.Optional;

public class AltarManager {

    private static final Identifier ALTAR_STRUCTURE =
            Identifier.fromNamespaceAndPath(
                    "astralcores",
                    "core_altar"
            );

    public enum PlaceResult {
        SUCCESS,
        ALREADY_EXISTS,
        STRUCTURE_NOT_FOUND,
        PLACEMENT_FAILED
    }

    public static PlaceResult place(
            ServerLevel level,
            BlockPos pos
    ) {
        if (AstralCores.CORE_RESPAWN_DATA.altarExists()) {
            return PlaceResult.ALREADY_EXISTS;
        }

        Optional<StructureTemplate> template =
                level.getStructureManager()
                        .get(ALTAR_STRUCTURE);

        if (template.isEmpty()) {
            return PlaceResult.STRUCTURE_NOT_FOUND;
        }

        StructurePlaceSettings settings =
                new StructurePlaceSettings()
                        .setKnownShape(true);

        boolean placed =
                template.get().placeInWorld(
                        level,
                        pos,
                        pos,
                        settings,
                        level.getRandom(),
                        2
                );

        if (!placed) {
            return PlaceResult.PLACEMENT_FAILED;
        }

        AstralCores.CORE_RESPAWN_DATA.setAltar(
                level.dimension().identifier(),
                pos
        );

        return PlaceResult.SUCCESS;
    }

    public static void remove() {
        AstralCores.CORE_RESPAWN_DATA.removeAltar();
    }

    public static Optional<BlockPos> getCoreRespawnPos() {
        AltarData altar =
                AstralCores.CORE_RESPAWN_DATA.getAltar();

        if (altar == null) {
            return Optional.empty();
        }

        return Optional.of(
                altar.pos().offset(1, 1, 1)
        );
    }
}