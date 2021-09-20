package net.supremesurvival.supremecore.commonUtils.intervalAnnouncer;

import me.clip.placeholderapi.PlaceholderAPI;
import net.supremesurvival.supremecore.SupremeCore;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IntervalAnnouncer implements Listener {
    protected static Map<String, PlayerMsgTask> sending;
    protected static int rLast;
    protected static int count;
    protected static boolean random;
    protected static List<String> announcements;
    protected static int announceInterval;
    protected static int announcementLength;
    protected static boolean announceOnJoin;
    protected static String joinMessage;
    protected static boolean disableSounds;
    protected static List<String> minechat;
    protected static BukkitTask iTask;
    private ActionCommands commands = new ActionCommands(this);
    private static ActionManager actionManager;
    protected static boolean placeholderAPI;
    SupremeCore plugin;

    public IntervalAnnouncer(SupremeCore plugin){
        this.plugin = plugin;
    }
    public void enable(){
            actionManager = new ActionManager();
            announcements = plugin.announcements;
            placeholderAPI = plugin.placeholderAPI;
            sending = new HashMap();
            minechat = new ArrayList();
            this.loadSettings();
            this.plugin.getCommand("actionannouncer").setExecutor(this.commands);
            this.plugin.getServer().getPluginManager().registerEvents(this,this.plugin);
            if(this.plugin.getConfig().getBoolean("announcer_enabled")){
                this.startAnnouncements();
            }

        }
    public void rello(){
        this.stopAnnouncements();
        if(this.plugin.getConfig().getBoolean("announcer_enabled")){
            this.startAnnouncements();
        }
    }
    private void loadSettings() {
        random = this.plugin.getConfig().getBoolean("announcer_random");
        announcements = this.plugin.getConfig().getStringList("announcements");
        announceInterval = this.plugin.getConfig().getInt("announce_interval");
        announceOnJoin = this.plugin.getConfig().getBoolean("announce_on_join");
        joinMessage = this.plugin.getConfig().getString("join_announcement");
        disableSounds = this.plugin.getConfig().getBoolean("disable_sounds");
        announcementLength = this.plugin.getConfig().getInt("announcement_length");
    }

    protected void startAnnouncements() {
        if (iTask == null) {
            iTask = this.plugin.getServer().getScheduler().runTaskTimerAsynchronously(this.plugin, new IntervalTask(this), 1L, 20L * (long)announceInterval);
            Bukkit.getConsoleSender().sendMessage("started announcements");
        } else {
            iTask.cancel();
            iTask = null;
            iTask = this.plugin.getServer().getScheduler().runTaskTimerAsynchronously(this.plugin, new IntervalTask(this), 1L, 20L * (long)announceInterval);
            Bukkit.getConsoleSender().sendMessage("started announcements");
        }

    }

    protected void stopAnnouncements() {
        if (iTask != null) {
            iTask.cancel();
            iTask = null;
        }

    }

    public static void sendAnnouncement(Player p, String msg) {
        actionManager.sendMessage(p, setPlaceholders(p, msg));
    }

    private static String setPlaceholders(Player p, String s) {
        if (s.contains("%player%")) {
            s = s.replace("%player%", p.getName());
        }

        if (s.contains("%online%")) {
            s = s.replace("%online%", String.valueOf(Bukkit.getServer().getOnlinePlayers().size()));
        }

        return placeholderAPI ? PlaceholderAPI.setPlaceholders(p, s) : s;
    }
}
