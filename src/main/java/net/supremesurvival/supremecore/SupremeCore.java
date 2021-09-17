package net.supremesurvival.supremecore;

import com.palmergames.bukkit.towny.TownyUniverse;
import net.supremesurvival.supremecore.commandUtils.SupremeTabs;
import net.supremesurvival.supremecore.commonUtils.BossBarUtility;
import net.supremesurvival.supremecore.commonUtils.ChatUtil;
import net.supremesurvival.supremecore.commonUtils.ConfigUtility;
import net.supremesurvival.supremecore.commonUtils.intervalAnnouncer.IntervalAnnouncer;
import net.supremesurvival.supremecore.mobUtils.HorseInfo;
import net.supremesurvival.supremecore.voteUtils.time.TimeVoteCommand;
import net.supremesurvival.supremecore.voteUtils.time.VoteEvent;
import net.supremesurvival.supremecore.voteUtils.time.VoteTime;
import net.supremesurvival.supremecore.voteUtils.time.VoteUtil;
import net.supremesurvival.supremecore.voteUtils.weather.VoteWeather;
import net.supremesurvival.supremecore.voteUtils.weather.WeatherVoteCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Boss;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.server.ServerLoadEvent;
import org.bukkit.plugin.java.JavaPlugin;
import net.supremesurvival.supremecore.mobUtils.MobLoot;
import java.util.List;

public final class SupremeCore extends JavaPlugin implements Listener {
    VoteTime voteTime = new VoteTime();
    VoteWeather voteWeather = new VoteWeather();
    BossBarUtility barManager = new BossBarUtility(this);
    public List announcements;
    public ChatUtil chatUtil = new ChatUtil(this);
    public boolean placeholderAPI;
    public boolean townyAdvanced;
    ConfigUtility configUtility = new ConfigUtility(this);
    IntervalAnnouncer intervalAnnouncer = new IntervalAnnouncer(this);
    @Override
    public void onEnable() {
        // Plugin startup logic
        super.onEnable();
        configUtility.initCfg();
        this.initHooks();
        this.initUtils();
        voteTime.enable();
        this.getCommand("TimeVote").setExecutor(new TimeVoteCommand(this, voteTime));
        this.getServer().getPluginManager().registerEvents(new VoteEvent(voteTime), this);
        this.getCommand("TimeVote").setTabCompleter(new SupremeTabs(voteTime.getArgs()));
        voteTime.timeToVote = this.getConfig().getInt("time-to-vote");
        voteTime.voteDelay = this.getConfig().getInt("vote-delay");
        voteTime.voteUtil = new VoteUtil(this, voteTime);
        voteWeather.enable();
        voteWeather.timeToVote = this.getConfig().getInt("time-to-vote");
        voteWeather.voteDelay = this.getConfig().getInt("vote-delay");
        voteWeather.voteUtil = new net.supremesurvival.supremecore.voteUtils.weather.VoteUtil(this, voteWeather);
        this.getCommand("WeatherVote").setExecutor(new WeatherVoteCommand(this, voteWeather));
        this.getCommand("WeatherVote").setTabCompleter(new SupremeTabs(voteWeather.getArgs()));
        Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "[SupremeCore] [+] " + ChatColor.GRAY +"SupremeCore loaded - Shit just got real.");
        this.getServer().getPluginManager().registerEvents(new MobLoot(), this);
        Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "[SupremeCore] [+] " + ChatColor.GRAY +"SupremeNerf Gold Filter Loaded.");
        this.getCommand("HorseInfo").setExecutor(new HorseInfo());

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        voteTime.disable();
        voteWeather.disable();
        barManager.disable();
    }
    @EventHandler
    public void onJoin(PlayerJoinEvent event){

    }
    public void initHooks(){
        placeholderAPI = Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI");
        if(placeholderAPI){
            Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "[SupremeCore] [+] " + ChatColor.GRAY + "PAPI hooked.");
        }
        townyAdvanced = Bukkit.getPluginManager().isPluginEnabled("Towny");
        if(townyAdvanced){
            Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "[SupremeCore] [+] " + ChatColor.GRAY + "Towny hooked.");
        }
    }
    public void initUtils(){
        this.getServer().getPluginManager().registerEvents(this,this);
        intervalAnnouncer.enable();

    }


}
