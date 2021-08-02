package net.supremesurvival.supremecore.commsUtils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class TitleUtility {
    public String title;
    public String subTitle;
    public int fadeIn;
    public int stay;
    public int fadeOut;

    public void sendTitleAll(String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        Bukkit.getOnlinePlayers().forEach(player -> {
            player.sendTitle(title, subtitle, fadeIn, stay, fadeOut);
        });
    }
    public void sendPlayer(String title, String subtitle, int fadeIn, int stay, int fadeOut, Player player){
        player.sendTitle(title, subtitle,fadeIn,stay,fadeOut);
    }
    }
