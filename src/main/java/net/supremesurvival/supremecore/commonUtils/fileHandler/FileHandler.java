package net.supremesurvival.supremecore.commonUtils.fileHandler;

import it.unimi.dsi.fastutil.Hash;
import net.supremesurvival.supremecore.commonUtils.Logger;
import net.supremesurvival.supremecore.commonUtils.morality.player.MoralPlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.*;
import java.util.*;

import static net.supremesurvival.supremecore.commonUtils.landmarks.PlayerListeners.landmarksDiscovered;

public class FileHandler {
    private static Plugin plugin;
    public FileHandler (Plugin pl){
        this.plugin = pl;
    }

    public static File getDataFile(String filePath){
        Logger.sendMessage(plugin.getDataFolder().toString(), Logger.LogType.INFO, "[File Handler]");
        File dataFile = new File(plugin.getDataFolder(), filePath);
        Logger.sendMessage(dataFile.getPath().toString(), Logger.LogType.INFO,"[File Handler]");
        return dataFile;
    }

    public static HashMap loadMoralityData(UUID player, HashMap hashMap, File file){
        UUID retrieveUUID = player;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))){
            String line;
            while ((line=reader.readLine())!=null){
                String[] parts = line.split(":");
                UUID playerUUID = UUID.fromString(parts[0]);
                if (playerUUID.equals(retrieveUUID)){
                    String data = parts[1];
                    Integer dataInt = Integer.valueOf(data);
                    MoralPlayer moralPlayer = new MoralPlayer(playerUUID, dataInt);
                    hashMap.put(playerUUID,moralPlayer);
                    Logger.sendMessage("Loaded Single Data for " + playerUUID.toString() + " into Hashmap", Logger.LogType.INFO, "File Manager");
                    return hashMap;
                }
            }
            //playerdata not located
        }
        catch (IOException e){
            Logger.sendMessage(e.getMessage(), Logger.LogType.ERR, "Landmarks");
        }
        return null;
    }
}
