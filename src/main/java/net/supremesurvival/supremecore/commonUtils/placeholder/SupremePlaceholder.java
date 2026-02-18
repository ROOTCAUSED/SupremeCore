package net.supremesurvival.supremecore.commonUtils.placeholder;

import net.supremesurvival.supremecore.SupremeCore;
import net.supremesurvival.supremecore.commonUtils.Logger;
import net.supremesurvival.supremecore.morality.Morality;
import net.supremesurvival.supremecore.morality.MoralityPlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

// This class serves as a single place for us to return placeholder responses.
// Placeholder variables will be loaded into the switch case below.
public class SupremePlaceholder {
    public static SupremeCore pl;
    final static String handle = "Supreme Placeholder";

    public static String onRequest(Player player, String string) {
        if (player == null || string == null) {
            return null;
        }

        return switch (string) {
            case "morality" -> String.valueOf(Morality.getMorality(player));
            case "standing" -> {
                String standingString = Morality.getMoralStanding(player);
                standingString = standingString.substring(0, 1).toUpperCase() + standingString.substring(1).toLowerCase();
                yield standingString;
            }
            default -> null;
        };
    }

    public static void register() {
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new MoralityPlaceholderExpansion().register();
        }
    }

    public static void enable(SupremeCore plugin) {
        pl = plugin;
        Logger.sendMessage("Enabled", Logger.LogType.INFO, "SupremePAPI");
    }
}
