package net.supremesurvival.supremecore.commonUtils;

import me.clip.placeholderapi.PlaceholderAPI;
import net.supremesurvival.supremecore.SupremeCore;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ChatUtil {
    private final SupremeCore plugin;
    private boolean placeholderAPI;
    public ChatUtil(SupremeCore plugin){
        this.plugin = plugin;
        this.placeholderAPI = plugin.placeholderAPI;
    }
    public String formatCC(String msg){
        return ChatColor.translateAlternateColorCodes('&',msg);

    }
    public String setPlaceholders(String s) {
        if (s.contains("%online%")) {
            s = s.replace("%online%", String.valueOf(Bukkit.getServer().getOnlinePlayers().size()));
        }

        return placeholderAPI ? PlaceholderAPI.setPlaceholders(p, s) : s;
    }
}