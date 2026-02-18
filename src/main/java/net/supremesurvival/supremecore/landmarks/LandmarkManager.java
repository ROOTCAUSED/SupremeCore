package net.supremesurvival.supremecore.landmarks;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionManager;
import net.supremesurvival.supremecore.commonUtils.Logger;
import net.supremesurvival.supremecore.commonUtils.fileHandler.ConfigUtility;
import net.supremesurvival.supremecore.commonUtils.fileHandler.FileHandler;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.*;
import java.util.*;

import static net.supremesurvival.supremecore.landmarks.PlayerListeners.landmarksDiscovered;
import static net.supremesurvival.supremecore.landmarks.PlayerListeners.landmarksDiscoveredAt;

public class LandmarkManager {

    public static ArrayList<Landmark> landmarkList = new ArrayList<>();
    private static final Map<String, Landmark> landmarkById = new HashMap<>();

    final static String handle = "Landmark Manager";
    private static File dataFile;

    public static void enable() {
        landmarkList.clear();
        landmarkById.clear();

        FileConfiguration config = ConfigUtility.getModuleConfig("Landmarks");
        if (!config.contains("Landmarks")) {
            Logger.sendMessage("No Landmarks section found.", Logger.LogType.WARN, handle);
            return;
        }

        ConfigurationSection section = config.getConfigurationSection("Landmarks");
        if (section == null) {
            Logger.sendMessage("Landmarks section is null.", Logger.LogType.WARN, handle);
            return;
        }

        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        for (String landmarkKey : section.getKeys(false)) {
            String base = landmarkKey + ".";
            String worldName = section.getString(base + "World");
            String regionId = section.getString(base + "region_ID");
            String announcement = section.getString(base + "Announcement", "");
            String typeRaw = section.getString(base + "Type", "MISC");
            String landmarkID = section.getString(base + "landmarkID", section.getString(base + "LandmarkID", landmarkKey));
            String landmarkName = section.getString(base + "Name", landmarkKey);
            int discoveryRadius = section.getInt(base + "DiscoveryRadius", 16);

            World world = Bukkit.getWorld(worldName);
            if (world == null) {
                Logger.sendMessage("Skipping landmark '" + landmarkKey + "': world not found: " + worldName, Logger.LogType.ERR, handle);
                continue;
            }

            RegionManager rm = container.get(BukkitAdapter.adapt(world));
            if (rm == null) {
                Logger.sendMessage("Skipping landmark '" + landmarkKey + "': region manager unavailable.", Logger.LogType.ERR, handle);
                continue;
            }

            ProtectedRegion landmarkRegion = rm.getRegion(regionId);
            if (landmarkRegion == null) {
                Logger.sendMessage("Skipping landmark '" + landmarkKey + "': region not found: " + regionId, Logger.LogType.ERR, handle);
                continue;
            }

            Landmark.LandmarkType type;
            try {
                type = Landmark.LandmarkType.valueOf(typeRaw.toUpperCase(Locale.ROOT));
            } catch (IllegalArgumentException ex) {
                type = Landmark.LandmarkType.MISC;
            }

            Landmark landmark = new Landmark(type, landmarkRegion, landmarkName, announcement, landmarkID, worldName, discoveryRadius);
            landmarkList.add(landmark);
            landmarkById.put(landmark.getID(), landmark);
        }

        dataFile = FileHandler.getDataFile("/Landmarks/playerdata.txt");
        loadData();
        Logger.sendMessage("Loaded " + landmarkList.size() + " landmarks.", Logger.LogType.INFO, handle);
    }

    public static Landmark getLandmarkById(String id) {
        return landmarkById.get(id);
    }

    private static void loadData() {
        try (BufferedReader reader = new BufferedReader(new FileReader(dataFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(":", 2);
                if (parts.length < 2) continue;
                UUID playerUUID = UUID.fromString(parts[0]);
                String[] landmarkIDs = parts[1].split(";");
                List<String> landmarks = new ArrayList<>();
                Map<String, Long> discoveredAt = new HashMap<>();
                for (String token : landmarkIDs) {
                    if (token.isBlank()) continue;
                    String id = token;
                    long when = 0L;
                    if (token.contains("@")) {
                        String[] idTs = token.split("@", 2);
                        id = idTs[0];
                        try {
                            when = Long.parseLong(idTs[1]);
                        } catch (NumberFormatException ignored) {
                            when = 0L;
                        }
                    }
                    landmarks.add(id);
                    if (when > 0) discoveredAt.put(id, when);
                }
                landmarksDiscovered.put(playerUUID, landmarks);
                landmarksDiscoveredAt.put(playerUUID, discoveredAt);
            }
        } catch (FileNotFoundException ignored) {
            // First boot / no data yet.
        } catch (IOException e) {
            Logger.sendMessage(e.getMessage(), Logger.LogType.ERR, handle);
        }
    }

    public static void disable() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(dataFile))) {
            for (Map.Entry<UUID, List<String>> entry : landmarksDiscovered.entrySet()) {
                UUID playerID = entry.getKey();
                List<String> landmarks = entry.getValue();
                if (landmarks == null || landmarks.isEmpty()) continue;
                writer.write(playerID + ":");
                Map<String, Long> discoveredAt = landmarksDiscoveredAt.getOrDefault(playerID, Collections.emptyMap());
                for (String landmark : landmarks) {
                    long ts = discoveredAt.getOrDefault(landmark, 0L);
                    if (ts > 0) {
                        writer.write(landmark + "@" + ts + ";");
                    } else {
                        writer.write(landmark + ";");
                    }
                }
                writer.newLine();
            }
        } catch (IOException e) {
            Logger.sendMessage(e.toString(), Logger.LogType.ERR, handle);
        }
    }
}
