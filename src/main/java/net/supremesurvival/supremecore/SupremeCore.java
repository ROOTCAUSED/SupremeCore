package net.supremesurvival.supremecore;

import net.supremesurvival.supremecore.commandUtils.SupremeTabs;
import net.supremesurvival.supremecore.mobUtils.HorseInfo;
import net.supremesurvival.supremecore.voteUtils.time.TimeVoteCommand;
import net.supremesurvival.supremecore.voteUtils.time.VoteEvent;
import net.supremesurvival.supremecore.voteUtils.time.VoteTime;
import net.supremesurvival.supremecore.voteUtils.time.VoteUtil;
import net.supremesurvival.supremecore.voteUtils.weather.VoteWeather;
import net.supremesurvival.supremecore.voteUtils.weather.WeatherVoteCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import net.supremesurvival.supremecore.mobUtils.MobLoot;

import java.util.EventListener;

public final class SupremeCore extends JavaPlugin {
    VoteTime voteTime = new VoteTime();
    VoteWeather voteWeather = new VoteWeather();
    @Override
    public void onEnable() {

        // Plugin startup logic
        super.onEnable();
        this.getConfig().options().copyDefaults(true);
        this.saveConfig();
        Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "[SupremeCore] [+] " + ChatColor.GRAY + "Config file loaded.");
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

    }
}
