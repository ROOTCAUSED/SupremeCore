package net.supremesurvival.supremecore.voteUtils.weather;
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

public class VoteUtil {
    private BukkitTask timer;
    private BossBar bossBar;
    private VoteWeather voteWeather;
    private SupremeCore plugin;
    private String voteType;
    public VoteUtil(SupremeCore plugin, VoteWeather voteWeather) {
        this.plugin = plugin;
        this.voteWeather = voteWeather;

    }

    public void startVote(Player player, String type){
        this.voteType = type;
        World world = player.getWorld();
        TitleUtility titleUtility = new TitleUtility();
        if(!isOverworld(player)) return;
        double timeElapsed = 0;
        if(voteWeather.lastVote != null) timeElapsed = Duration.between(voteWeather.lastVote, Instant.now()).toMinutes();
        if(voteWeather.lastVote == null || timeElapsed >= voteWeather.voteDelay){
            voteWeather.isVoteActive = true;
            if(voteWeather.lastVote == null) {
                voteWeather.lastVote = Instant.now();
            }
            TextComponent yes = new TextComponent("Yes");
            yes.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,"/wv yes"));
            yes.setColor(ChatColor.GREEN);

            TextComponent no = new TextComponent("No");
            no.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,"/wv no"));
            no.setColor(ChatColor.RED);
            for(Player p : world.getPlayers()){
                p.sendMessage(ChatColor.YELLOW + "[WV] " + ChatColor.GOLD + player.getName() + ChatColor.GRAY + " has started a weather vote for " + ChatColor.GOLD + type +
                        ChatColor.GRAY + " you have" + ChatColor.GOLD + voteWeather.timeToVote + ChatColor.GRAY + " seconds to vote. " +
                        "You may click yes/no OR type /wv <yes/no>.");
                p.sendMessage(new ComponentBuilder().append(yes).append(" / ").color(ChatColor.GRAY).append(no).create());
            }
            //plugin.getServer().broadcastMessage(ChatColor.YELLOW + "[WV] " + ChatColor.GOLD + player.getName() + ChatColor.GRAY + " has started a weather vote for " + ChatColor.GOLD + type +
            //        ChatColor.GRAY + " you have" + ChatColor.GOLD + voteWeather.timeToVote + ChatColor.GRAY + " seconds to vote. " +
            //        "You may click yes/no OR type /wv <yes/no>.");
            //plugin.getServer().broadcast(new ComponentBuilder().append(yes).append(" / ").color(ChatColor.GRAY).append(no).create());

            voteWeather.getYesVote().add(player.getUniqueId());

            player.sendMessage(ChatColor.YELLOW + "[WV] " + ChatColor.GRAY + "You automatically cast a Yes vote by starting the vote.");

            createBossBar();

            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if(voteWeather.getYesVote().size() > voteWeather.getNoVote().size()){
                    if(type.equalsIgnoreCase("sun")){
                        world.setStorm(false);
                        if (world.isThundering()) {
                            world.setThundering(false);
                        }
                        plugin.getServer().broadcastMessage(ChatColor.GREEN + "[WV] " + ChatColor.GREEN + "Vote Success: "
                                + ChatColor.GRAY + "The skies are starting to clear.");
                    } else if(type.equalsIgnoreCase("storm")){
                        world.setStorm(true);
                        world.setThundering(true);
                        plugin.getServer().broadcastMessage(ChatColor.GREEN + "[WV] " + ChatColor.GREEN + "Vote Success: "
                                + ChatColor.GRAY + "Dark clouds are forming in the sky, we're in for a big one!");
                    } else if(type.equalsIgnoreCase("rain")){
                        world.setStorm(true);
                        if(world.isThundering()){
                            world.setThundering(false);
                        }
                        plugin.getServer().broadcastMessage(ChatColor.GREEN + "[WV] " + ChatColor.GREEN + "Vote Success: "
                                + ChatColor.GRAY + "Clouds are forming in the sky, expect a light drizzling.");
                    }
                } else {
                    plugin.getServer().broadcastMessage(ChatColor.RED + "[WV] " + ChatColor.RED + "Vote Failed: "
                            + ChatColor.GRAY + "Server weather shall remain unchanged.");
                }

                voteWeather.isVoteActive = false;
                voteWeather.getYesVote().clear();
                voteWeather.getNoVote().clear();
                timer.cancel();
                bossBar.removeAll();
                titleUtility.sendTitleAll(ChatColor.AQUA + "WeatherVoteWeatherVoteWeatherVoteWeatherVoteWeatherVoteWeatherVoteWeatherVoteWeatherVoteWeatherVoteWeatherVoteWeatherVoteWeatherVoteWeatherVoteWeatherVoteWeatherVote","Weather Changed", 1, 60,1);

            }, voteWeather.timeToVote * 20);
        } else {
            player.sendMessage(ChatColor.RED + "[WV] " + ChatColor.GRAY + "It's too soon to start another time vote.");
        }
    }

    private void createBossBar() {
        bossBar = Bukkit.createBossBar("Weather Vote:", BarColor.BLUE, BarStyle.SEGMENTED_20);
        bossBar.setProgress(1);
        Bukkit.getOnlinePlayers().forEach(player -> {
            bossBar.addPlayer(player);
        });

        timer = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            float increment = (float) 1 / voteWeather.timeToVote;
            double newProgress = bossBar.getProgress() - increment;
            if (newProgress <= 0) {
                bossBar.setProgress(0);
                return;
            }

            bossBar.setProgress(newProgress);

        }, 0, 20);
    }

    //This function returns true if the player is in the overworld, or false if in another world.
    public boolean isOverworld(Player player) {
        World world = player.getWorld();

        if (world.getEnvironment() != World.Environment.NORMAL) {
            player.sendMessage(org.bukkit.ChatColor.RED + "[WV] " + org.bukkit.ChatColor.GRAY + "WeatherVote can only be done in the overworld.");
            return false;
        }
        return true;

    }
    //This function checks the weather in the world the player is in. If the current weather state is no different to
    //the voted for weather state then this function returns false, preventing the weather vote from starting
    //not that it'd do any harm, just would seem pointless.
    public boolean preVoteEnvCheck(String arg, Player player){
        World world = player.getWorld();
        Boolean raining = world.hasStorm();
        Boolean thundering = world.isThundering();
        Boolean sunny = world.isClearWeather();
        player.sendMessage(arg);
        if(arg.equalsIgnoreCase("sun")) {
            if (sunny) {
                player.sendMessage("false");
                return false;
            } else {
                return true;
            }
        } else if(arg.equalsIgnoreCase("storm")){
            if(thundering & raining){
                return false;
            }else{
                return true;
            }
        } else if(arg.equalsIgnoreCase("rain")){
            if(raining & !thundering){
                return false;
            }else{
                return true;
            }
        }else{
            return true;
        }
    }
}