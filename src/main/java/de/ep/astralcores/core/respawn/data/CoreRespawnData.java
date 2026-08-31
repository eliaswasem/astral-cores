package de.ep.astralcores.core.respawn.data;

import de.ep.astralcores.core.CoreType;

public record CoreRespawnData(
        CoreType type,
        long startTimestamp,
        long endTimestamp
) {
}