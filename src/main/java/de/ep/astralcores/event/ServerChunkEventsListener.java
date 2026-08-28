package de.ep.astralcores.event;

import de.ep.astralcores.structure.StructureManager;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents;

public class ServerChunkEventsListener {
    public static void register() {
        ServerChunkEvents.CHUNK_LOAD.register((level, chunk, isNewChunk) -> {
            // Dispatches the chunk load event to the structure manager to check for planned injections
            StructureManager.onChunkLoad(level, chunk);
        });
    }
}
