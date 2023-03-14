package net.supremesurvival.supremecore.commonUtils.landmarks;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import net.supremesurvival.supremecore.commonUtils.ConfigUtility;
import net.supremesurvival.supremecore.commonUtils.Logger;
import net.supremesurvival.supremecore.commonUtils.tomes.Tome;
import org.bukkit.Bukkit;
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
                for(final String tomeKey : section.getKeys(false)){
                    String tomeTitle = section.getString(tomeKey + ".title");
                    String tomeAuthor = section.getString(tomeKey + ".author");
                    String pages = section.getString(tomeKey + ".pages");
                    String preamble = section.getString(tomeKey + ".preamble");
                    List<String> lore = section.getStringList(tomeKey + ".lore");
                    landmark = new Landmark();
                    landmarkList.add(landmark);
                    Logger.sendMessage("Registered Tome: " + tomeTitle, Logger.LogType.INFO,"Tomes");
                }
            }
        }
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();

    }

}
