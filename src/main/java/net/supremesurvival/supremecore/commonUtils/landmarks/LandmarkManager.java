package net.supremesurvival.supremecore.commonUtils.landmarks;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import net.supremesurvival.supremecore.commonUtils.ConfigUtility;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.UUID;

public class LandmarkManager {
    private static HashMap<UUID, Landmark> moralManagerList;


    public static void enable(){
        ConfigUtility.getModuleConfig("Landmarks");
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();

    }

}
