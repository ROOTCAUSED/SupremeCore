package net.supremesurvival.supremecore.commonUtils.intervalAnnouncer;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ActionManager {
    public void sendMessage(Player player, String msg){
        msg = chatcc(msg);
        player.sendActionBar(msg);
    }
    public String chatcc(String msg){
        return(ChatColor.translateAlternateColorCodes('&',msg));
    }

}
