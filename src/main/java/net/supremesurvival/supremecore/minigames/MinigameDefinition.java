package net.supremesurvival.supremecore.minigames;

import org.bukkit.Location;

public record MinigameDefinition(
        MinigameType type,
        int minPlayers,
        int durationSeconds,
        Location arenaSpawn
) {
}
