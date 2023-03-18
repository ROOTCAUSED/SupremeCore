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
    private LivingEntity PlayerMount;
    private AbstractHorse PlayerHorse;
    private double movementSpeed = 1.0, jumpStrength = 1.0, health = 1.0, maxhealth = 1.0;
    private String name = "";
    private String OwnerName;
    DecimalFormat df = new DecimalFormat("###.###");
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player player) {
            if (player.isInsideVehicle()) {
                if (player.getVehicle() instanceof Horse) {
                    this.PlayerMount = (LivingEntity) player.getVehicle();
                    this.PlayerHorse = (AbstractHorse) PlayerMount;
                    this.name = PlayerHorse.getName();
                    this.movementSpeed = PlayerHorse.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getBaseValue();
                    this.health = PlayerHorse.getHealth();
                    if (PlayerHorse.getOwner() != null){
                        this.OwnerName = PlayerHorse.getOwner().getName();
                    }else{
                        this.OwnerName = "Nobody";
                    }
                    this.maxhealth = PlayerHorse.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue();
                    this.jumpStrength = PlayerHorse.getJumpStrength();
                    PlayerHorse.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
                    player.sendMessage(ChatColor.GRAY + "[" + ChatColor.GOLD + "--------------" + ChatColor.GRAY +"]" + ChatColor.YELLOW + "\nHorse Name: " + ChatColor.GRAY + this.name + ChatColor.YELLOW + "\nJump Strength: " + ChatColor.GRAY + df.format(this.jumpStrength) + ChatColor.YELLOW +  "\nSpeed: " + ChatColor.GRAY + df.format(this.movementSpeed) + ChatColor.YELLOW +  "\nHealth: " + ChatColor.GRAY + df.format(this.health) + "/"+ this.maxhealth + ChatColor.GRAY + ChatColor.YELLOW + "\nTamed by: " + ChatColor.GRAY + this.OwnerName + "\n[" + ChatColor.GOLD + "--------------" + ChatColor.GRAY +"]");
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
