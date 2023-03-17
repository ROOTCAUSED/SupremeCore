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
import net.supremesurvival.supremecore.commonUtils.fileHandler.FileHandler;
import net.supremesurvival.supremecore.commonUtils.morality.player.MoralPlayer;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;

import java.io.*;
import java.util.*;

import static net.supremesurvival.supremecore.commonUtils.landmarks.PlayerListeners.landmarksDiscovered;


public class LandmarkManager {

    public static ArrayList<Landmark> landmarkList = new ArrayList<Landmark>();
    public static Landmark landmark;

    private static Plugin plugin;

    private static File dataFile;
    public static void enable(){
        FileConfiguration config = ConfigUtility.getModuleConfig("Landmarks");

        if(config.contains("Landmarks")){
            ConfigurationSection section = config.getConfigurationSection("Landmarks");
            if(section != null){
                for(final String landmarkKey : section.getKeys(false)){
                    RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
                    World world = Bukkit.getWorld(section.getString(landmarkKey + ".World"));
                    ProtectedRegion landmarkRegion = container.get(BukkitAdapter.adapt(world)).getRegion(section.getString(landmarkKey + ".region_ID"));
                    String announcement = section.getString(landmarkKey + ".Announcement");
                    Landmark.LandmarkType type = Landmark.LandmarkType.valueOf(section.getString(landmarkKey + ".Type"));
                    String landmarkID = section.getString(landmarkKey + ".LandmarkID");
                    String landmarkName = section.getString(landmarkKey + ".Name");
                    landmark = new Landmark(type, landmarkRegion, landmarkName, announcement, landmarkID);
                    landmarkList.add(landmark);
                }
                dataFile = FileHandler.getDataFile("/Landmarks/playerdata.txt");
                loadData();
            }
        }
    }

    private static void loadData(){
        try (BufferedReader reader = new BufferedReader(new FileReader(dataFile))){
            String line;
            while ((line=reader.readLine())!=null){
                String[] parts = line.split(":");
                UUID playerUUID = UUID.fromString(parts[0]);
                String[] landmarkIDs = parts[1].split(";");
                List <String> landmarks = new ArrayList<>();
                for (String landmarkID : landmarkIDs){
                    landmarks.add(landmarkID);
                }
                landmarksDiscovered.put(playerUUID,landmarks);
                Logger.sendMessage("Loaded landmark data for " + playerUUID.toString() + " into LandmarksDiscovered Hashmap", Logger.LogType.INFO, "[Landmark Manager]");
                for (String s : landmarks) {
                    Logger.sendMessage(s.toString(), Logger.LogType.INFO, "LandmarkManager");
                }
            }
        }
        catch (IOException e){
            Logger.sendMessage(e.getMessage(), Logger.LogType.ERR, "Landmarks");
        }
    }

    public static void init(File file){

    }
    public static void disable(){
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(dataFile))){
            for (Map.Entry<UUID, List<String>> entry : landmarksDiscovered.entrySet()){
                UUID playerID = entry.getKey();
                List <String> landmarks = entry.getValue();
                writer.write(playerID.toString() + ":");
                for(String landmark : landmarks){
                    writer.write(landmark + ";");
                }
                writer.write("\n");
            }
        }
        catch (IOException e) {
            Logger.sendMessage(e.toString(), Logger.LogType.ERR, "[Landmarks]");
        }
    }

}
