package net.supremesurvival.supremecore.commonUtils.placeholder;

import net.supremesurvival.supremecore.SupremeCore;
import net.supremesurvival.supremecore.commonUtils.Logger;
import net.supremesurvival.supremecore.commonUtils.morality.Morality;
import net.supremesurvival.supremecore.commonUtils.morality.MoralityPlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class SupremePlaceholder {
    public static SupremeCore pl;

    public static String onRequest(Player player, String string){
        Logger.sendMessage(string, Logger.LogType.INFO, "S-PAPI");
        switch(string){
            case "morality":
                Logger.sendMessage("returned Morality", Logger.LogType.INFO, "SupremePAPI");
                return String.valueOf(Morality.getMorality(player));
            case "standing":
                Logger.sendMessage("returned Morality", Logger.LogType.INFO, "SupremePAPI");
                return Morality.getMoralStanding(player);
            default:
                return null;
        }
    }
    public static void register(String handle){
        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI")!= null){
            new MoralityPlaceholderExpansion(pl).register();
        }
    }

    public static void enable(SupremeCore plugin){
        pl = plugin;
        Logger.sendMessage("Enabled", Logger.LogType.INFO, "SupremePAPI");
    }
}
