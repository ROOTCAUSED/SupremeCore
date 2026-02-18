package net.supremesurvival.supremecore.morality.player;

import java.util.UUID;

public class MoralPlayer {
    private final UUID uuid;
    private int morality;
    private String standing;
    private long lastUpdatedEpochSeconds;

    public MoralPlayer(UUID uuid, int morality, String standing, long lastUpdatedEpochSeconds) {
        this.uuid = uuid;
        this.morality = morality;
        this.standing = standing;
        this.lastUpdatedEpochSeconds = lastUpdatedEpochSeconds;
    }

    public UUID getUuid() {
        return uuid;
    }

    public int getMorality() {
        return morality;
    }

    public void setMorality(int morality) {
        this.morality = morality;
    }

    public String getStanding() {
        return standing;
    }

    public void setStanding(String standing) {
        this.standing = standing;
    }

    public long getLastUpdatedEpochSeconds() {
        return lastUpdatedEpochSeconds;
    }

    public void setLastUpdatedEpochSeconds(long lastUpdatedEpochSeconds) {
        this.lastUpdatedEpochSeconds = lastUpdatedEpochSeconds;
    }
}
