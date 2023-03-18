package net.supremesurvival.supremecore.commonUtils.fileHandler;

import net.supremesurvival.supremecore.commonUtils.Logger;
import net.supremesurvival.supremecore.morality.player.MoralPlayer;
import org.bukkit.plugin.Plugin;

import java.io.*;
import java.util.*;

public class FileHandler {
    private static Plugin plugin;
    public FileHandler (Plugin pl){
        plugin = pl;
    }
    final static String handle = "FileHandler";
    public static File getDataFile(String filePath){
        Logger.sendMessage(plugin.getDataFolder().toString(), Logger.LogType.INFO, handle);
        File dataFile = new File(plugin.getDataFolder(), filePath);
        Logger.sendMessage(dataFile.getPath(), Logger.LogType.INFO,handle);
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
                    Logger.sendMessage("Loaded Single Data for " + playerUUID + " into Hashmap", Logger.LogType.INFO, handle);
                    return hashMap;
                }
            }
            //playerdata not located
        }
        catch (IOException e){
            Logger.sendMessage(e.getMessage(), Logger.LogType.ERR, handle);
        }
        return null;
    }

    public static HashMap loadAllMoralityData(File file, HashMap hashmap){
        try (BufferedReader reader = new BufferedReader(new FileReader(file))){
            String line;
            while ((line=reader.readLine())!=null){
                String[] parts = line.split(":");
                UUID playerUUID = UUID.fromString(parts[0]);
                String data = parts [1];
                Integer dataInt = Integer.valueOf(data);
                MoralPlayer moralPlayer = new MoralPlayer(playerUUID, dataInt);
                hashmap.put(playerUUID, moralPlayer);
            }
            return hashmap;
}catch (IOException e){
            Logger.sendMessage(e.getMessage(), Logger.LogType.ERR, handle);
        }
        return null;
    }
}