package net.supremesurvival.supremecore.commonUtils.landmarks;

import org.bukkit.Location;

public class Landmark {
    private LandmarkType type;
    private Location location;
    private String title;
    private String announcement;

    public void setType(LandmarkType type){
        this.type = type;
    }

    public void setLocation(Location location){
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

    public String getTitle(){
        return this.title;
    }

    public String getAnnouncement(){
        return this.announcement;
    }

    public enum LandmarkType {
        CAVE,
        RUIN,
        FORT
    }
}
