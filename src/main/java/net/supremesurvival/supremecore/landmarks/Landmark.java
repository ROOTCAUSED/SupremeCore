package net.supremesurvival.supremecore.landmarks;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class Landmark {
    private LandmarkType type;
    private ProtectedRegion region;
    private String name;
    private String announcement;
    private String id;
    public Landmark(LandmarkType type, ProtectedRegion region, String name, String announcement, String id ){
        this.type = type;
        this.name = name;
        this.id = id;
        this.announcement = announcement;
        this.region = region;
    }
    public void setType(LandmarkType type){
        this.type = type;
    }

    public void setLocation(ProtectedRegion region){
        this.region = region;
    }

    public void setTitle(String name){
        this.name = name;
    }

    public void setAnnouncement(String announcement){
        this.announcement = announcement;
    }

    public LandmarkType getType(){
        return this.type;
    }
    public ProtectedRegion getRegion() { return this.region;}

    public String getTitle(){
        return this.name;
    }

    public String getAnnouncement(){
        return this.announcement;
    }

    public String getID(){
        return this.id;
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
