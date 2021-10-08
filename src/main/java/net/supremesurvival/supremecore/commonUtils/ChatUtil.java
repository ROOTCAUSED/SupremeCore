package net.supremesurvival.supremecore.commonUtils;

import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.supremesurvival.supremecore.SupremeCore;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ChatUtil {
    private boolean placeholderAPI;
    public ChatUtil(SupremeCore plugin){
        this.placeholderAPI = plugin.placeholderAPI;
    }
    public String formatCC(String msg){
        return ChatColor.translateAlternateColorCodes('&',msg);
    }

    public void sendActionbar(String msg, Player player){
        player.sendActionBar(msg);
    }

    public String setPlaceholders(String s) {
        if (s.contains("%online%")) {
            s = s.replace("%online%", String.valueOf(Bukkit.getServer().getOnlinePlayers().size()));
        }

        return s;
    }
}