package net.supremesurvival.supremecore.commonUtils.intervalAnnouncer;

import me.clip.placeholderapi.PlaceholderAPI;
import net.supremesurvival.supremecore.SupremeCore;
import net.supremesurvival.supremecore.commonUtils.fileHandler.ConfigUtility;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
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
    private final ActionCommands commands = new ActionCommands(this);
    private static ActionManager actionManager;
    protected static boolean placeholderAPI;
    SupremeCore plugin;
    private static FileConfiguration cfg;

    public IntervalAnnouncer(SupremeCore plugin){
        this.plugin = plugin;
    }
    public void enable(){
            cfg = ConfigUtility.getModuleConfig("Announcer");
            actionManager = new ActionManager();
            placeholderAPI = plugin.placeholderAPI;
            sending = new HashMap();
            ///Minechat is an android ios app that allows users to chat by logging into the game, they do not have a display and cant be served action bar announcements. Can look into a playermsg alternative, but is it worth it? nah.
            minechat = new ArrayList();
            this.loadSettings();
            this.plugin.getCommand("Announcer").setExecutor(this.commands);
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

        random = cfg.getBoolean("announcer_random");
        announcements = cfg.getStringList("announcements");
        announceInterval = cfg.getInt("announce_interval");
        announceOnJoin = cfg.getBoolean("announce_on_join");
        joinMessage = cfg.getString("join_announcement");
        disableSounds = cfg.getBoolean("disable_sounds");
        announcementLength = cfg.getInt("announcement_length");
    }

    protected void startAnnouncements() {
        if (iTask != null) {
            iTask.cancel();
            iTask = null;
        }
        iTask = this.plugin.getServer().getScheduler().runTaskTimerAsynchronously(this.plugin, new IntervalTask(this), 1L, 20L * (long)announceInterval);
        Bukkit.getConsoleSender().sendMessage("started announcements");

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
