package net.supremesurvival.supremecore.mobUtils;
import net.supremesurvival.supremecore.commonUtils.Logger;
import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;
import java.text.DecimalFormat;
//This is a crude class to return horse info to a player who runs the command from horseback.
//At some point this will be expanded with features reverse engineered from the RPGHorses library, however for now it serves its purpose.
public class HorseInfo implements CommandExecutor {
    final DecimalFormat df = new DecimalFormat("###.###");
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player player) {
            if (player.isInsideVehicle()) {
                if (player.getVehicle() instanceof Horse) {
                    LivingEntity playerMount = (LivingEntity) player.getVehicle();
                    AbstractHorse playerHorse = (AbstractHorse) playerMount;
                    String name = playerHorse.getName();
                    double movementSpeed = playerHorse.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getBaseValue();
                    double health = playerHorse.getHealth();
                    String ownerName;
                    if (playerHorse.getOwner() != null){
                        ownerName = playerHorse.getOwner().getName();
                    }else{
                        ownerName = "Nobody";
                    }
                    double maxhealth = playerHorse.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue();
                    double jumpStrength = playerHorse.getJumpStrength();
                    playerHorse.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
                    player.sendMessage(ChatColor.GRAY + "[" + ChatColor.GOLD + "--------------" + ChatColor.GRAY +"]" + ChatColor.YELLOW + "\nHorse Name: " + ChatColor.GRAY + name + ChatColor.YELLOW + "\nJump Strength: " + ChatColor.GRAY + df.format(jumpStrength) + ChatColor.YELLOW +  "\nSpeed: " + ChatColor.GRAY + df.format(movementSpeed) + ChatColor.YELLOW +  "\nHealth: " + ChatColor.GRAY + df.format(health) + "/"+ maxhealth + ChatColor.GRAY + ChatColor.YELLOW + "\nTamed by: " + ChatColor.GRAY + ownerName + "\n[" + ChatColor.GOLD + "--------------" + ChatColor.GRAY +"]");
                    return true;
                }else {
                    player.sendMessage(ChatColor.GOLD + "[SupremeCore]" + ChatColor.GRAY + ":" + ChatColor.RED + "You must be riding a Horse to use this command.");
                    return true;
                }
            } else {
                player.sendMessage(ChatColor.GOLD + "[SupremeCore]" + ChatColor.GRAY + ":" + ChatColor.RED + "You must be riding a Horse to use this command.");
                return true;
            }
        }
        Logger.sendMessage("Horseinfo can only be run by a player on horseback.", Logger.LogType.INFO, "HorseInfo");
        return true;
    }
}
