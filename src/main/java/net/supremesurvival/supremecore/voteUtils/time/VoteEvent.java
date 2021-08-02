package net.supremesurvival.supremecore.voteUtils.time;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;

public class VoteEvent implements Listener {

    private VoteTime voteTime;
    public VoteEvent(VoteTime voteTime) {
        this.voteTime = voteTime;
    }

    @EventHandler
    public void sleepVote(PlayerBedEnterEvent event){
        Player player = event.getPlayer();
        World world =player.getWorld();

        if(world.hasStorm()){
            world.setStorm(false);
            player.getServer().broadcastMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "[TV] " + player.getName() + ChatColor.RESET + ""
                    + ChatColor.GRAY + " has slept and reset the weather to clear.");
            return;
        }

        if(!voteTime.isVoteActive){
            if(!voteTime.voteUtil.isOverworld(player)) return;

            voteTime.voteUtil.startVote(player);
        }else{
            if(!voteTime.getYesVote().contains(player.getUniqueId())){
                voteTime.getYesVote().add(player.getUniqueId());
                player.sendMessage(ChatColor.YELLOW + "[TV] " + ChatColor.GRAY + "Because you chose to sleep you are starting a time vote.");
            }
        }
    }
}