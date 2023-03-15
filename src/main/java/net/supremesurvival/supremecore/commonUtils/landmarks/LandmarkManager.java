package net.supremesurvival.supremecore.commonUtils.landmarks;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import net.supremesurvival.supremecore.commonUtils.ConfigUtility;
import net.supremesurvival.supremecore.commonUtils.Logger;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class LandmarkManager {

    private static ArrayList<Landmark> landmarkList = new ArrayList<Landmark>();
    public static Landmark landmark;
    public static void enable(){
        FileConfiguration config = ConfigUtility.getModuleConfig("Landmarks");
        if(config.contains("Landmarks")){
            ConfigurationSection section = config.getConfigurationSection("Landmarks");
            if(section != null){
                for(final String landmarkKey : section.getKeys(false)){
                    RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
                    World world = Bukkit.getWorld(section.getString(landmarkKey + "world"));
                    ProtectedRegion landmarkRegion = container.get(BukkitAdapter.adapt(world)).getRegion(section.getString(landmarkKey + "regionID"));
                    String announcement = section.getString(landmarkKey + "announcement");
                    Landmark.LandmarkType type = Landmark.LandmarkType.valueOf(section.getString(landmarkKey + "Type"));
                    String landmarkName = section.getString(landmarkKey + "name");
                    landmark = new Landmark(type,landmarkRegion, landmarkName, announcement);
                    landmarkList.add(landmark);
                }
            }
        }
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();

    }

}
