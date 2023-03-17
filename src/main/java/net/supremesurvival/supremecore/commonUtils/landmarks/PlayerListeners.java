package net.supremesurvival.supremecore.commonUtils.landmarks;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import net.citizensnpcs.api.CitizensAPI;
import net.supremesurvival.supremecore.commonUtils.Logger;
import net.supremesurvival.supremecore.commonUtils.TitleUtility;
import net.supremesurvival.supremecore.commonUtils.tomes.Tome;
import net.supremesurvival.supremecore.commonUtils.tomes.TomeManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import static net.supremesurvival.supremecore.commonUtils.landmarks.LandmarkManager.landmarkList;

public class PlayerListeners implements Listener {
    private static HashMap<UUID, Landmark> playersInLandmarks = new HashMap<>();
    public static HashMap<UUID, List <String>> landmarksDiscovered = new HashMap<>();

    @EventHandler
    public void join(PlayerJoinEvent event){
        //need to check if player is in list, if not insert as below
        //will optimise this to only load player data on join not load all data from beginning
        Logger.sendMessage("Placed " + event.getPlayer().getName().toString() + " into Landmarks Discovered List", Logger.LogType.INFO, "[Landmark Manager]" );
        if(!landmarksDiscovered.containsKey(event.getPlayer().getUniqueId())){landmarksDiscovered.put(event.getPlayer().getUniqueId(),new ArrayList<>());}
    }
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event){
        //ignore citizens npc's
        if(Bukkit.getPluginManager().getPlugin("Citizens").isEnabled() && CitizensAPI.getNPCRegistry().isNPC(event.getPlayer())){
            return;
        }
        //as event fires even when player looks around, we'll cancel if they dont actually move
        if(event.getFrom().getBlockX() == event.getTo().getBlockX() && event.getFrom().getBlockZ() == event.getTo().getBlockZ() && event.getFrom().getBlockY() == event.getTo().getBlockY()){
            return;
        }
        //if not yet cancelled we can start instantiating shit, i hope. Main concern is once server is heavily populated this event will be called A LOT. Need to keep it minimal.
        //returning as soon as we realise we neednt compute any further.
        Player player = event.getPlayer();
        Location to = event.getTo();
        if(!playersInLandmarks.isEmpty() && isPlayerInLandmark(player)){
            Landmark landmark = getCurrentLandmark(player);
            if (!landmark.getRegion().contains(to.getBlockX(),to.getBlockY(),to.getBlockZ())){
                player.sendMessage("you have exited a landmark");
                TitleUtility.sendPlayer("You have exited " + landmark.getTitle(), "Substring", 5, 20,5, player);
                playersInLandmarks.remove(player.getUniqueId());
            }
            return;
        };
        //we can look into caching these values to reduce compute requirements of this block.
        Location from = event.getFrom();
        //we're now going to take their location data and check if any of the regions in our landmark manager contain either their to or from positions.
        //If they do then we know a player is exiting or entering a landmark region.
        Iterator landmarkIterator = landmarkList.iterator();
        while(landmarkIterator.hasNext()){
            Landmark landmark = (Landmark)landmarkIterator.next();
            if(landmark.getRegion().contains(to.getBlockX(),to.getBlockY(),to.getBlockZ())){
                if(!hasDiscovered(player, landmark.getID())){
                    discoverLandmark(player,landmark);
                }
                player.sendMessage("You have entered a landmark");
                playersInLandmarks.put(player.getUniqueId(),landmark);

        }
        }
    }

    public boolean isPlayerInLandmark (Player player){
        if(playersInLandmarks.containsKey(player.getUniqueId())){
            return true;
        }
        return false;
    }
    public Landmark getCurrentLandmark (Player player){
        return playersInLandmarks.get(player.getUniqueId());
    }
    public void discoverLandmark(Player player, Landmark landmark){
        List<String> landmarksPlayer = landmarksDiscovered.getOrDefault(player.getUniqueId(), new ArrayList<>());
        if(!landmarksPlayer.contains(landmark.getID())){
            landmarksPlayer.add(landmark.getID());
            landmarksDiscovered.put(player.getUniqueId(),landmarksPlayer);
            player.sendMessage("You have discovered " + landmark.getTitle());
        };

    }

    public boolean hasDiscovered(Player player, String landmarkID){
        if(landmarksDiscovered.get(player.getUniqueId()).contains(landmarkID)){
            return true;
        }
        return false;
    }
}
