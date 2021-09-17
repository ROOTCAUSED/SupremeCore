package net.supremesurvival.supremecore.commonUtils.intervalAnnouncer;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ActionManager {
    public void sendMessage(Player player, String msg){
        msg = chatcc(msg);
        player.sendActionBar(msg);
        Bukkit.getServer().getConsoleSender().sendMessage(msg);
    }
    public String chatcc(String msg){
        return(ChatColor.translateAlternateColorCodes('&',msg));
    }

}
