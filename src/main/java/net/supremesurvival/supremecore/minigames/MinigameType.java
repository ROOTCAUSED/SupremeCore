package net.supremesurvival.supremecore.minigames;

import java.util.Locale;

public enum MinigameType {
    PVP,
    PARKOUR,
    SPLEEF;

    public static MinigameType fromString(String raw) {
        if (raw == null) return null;
        try {
            return MinigameType.valueOf(raw.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }
}
