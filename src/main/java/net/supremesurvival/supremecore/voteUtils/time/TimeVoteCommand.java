package net.supremesurvival.supremecore.voteUtils.time;
import net.supremesurvival.supremecore.SupremeCore;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TimeVoteCommand implements CommandExecutor {
    private SupremeCore plugin;
    private VoteTime voteTime;
    public TimeVoteCommand(SupremeCore plugin, VoteTime voteTime) {
        this.plugin = plugin;
        this.voteTime = voteTime;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if(commandSender instanceof Player){
            Player player = (Player) commandSender;
            World world = player.getWorld();

            if(!voteTime.voteUtil.isOverworld(player)) return true;

            if (args.length == 1) {
                if(voteTime.getActiveVote(world)){
                    if(voteTime.getYesVotes(world).contains(player.getUniqueId()) || voteTime.getNoVotes(world).contains(player.getUniqueId())){
                        player.sendMessage(ChatColor.RED + "[TV] " + ChatColor.GRAY + "You have already voted");
                        return true;
                    }

                    if(args[0].equalsIgnoreCase("yes")){
                        player.sendMessage(ChatColor.GREEN + "[TV] " + ChatColor.GRAY + "You have voted Yes");
                        plugin.getServer().broadcastMessage(ChatColor.GREEN + "[TV] " + player.getName() + ChatColor.GRAY + " voted " + ChatColor.GREEN + "Yes");
                        voteTime.getYesVotes(world).add(player.getUniqueId());
                    }else if(args[0].equalsIgnoreCase("no")){
                        player.sendMessage(ChatColor.RED + "[TV] " + ChatColor.GRAY + "You have voted No");
                        plugin.getServer().broadcastMessage(ChatColor.RED + "[TV] " + player.getName() + ChatColor.GRAY + " voted " + ChatColor.RED + "No");
                        voteTime.getNoVotes(world).add(player.getUniqueId());
                    }else{
                        player.sendMessage(ChatColor.RED + "[TV] " + ChatColor.GRAY + "Invalid use: /timevote <yes/no>");
                    }
                }else{
                    player.sendMessage(ChatColor.RED + "[TV] " + ChatColor.GRAY + "There is no active vote.");
                }
            }else if(args.length == 0){
                if(!voteTime.getActiveVote(world)){
                    voteTime.voteUtil.startVote(player);
                }
            }
        }
        return true;
    }
}