package net.supremesurvival.supremecore.minigames;

import org.bukkit.scheduler.BukkitTask;

import java.util.Set;
import java.util.UUID;

public class MinigameSession {
    private final MinigameType type;
    private final Set<UUID> participants;
    private final long startedAtMillis;
    private final long endAtMillis;
    private BukkitTask tickerTask;

    public MinigameSession(MinigameType type, Set<UUID> participants, long startedAtMillis, long endAtMillis) {
        this.type = type;
        this.participants = participants;
        this.startedAtMillis = startedAtMillis;
        this.endAtMillis = endAtMillis;
    }

    public MinigameType getType() {
        return type;
    }

    public Set<UUID> getParticipants() {
        return participants;
    }

    public long getStartedAtMillis() {
        return startedAtMillis;
    }

    public long getEndAtMillis() {
        return endAtMillis;
    }

    public BukkitTask getTickerTask() {
        return tickerTask;
    }

    public void setTickerTask(BukkitTask tickerTask) {
        this.tickerTask = tickerTask;
    }
}
