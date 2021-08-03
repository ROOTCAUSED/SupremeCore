//This class provides a single interface to the bukkit on screen titles feature and will be accessed via any other class that needs to send a title.

package net.supremesurvival.supremecore.commonUtils;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class TitleUtility {
    public String title;
    public String subTitle;
    public int fadeIn;
    public int stay;
    public int fadeOut;
    //Displays a title to all players on current server
    public void sendTitleAll(String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        Bukkit.getOnlinePlayers().forEach(player -> {
            player.sendTitle(title, subtitle, fadeIn, stay, fadeOut);
        });
    }
    //Displays a title to all players on a given world of the current server
    public void sendToWorld(String title, String subtitle, int fadeIn, int stay, int fadeOut, World world){
        world.getPlayers().forEach(player -> {
            player.sendTitle(title, subtitle,fadeIn,stay,fadeOut);
        });
    }
    //Displays a title to a specified player on the current server.
    public void sendPlayer(String title, String subtitle, int fadeIn, int stay, int fadeOut, Player player){
        player.sendTitle(title, subtitle,fadeIn,stay,fadeOut);
    }
    }
