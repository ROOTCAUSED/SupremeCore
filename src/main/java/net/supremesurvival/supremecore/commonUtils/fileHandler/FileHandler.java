package net.supremesurvival.supremecore.commonUtils.fileHandler;

import net.supremesurvival.supremecore.commonUtils.Logger;
import net.supremesurvival.supremecore.morality.player.MoralPlayer;
import org.bukkit.plugin.Plugin;

import java.io.*;
import java.util.*;

public class FileHandler {
    private static Plugin plugin;

    public FileHandler(Plugin pl) {
        plugin = pl;
    }

    final static String handle = "FileHandler";

    public static File getDataFile(String filePath) {
        Logger.sendMessage(plugin.getDataFolder().toString(), Logger.LogType.INFO, handle);
        File dataFile = new File(plugin.getDataFolder(), filePath);
        Logger.sendMessage(dataFile.getPath(), Logger.LogType.INFO, handle);
        return dataFile;
    }

    public static HashMap<UUID, MoralPlayer> loadMoralityData(UUID player, HashMap<UUID, MoralPlayer> hashMap, File file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line == null || line.isBlank()) continue;

                String[] parts = line.split(":");
                if (parts.length < 2) {
                    Logger.sendMessage("Skipping malformed morality line: " + line, Logger.LogType.WARN, handle);
                    continue;
                }

                try {
                    UUID playerUUID = UUID.fromString(parts[0]);
                    if (!playerUUID.equals(player)) continue;

                    int dataInt = Integer.parseInt(parts[1]);
                    MoralPlayer moralPlayer = new MoralPlayer(playerUUID, dataInt);
                    hashMap.put(playerUUID, moralPlayer);
                    Logger.sendMessage("Loaded Single Data for " + playerUUID + " into Hashmap", Logger.LogType.INFO, handle);
                    return hashMap;
                } catch (IllegalArgumentException ex) {
                    Logger.sendMessage("Skipping invalid morality entry: " + line, Logger.LogType.WARN, handle);
                }
            }
            // playerdata not located
        } catch (IOException e) {
            Logger.sendMessage(e.getMessage(), Logger.LogType.ERR, handle);
        }
        return null;
    }

    public static HashMap<UUID, MoralPlayer> loadAllMoralityData(File file, HashMap<UUID, MoralPlayer> hashmap) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line == null || line.isBlank()) continue;

                String[] parts = line.split(":");
                if (parts.length < 2) {
                    Logger.sendMessage("Skipping malformed morality line: " + line, Logger.LogType.WARN, handle);
                    continue;
                }

                try {
                    UUID playerUUID = UUID.fromString(parts[0]);
                    int dataInt = Integer.parseInt(parts[1]);
                    MoralPlayer moralPlayer = new MoralPlayer(playerUUID, dataInt);
                    hashmap.put(playerUUID, moralPlayer);
                } catch (IllegalArgumentException ex) {
                    Logger.sendMessage("Skipping invalid morality entry: " + line, Logger.LogType.WARN, handle);
                }
            }
            return hashmap;
        } catch (IOException e) {
            Logger.sendMessage(e.getMessage(), Logger.LogType.ERR, handle);
        }
        return null;
    }
}
