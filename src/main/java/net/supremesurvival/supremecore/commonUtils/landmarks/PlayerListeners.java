package net.supremesurvival.supremecore.commonUtils.landmarks;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import net.citizensnpcs.api.CitizensAPI;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerListeners {
    
    public void getWorldGuard(){

    }

    public void onPlayerMove(PlayerMoveEvent event){
        //ignore citizens npc's
        if(Bukkit.getPluginManager().getPlugin("Citizens2").isEnabled() && CitizensAPI.getNPCRegistry().isNPC(event.getPlayer())){
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
        //we can look into caching these values to reduce compute requirements of this block.
        Location from = event.getFrom();
        //we're now going to take their location data and check if any of the regions in our landmark manager contain either their to or from positions.
        //If they do then we know a player is exiting or entering a landmark region.

    }
}
