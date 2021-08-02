package net.supremesurvival.supremecore.voteUtils.weather;

import net.supremesurvival.supremecore.SupremeCore;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WeatherVoteCommand implements CommandExecutor {
    private SupremeCore plugin;
    private VoteWeather voteWeather;
    public WeatherVoteCommand(SupremeCore plugin, VoteWeather voteWeather) {
        this.plugin = plugin;
        this.voteWeather = voteWeather;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if(commandSender instanceof Player){
            Player player = (Player) commandSender;
            World world = player.getWorld();

            if(!voteWeather.voteUtil.isOverworld(player)) return true;

            if (args.length == 1) {
                if(voteWeather.isVoteActive){
                    if(voteWeather.getYesVote().contains(player.getUniqueId()) || voteWeather.getNoVote().contains(player.getUniqueId())){
                        player.sendMessage(ChatColor.RED + "[WV] " + ChatColor.GRAY + "You have already voted");
                        return true;
                    }

                    if(args[0].equalsIgnoreCase("sun") || args[0].equalsIgnoreCase("storm") || args[0].equalsIgnoreCase("rain")){
                        if(voteWeather.voteUtil.preVoteEnvCheck(args[0], player)) {
                           voteWeather.voteUtil.startVote(player, args[0]);
                        }else {
                            player.sendMessage(ChatColor.RED + "[WV] " + ChatColor.GRAY + "Your vote would have no effect.");
                        }
                    }else if(args[0].equalsIgnoreCase("yes")){
                        player.sendMessage(ChatColor.GREEN + "[WV] " + ChatColor.GRAY + "You have voted Yes");
                        plugin.getServer().broadcastMessage(ChatColor.GREEN + "[WV] " + player.getName() + ChatColor.GRAY + " voted " + ChatColor.GREEN + "Yes");
                        voteWeather.getYesVote().add(player.getUniqueId());
                    }else if(args[0].equalsIgnoreCase("no")){
                        player.sendMessage(ChatColor.RED + "[WV] " + ChatColor.GRAY + "You have voted No");
                        plugin.getServer().broadcastMessage(ChatColor.RED + "[WV] " + player.getName() + ChatColor.GRAY + " voted " + ChatColor.RED + "No");
                        voteWeather.getNoVote().add(player.getUniqueId());
                    }else{
                        player.sendMessage(ChatColor.RED + "[WV] " + ChatColor.GRAY + "Invalid use: /wv <yes/no>");
                    }
                }else{
                    if(args[0].equalsIgnoreCase("sun") || args[0].equalsIgnoreCase("storm") || args[0].equalsIgnoreCase("rain")){
                        if(voteWeather.voteUtil.preVoteEnvCheck(args[0], player)) {
                            voteWeather.voteUtil.startVote(player, args[0]);
                        }else {
                            player.sendMessage(ChatColor.RED + "[WV] " + ChatColor.GRAY + "Your vote would have no effect.");
                        }
                }else{
                        player.sendMessage(ChatColor.RED + "[WV] " + ChatColor.GRAY + "There is no WeatherVote happening right now. Start one with " + ChatColor.GREEN + "/wv <sun/rain/storm>" + ChatColor.GRAY + ".");
                    }
                }
            }else if(args.length == 0){
                player.sendMessage(ChatColor.RED + "[WV] " + ChatColor.GRAY + "Correct usage is: " + ChatColor.GREEN + "/wv <sun/rain/storm>.");
            }
        }
        return true;
    }
}