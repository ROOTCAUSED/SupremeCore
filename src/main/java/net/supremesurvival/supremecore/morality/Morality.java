package net.supremesurvival.supremecore.morality;

import com.palmergames.bukkit.towny.event.player.PlayerKilledPlayerEvent;
import net.supremesurvival.supremecore.commonUtils.fileHandler.ConfigUtility;
import net.supremesurvival.supremecore.commonUtils.Logger;
import net.supremesurvival.supremecore.commonUtils.fileHandler.FileHandler;
import net.supremesurvival.supremecore.morality.player.MoralPlayer;
import net.supremesurvival.supremecore.commonUtils.placeholder.SupremePlaceholder;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.raid.RaidFinishEvent;
import org.bukkit.event.raid.RaidTriggerEvent;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

//Working - could do with cleaning up.
public class Morality implements Listener {
    private static FileConfiguration moralityConfig;
    private static HashMap<UUID, MoralPlayer> moralManagerList;
    private static Map<String, Integer> boundsMap = new LinkedHashMap<>();
    private static File dataFile;
    final static String handle = "Morality";
    public static void enable(){
        SupremePlaceholder.register();
        moralityConfig = ConfigUtility.getModuleConfig("Morality");
        ConfigurationSection moralityBounds = moralityConfig.getConfigurationSection("moralitybounds");
        for (String key : moralityBounds.getKeys(false)) {
            int value = moralityBounds.getInt(key);
            boundsMap.put(key.toUpperCase(), value);
        }
        moralManagerList = new HashMap<UUID, MoralPlayer>();
        dataFile = FileHandler.getDataFile("/Morality/playerdata.txt");
        HashMap <UUID, MoralPlayer> allData = new HashMap<UUID,MoralPlayer>();
        allData = FileHandler.loadAllMoralityData(dataFile, allData);
        }

        //currently overwrites whole file with online users. Need to correct this. Seems that reading back the whole file and overwriting the lines where uuid is contained in moralmanagerlist is the way to go. That or switch to json.
    public static void disable(){
        HashMap <UUID, MoralPlayer> allData = new HashMap<UUID,MoralPlayer>();
        allData = FileHandler.loadAllMoralityData(dataFile, allData);
        if(allData.isEmpty()){
            return;
        }
        for(UUID uuid : moralManagerList.keySet()){
            if(allData.containsKey(uuid)){
                allData.replace(uuid, moralManagerList.get(uuid));
            }else{allData.put(uuid, moralManagerList.get(uuid));}
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(dataFile))){
            for (Map.Entry<UUID, MoralPlayer> entry : allData.entrySet()){
            UUID playerID = entry.getKey();
            Integer morality = entry.getValue().getMorality();
            writer.write(playerID.toString() + ":" + morality);
            writer.write("\n");
            Logger.sendMessage("Wrote playerdata for " + playerID + ": Morality = " + morality, Logger.LogType.INFO, handle);
        }
    }
        catch (
    IOException e) {
        Logger.sendMessage(e.toString(), Logger.LogType.ERR, handle);
    }}
    @EventHandler
    public void HeroEvent(RaidFinishEvent event){
        List<Player> players = event.getWinners();
        Iterator playersIterator = players.iterator();
        Logger.sendMessage("Raid Finished", Logger.LogType.INFO,handle);
        while(playersIterator.hasNext()){
            Player player = (Player)playersIterator.next();
            Logger.sendMessage(player.getName() + "Participated in raid", Logger.LogType.INFO, handle);
            MoralPlayer moralPlayer = moralManagerList.get(player.getUniqueId());
            moralPlayer.addMorality(1000);
            moralityCheck(moralPlayer);
        }
    }

    @EventHandler
    public void BadOmenEvent(RaidTriggerEvent event){
        Player player = event.getPlayer();
        Logger.sendMessage("Raid started by " + player.getName(), Logger.LogType.INFO, handle);
        MoralPlayer moralPlayer = moralManagerList.get(player.getUniqueId());
        moralPlayer.reduceMorality(250);
        moralityCheck(moralPlayer);
    }

    @EventHandler
    public void mobDeath(EntityDeathEvent event){
        Entity entity = event.getEntity();
        if(entity instanceof Villager){
            if(((Villager) entity).getKiller() != null){
                Player player = ((Villager) entity).getKiller();
                MoralPlayer moralPlayer = moralManagerList.get(player.getUniqueId());
                moralPlayer.reduceMorality(50);
                moralityCheck(moralPlayer);
            }
        }
    }
    @EventHandler
    public void playerKill(PlayerKilledPlayerEvent event){
        Player killer = event.getKiller();
        Player victim = event.getVictim();
        MoralPlayer moralVictim = moralManagerList.get(victim.getUniqueId());
        MoralPlayer moralKiller = moralManagerList.get(killer.getUniqueId());
        if(moralVictim.getMorality()< -250){
            moralKiller.addMorality(150);

        }else if(moralVictim.getMorality()> 250){
            moralKiller.reduceMorality(150);
        }
    }

    @EventHandler
    public void join(PlayerJoinEvent event){
        Player player = event.getPlayer();
        if(!player.hasPlayedBefore()){
            //addplayer data with 0 values to wherever it ends up stored also
            moralManagerList.put(player.getUniqueId(),new MoralPlayer(player.getUniqueId(), 0));
            return;
        }
        if(moralManagerList.containsKey(player.getUniqueId())){
            //test
            moralityCheck(moralManagerList.get(player.getUniqueId()));
            return;
        }
        //load player data from wherever it is stored
        HashMap<UUID, MoralPlayer> moralManagerListTMP = FileHandler.loadMoralityData(event.getPlayer().getUniqueId(), moralManagerList, dataFile);
        if (moralManagerListTMP == null){
            Logger.sendMessage("Player data for " + player.getName() + " not found, inserting default value", Logger.LogType.INFO, handle);
            moralManagerList.put(player.getUniqueId(),new MoralPlayer(player.getUniqueId(),0));
            moralityCheck(moralManagerList.get(player.getUniqueId()));
            return;
        }
        moralManagerList = moralManagerListTMP;
        //test
        moralityCheck(moralManagerList.get(player.getUniqueId()));
        }

    //This class will check the players morality value against the configured morality bounds
    public static void moralityCheck(MoralPlayer player){
        Player bukkitPlayer = player.getPlayer();
        String moralStanding = "";
        int morality = player.getMorality();
        for (Map.Entry<String, Integer> entry : boundsMap.entrySet()) {
            if (morality < 0){
                if(entry.getValue() > 0){continue;}
                if(morality < entry.getValue()){moralStanding = entry.getKey();
                continue;}
                if(morality > entry.getValue()){
                    player.updateMoralStanding(MoralPlayer.MoralStanding.valueOf(moralStanding));
                    break;}
            }
            if (morality >= entry.getValue()) {
                moralStanding = entry.getKey();
                player.updateMoralStanding(MoralPlayer.MoralStanding.valueOf(moralStanding));
                break;
            }
        }
        bukkitPlayer.sendMessage("Your morality is " + player.getMorality() + ":" + player.getStanding());
    }
    public static String getMoralStanding(Player player){
        return moralManagerList.get(player.getUniqueId()).getStanding().toString();
    }
    public static int getMorality(Player player){
        return moralManagerList.get(player.getUniqueId()).getMorality();
    }
}
