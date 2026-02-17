package net.supremesurvival.supremecore.tomes;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TomesCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        if (args.length == 0) {
            player.sendMessage("/tomes retrieve");
            return true;
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("retrieve")) {
            if (!player.hasPermission("tomes.retrieve")) {
                player.sendMessage("You do not have permission to use this command.");
                return true;
            }

            for (Tome tome : TomeManager.tomes) {
                player.getInventory().addItem(tome.getItem());
            }

            player.sendMessage("Retrieved " + TomeManager.tomes.size() + " tome(s).");
            return true;
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("add")) {
            if (!player.hasPermission("tomes.add")) {
                player.sendMessage("You do not have permission to use this command.");
                return true;
            }

            if (player.getInventory().getItemInMainHand().getType() == Material.WRITTEN_BOOK) {
                player.sendMessage("Tome add flow is not implemented yet.");
                return true;
            }

            player.sendMessage("Hold a written book in your main hand.");
            return true;
        }

        player.sendMessage("Usage: /tomes retrieve");
        return true;
    }
}
