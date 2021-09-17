package net.supremesurvival.supremecore.voteUtils.time;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.supremesurvival.supremecore.SupremeCore;
import net.supremesurvival.supremecore.commonUtils.TitleUtility;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

public class VoteUtil {
    private BukkitTask timer;
    private BossBar bossBar;
    private VoteTime voteTime;
    private SupremeCore plugin;
    TitleUtility titleUtil = new TitleUtility();

    public VoteUtil(SupremeCore plugin, VoteTime voteTime) {
        this.plugin = plugin;
        this.voteTime = voteTime;

    }

    public void startVote(Player player){
        World world = player.getWorld();
        if(!isOverworld(player)) return;
        double timeElapsed = 0;
            if(voteTime.getLastVote(world) != null) timeElapsed = Duration.between(voteTime.getLastVote(world), Instant.now()).toMinutes();
                if(voteTime.getLastVote(world) == null || timeElapsed >= voteTime.voteDelay){
                    voteTime.setActiveVote(world, true);
                    if(voteTime.getLastVote(world) == null){
                        voteTime.setLastVote(world, Instant.now());
                    }
        TextComponent yes = new TextComponent("Yes");
        yes.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,"/tv yes"));
        yes.setColor(ChatColor.GREEN);

        TextComponent no = new TextComponent("No");
        no.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,"/tv no"));
        no.setColor(ChatColor.RED);
        //will refactor this to use the titleutility rather than the getServer Broadcast
        //broadcast will also send to players in other worlds which is unwanted behaviour.
        //actually broadcast can still be used, we will likely just have to implement a temp permissable to only send the broadcast to players in voteworld.
        plugin.getServer().broadcastMessage(ChatColor.YELLOW + "[TV] " + ChatColor.GOLD + player.getName() + ChatColor.GRAY + " has started a time vote. " +
                "You have " + ChatColor.GOLD + voteTime.timeToVote + ChatColor.GRAY + " seconds to vote. " +
                "You may click yes/no OR type /timevote <yes/no> OR sleep in bed to vote yes. ");
        plugin.getServer().broadcast(new ComponentBuilder().append(yes).append(" / ").color(ChatColor.GRAY).append(no).create());

        voteTime.getYesVotes(world).add(player.getUniqueId());

        titleUtil.sendPlayer("&bVote &eStarted","You started a time vote",0,100,0, player);
        player.sendMessage(ChatColor.YELLOW + "[TV] " + ChatColor.GRAY + "You automatically cast a Yes vote by starting the vote.");

        createBossBar();

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if(voteTime.getYesVotes(world).size() > voteTime.getNoVotes(world).size()){
                if(world.getTime()>=12600){
                    world.setTime(0);
                    plugin.getServer().broadcastMessage(ChatColor.GREEN + "[TV] " + ChatColor.GREEN + "Vote Success: "
                            + ChatColor.GRAY + "Server time has been voted to day.");
                } else {
                    world.setTime(12600);
                    plugin.getServer().broadcastMessage(ChatColor.GREEN + "[TV] " + ChatColor.GREEN + "Vote Success: "
                            + ChatColor.GRAY + "Server time has been voted to night.");
                }
            } else {
                plugin.getServer().broadcastMessage(ChatColor.RED + "[TV] " + ChatColor.RED + "Vote Failed: "
                        + ChatColor.GRAY + "Server time will elapse naturally.");
            }

            voteTime.setActiveVote(world, false);
            voteTime.getYesVotes(world).clear();
            voteTime.getNoVotes(world).clear();
            timer.cancel();
            bossBar.removeAll();

        }, voteTime.timeToVote * 20);
            } else {
        player.sendMessage(ChatColor.RED + "[TV] " + ChatColor.GRAY + "It's too soon to start another time vote.");
        }
    }

    private void createBossBar() {
        bossBar = Bukkit.createBossBar("Vote Timer:", BarColor.BLUE, BarStyle.SOLID);
        bossBar.setProgress(1);
        Bukkit.getOnlinePlayers().forEach(player -> {
            bossBar.addPlayer(player);
        });

        timer = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            float increment = (float) 1 / voteTime.timeToVote;
            double newProgress = bossBar.getProgress() - increment;
            if (newProgress <= 0) {
                bossBar.setProgress(0);
                return;
            }

            bossBar.setProgress(newProgress);

        }, 0, 20);
    }

    public boolean isOverworld(Player player) {
        World world = player.getWorld();

        if (world.getEnvironment() != World.Environment.NORMAL) {
            player.sendMessage(org.bukkit.ChatColor.RED + "[TV] " + org.bukkit.ChatColor.GRAY + "Timevote can only be done in the overworld.");
            return false;
        }
        return true;

    }
}