package net.supremesurvival.supremecore.realestate;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Towny real-estate command entrypoint.
 *
 * NOTE: This is intentionally a thin command shell for PR review so we can align UX
 * and policy before wiring into full Towny listing/teleport behavior.
 */
public class RealEstateCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        if (args.length == 0) {
            sendHelp(player);
            return true;
        }

        if (args.length >= 1 && args[0].equalsIgnoreCase("list")) {
            player.sendMessage(ChatColor.GOLD + "[RealEstate]" + ChatColor.GRAY + " Towny market query hook is staged for implementation.");
            player.sendMessage(ChatColor.GRAY + "Planned: /realestate list [town] [page]");
            return true;
        }

        if (args.length >= 1 && args[0].equalsIgnoreCase("view")) {
            player.sendMessage(ChatColor.GOLD + "[RealEstate]" + ChatColor.GRAY + " Plot viewing teleport hook is staged for implementation.");
            player.sendMessage(ChatColor.GRAY + "Planned: /realestate view <listingId>");
            return true;
        }

        sendHelp(player);
        return true;
    }

    private void sendHelp(Player player) {
        player.sendMessage(ChatColor.YELLOW + "/realestate list [town] [page]");
        player.sendMessage(ChatColor.YELLOW + "/realestate view <listingId>");
    }
}
