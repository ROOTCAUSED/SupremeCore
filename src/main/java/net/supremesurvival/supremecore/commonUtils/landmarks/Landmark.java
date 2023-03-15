package net.supremesurvival.supremecore.commonUtils.landmarks;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import net.supremesurvival.supremecore.SupremeCore;
import net.supremesurvival.supremecore.commonUtils.Logger;
import org.bukkit.Location;

public class Landmark {
    private LandmarkType type;
    private ProtectedRegion region;
    private String title;
    private String announcement;
    private String id;

    public Landmark(LandmarkType type, ProtectedRegion region, String title, String announcement ){
        Logger.sendMessage("Registering Landmark: " + title, Logger.LogType.INFO, "[Landmarks]" );
        this.type = type;
        this.title = title;
        this.announcement = announcement;
        this.region = region;
    }
    public void setType(LandmarkType type){
        this.type = type;
    }

    public void setLocation(ProtectedRegion region){
        this.region = region;
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
    public ProtectedRegion getRegion() { return this.region;}

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
