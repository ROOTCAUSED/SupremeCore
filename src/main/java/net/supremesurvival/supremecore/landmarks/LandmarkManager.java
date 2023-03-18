package net.supremesurvival.supremecore.landmarks;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import net.supremesurvival.supremecore.commonUtils.fileHandler.ConfigUtility;
import net.supremesurvival.supremecore.commonUtils.Logger;
import net.supremesurvival.supremecore.commonUtils.fileHandler.FileHandler;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.*;
import java.util.*;

import static net.supremesurvival.supremecore.landmarks.PlayerListeners.landmarksDiscovered;


public class LandmarkManager {

    public static ArrayList<Landmark> landmarkList = new ArrayList<Landmark>();
    public static Landmark landmark;

    private static Plugin plugin;
    final static String handle = "Landmark Manager";
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
                if(line.trim().isEmpty()) continue; // Skip empty lines
                String[] parts = line.split(":");
                UUID playerUUID = UUID.fromString(parts[0]);
                String[] landmarkIDs = parts[1].split(";");
                List <String> landmarks = new ArrayList<>();
                Collections.addAll(landmarks, landmarkIDs);
                landmarksDiscovered.put(playerUUID,landmarks);
                Logger.sendMessage("Loaded landmark data for " + playerUUID + " into LandmarksDiscovered Hashmap", Logger.LogType.INFO, handle);
                for (String s : landmarks) {
                    Logger.sendMessage(s, Logger.LogType.INFO, handle);
                }
            }
        }
        catch (IOException e){
            Logger.sendMessage(e.getMessage(), Logger.LogType.ERR, handle);
        }
    }

    public static void disable(){
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(dataFile))){
            for (Map.Entry<UUID, List<String>> entry : landmarksDiscovered.entrySet()){
                UUID playerID = entry.getKey();
                List <String> landmarks = entry.getValue();
                if (landmarks.isEmpty()) continue; // Skip players with no landmarks
                writer.write(playerID.toString() + ":");
                for(String landmark : landmarks){
                    writer.write(landmark + ";");
                }
                writer.write("\n");
            }
        }
        catch (IOException e) {
            Logger.sendMessage(e.toString(), Logger.LogType.ERR, handle);
        }
    }

}
