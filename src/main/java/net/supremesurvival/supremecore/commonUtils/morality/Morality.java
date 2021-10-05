package net.supremesurvival.supremecore.commonUtils.morality;

import net.supremesurvival.supremecore.commonUtils.ConfigUtility;
import net.supremesurvival.supremecore.commonUtils.Logger;
import net.supremesurvival.supremecore.commonUtils.morality.player.MoralPlayer;
import org.bukkit.Bukkit;
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

import java.util.*;

public class Morality implements Listener {
    private static FileConfiguration moralityConfig;
    private static HashMap<UUID, MoralPlayer> moralManagerList;

    public static void enable(){
        moralityConfig = ConfigUtility.getModuleConfig("Morality");
        moralManagerList = new HashMap<UUID, MoralPlayer>();
        if (!(Bukkit.getServer().getOnlinePlayers().size()==0)){
            Iterator onlinePlayerIterator = Bukkit.getOnlinePlayers().iterator();
            while(onlinePlayerIterator.hasNext()){
                  Player player = (Player)onlinePlayerIterator.next();
                  moralManagerList.put(player.getUniqueId(),new MoralPlayer(player, 0, MoralPlayer.MoralStanding.NEUTRAL));
                  Logger.sendMessage("Player Morality for " + player.getDisplayName() + "0", Logger.LogType.INFO, "Morality");
                }
            }
        }
    public static void disable(){
        moralManagerList.clear();
    }

    @EventHandler
    public void HeroEvent(RaidFinishEvent event){
        List<Player> players = event.getWinners();
        Iterator playersIterator = players.iterator();
        Logger.sendMessage("Raid Finished", Logger.LogType.INFO,"Morality");
        while(playersIterator.hasNext()){
            Player player = (Player)playersIterator.next();
            Logger.sendMessage(player.getName() + "Participated in raid", Logger.LogType.INFO, "Morality");
            MoralPlayer moralPlayer = moralManagerList.get(player.getUniqueId());
            moralPlayer.addMorality(1000);
            moralityCheck(moralPlayer);
        }
    }

    @EventHandler
    public void BadOmenEvent(RaidTriggerEvent event){
        Player player = event.getPlayer();
        Logger.sendMessage("Raid started by " + player.getName(), Logger.LogType.INFO, "Morality");
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
    public void join(PlayerJoinEvent event){
        Player player = event.getPlayer();
        if(!player.hasPlayedBefore()){
            //addplayer data with 0 values to wherever it ends up stored also
            moralManagerList.put(player.getUniqueId(),new MoralPlayer(player, 0, MoralPlayer.MoralStanding.NEUTRAL));
        } else {
            //load player data from wherever it is stored
            moralManagerList.put(player.getUniqueId(), new MoralPlayer(player, 0, MoralPlayer.MoralStanding.NEUTRAL));
        }
    }

    //This class will check the players morality value against the configured morality bounds
    public static void moralityCheck(MoralPlayer player){
        int evilMax = moralityConfig.getInt("moralitybounds.Evil.upper-Bound");
        int neutralMin = moralityConfig.getInt("moralitybounds.Neutral.low-Bound");
        int neutralMax = moralityConfig.getInt("moralitybounds.Neutral.upper-Bound");
        int goodMin = moralityConfig.getInt("moralitybounds.Good.low-Bound");

        Player bukkitPlayer = player.getPlayer();
        bukkitPlayer.sendMessage("Your morality is " + player.getMorality() + ":" + player.getStanding());
        int morality = player.getMorality();
        if(morality > neutralMin && morality > neutralMax && morality > goodMin){
            player.updateMoralStanding(MoralPlayer.MoralStanding.GOOD);
        }
        if(morality > neutralMin && morality < goodMin){
            player.updateMoralStanding(MoralPlayer.MoralStanding.NEUTRAL);
        }
        if(morality <neutralMin){
            player.updateMoralStanding(MoralPlayer.MoralStanding.EVIL);
        }
    }

}
