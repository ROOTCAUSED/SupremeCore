package net.supremesurvival.supremecore.commonUtils.landmarks;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import net.supremesurvival.supremecore.SupremeCore;
import org.bukkit.Location;

public class Landmark {
    private LandmarkType type;
    private Region location;
    private String title;
    private String announcement;
    private String id;

    public static SupremeCore pl;
    public void setType(LandmarkType type){
        this.type = type;
    }

    public void setLocation(Region location){
        this.location = location;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public void setAnnouncement(String announcement){
        this.announcement = announcement;
    }

    public LandmarkType getType(){
        return this.type;
    }
    public boolean isRegion(){
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager regions = container.get(location.getWorld());
        regions.getRegion(id);
        return true;
    }
    public String getTitle(){
        return this.title;
    }

    public String getAnnouncement(){
        return this.announcement;
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
