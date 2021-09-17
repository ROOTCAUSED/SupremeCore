package net.supremesurvival.supremecore.commonUtils;

import net.supremesurvival.supremecore.SupremeCore;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class AnnouncerUtility {
    private int taskID = -1;
    private SupremeCore plugin;
    public AnnouncerUtility(SupremeCore plugin){
        this.plugin = plugin;
    }
    public void announce(){

        taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            int count = 0;
            double progress = 1.0;
            double time = 1.0/(20);
            int maxbound = plugin.announcements.size();
            @Override
            public void run() {
                if (count<maxbound) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        plugin.chatUtil.sendActionbar(plugin.chatUtil.formatCC(plugin.chatUtil.setPlaceholders(plugin.announcements.get(count).toString())), player);
                    }
                }else {
                    count = 0;
                }
                progress = progress - time;
                if(progress <=0) {
                    count++;
                    progress = 1.0;
                }
            }
        },0,20);
    }
}


