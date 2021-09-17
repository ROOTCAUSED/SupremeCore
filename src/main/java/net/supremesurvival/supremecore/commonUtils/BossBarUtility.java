package net.supremesurvival.supremecore.commonUtils;


import net.supremesurvival.supremecore.SupremeCore;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import java.util.Iterator;

public class BossBarUtility {
    private int taskID = -1;
    private final SupremeCore plugin;
    private BossBar bar;

    public BossBarUtility(SupremeCore plugin){
        this.plugin = plugin;
    }

    public void addPlayer(Player player){
        bar.addPlayer(player);
    }

    public BossBar getBar(){
        return bar;
    }

    public void createBar(){
        bar = Bukkit.createBossBar(plugin.chatUtil.formatCC(plugin.announcements.get(0).toString()), BarColor.RED, BarStyle.SOLID, BarFlag.CREATE_FOG);
        bar.setVisible(true);
        cast();
    }

    public void cast(){
        taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            int count = 0;
            double progress = 1.0;
            double time = 1.0/(20);
            int maxbound = plugin.announcements.size();
            @Override
            public void run() {
                    bar.setProgress(progress);
                if (count<maxbound) {
                    bar.setTitle(plugin.chatUtil.formatCC(plugin.chatUtil.setPlaceholders(plugin.announcements.get(count).toString())));
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
    public int getTaskID() {
        return taskID;
    }

    public void setTaskID(int taskID) {
        this.taskID = taskID;
    }
    public void disable(){
        if(Bukkit.getOnlinePlayers().size() > 0)
            for (Player on : Bukkit.getOnlinePlayers())
                getBar().removeAll();
        }
}

