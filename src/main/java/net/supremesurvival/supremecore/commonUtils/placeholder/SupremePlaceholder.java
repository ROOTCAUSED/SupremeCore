package net.supremesurvival.supremecore.commonUtils.placeholder;

import net.supremesurvival.supremecore.SupremeCore;
import net.supremesurvival.supremecore.commonUtils.Logger;
import net.supremesurvival.supremecore.morality.Morality;
import net.supremesurvival.supremecore.morality.MoralityPlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
//This class will serve as a single place for us to return placeholder responses. Placeholder variables will be loaded into the switch case below.
//Shouldnt really get too large, even if it did i dont think it'd cause issues. If server sets on fire will revisit.
public class SupremePlaceholder {
    public static SupremeCore pl;
    final static String handle = "Supreme Placeholder";
    public static String onRequest(Player player, String string){
        Logger.sendMessage(string, Logger.LogType.INFO, "S-PAPI");
        switch (string) {
            case "morality" -> {
                Logger.sendMessage("returned Morality", Logger.LogType.INFO, handle);
                return String.valueOf(Morality.getMorality(player));
            }
            case "standing" -> {
                Logger.sendMessage("returned Morality", Logger.LogType.INFO, handle);
                String standingString = Morality.getMoralStanding(player);
                standingString = standingString.substring(0, 1).toUpperCase() + standingString.substring(1).toLowerCase();
                return standingString;
            }
            default -> {
                return null;
            }
        }
    }
    public static void register(){
        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI")!= null){
            new MoralityPlaceholderExpansion().register();
        }
    }

    public static void enable(SupremeCore plugin){
        pl = plugin;
        Logger.sendMessage("Enabled", Logger.LogType.INFO, "SupremePAPI");
    }
}
