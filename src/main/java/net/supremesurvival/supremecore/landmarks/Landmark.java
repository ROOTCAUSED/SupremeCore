package net.supremesurvival.supremecore.landmarks;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class Landmark {
    private final LandmarkType type;
    private final ProtectedRegion region;
    private final String name;
    private final String announcement;
    private final String id;
    private final String worldName;
    private final int discoveryRadius;

    public Landmark(LandmarkType type, ProtectedRegion region, String name, String announcement, String id, String worldName, int discoveryRadius) {
        this.type = type;
        this.name = name;
        this.id = id;
        this.announcement = announcement;
        this.region = region;
        this.worldName = worldName;
        this.discoveryRadius = discoveryRadius;
    }

    public LandmarkType getType() {
        return this.type;
    }

    public ProtectedRegion getRegion() {
        return this.region;
    }

    public String getTitle() {
        return this.name;
    }

    public String getAnnouncement() {
        return this.announcement;
    }

    public String getID() {
        return this.id;
    }

    public String getWorldName() {
        return worldName;
    }

    public int getDiscoveryRadius() {
        return discoveryRadius;
    }

    public BlockVector3 getCenter() {
        return region.getMinimumPoint().add(region.getMaximumPoint()).divide(2);
    }

    public boolean isDiscoveredAt(int x, int y, int z) {
        if (region.contains(x, y, z)) {
            return true;
        }
        BlockVector3 c = getCenter();
        long dx = x - c.getBlockX();
        long dy = y - c.getBlockY();
        long dz = z - c.getBlockZ();
        long d2 = dx * dx + dy * dy + dz * dz;
        long r2 = (long) discoveryRadius * discoveryRadius;
        return d2 <= r2;
    }

    public enum LandmarkType {
        CAVE,
        RUIN,
        FORT,
        VILLAGE,
        WAYSHRINE,
        MOUNTAIN,
        MISC,
    }
}
