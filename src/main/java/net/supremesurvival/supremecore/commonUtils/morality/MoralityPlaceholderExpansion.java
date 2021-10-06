package net.supremesurvival.supremecore.commonUtils.morality;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.supremesurvival.supremecore.SupremeCore;
import net.supremesurvival.supremecore.commonUtils.Logger;
import net.supremesurvival.supremecore.commonUtils.placeholder.SupremePlaceholder;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
//This class serves as a placeholder expansion for the morality system, providing placeholders ingame for player morality and standing values.
public class MoralityPlaceholderExpansion extends PlaceholderExpansion {
    private final SupremeCore plugin;
    public MoralityPlaceholderExpansion(SupremeCore plugin){
        this.plugin = plugin;
    }
    @Override
    public String getIdentifier() {
        return "SCMorality";
    }

    @Override
    public String getAuthor(){
        return "xMachiavellix";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    public boolean persist() {
        return true; // This is required or else PlaceholderAPI will unregister the Expansion on reload
    }

    @Override
    public boolean canRegister(){
        return true;
    }

    @Override
    //calls to SupremePlaceholder which will manager overall placeholders provided by this api.
    public String onPlaceholderRequest(Player player, String placeholder){
        return SupremePlaceholder.onRequest(player, placeholder);
    }
}
