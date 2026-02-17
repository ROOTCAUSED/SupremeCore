package net.supremesurvival.supremecore.artefacts;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ArtefactsCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        if (args.length == 0) {
            player.sendMessage("/artefacts retrieve");
            return true;
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("retrieve")) {
            if (!player.hasPermission("artefacts.retrieve")) {
                player.sendMessage("You do not have permission to use this command.");
                return true;
            }

            for (Artefact artefact : ArtefactManager.artefacts) {
                player.getInventory().addItem(artefact.getItem());
            }

            player.sendMessage("Retrieved " + ArtefactManager.artefacts.size() + " artefact(s).");
            return true;
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("add")) {
            if (!player.hasPermission("artefacts.add")) {
                player.sendMessage("You do not have permission to use this command.");
                return true;
            }

            if (player.getInventory().getItemInMainHand().getType() == Material.WRITTEN_BOOK) {
                player.sendMessage("Artefact add flow is not implemented yet.");
                return true;
            }

            player.sendMessage("Hold a written book in your main hand.");
            return true;
        }

        player.sendMessage("Usage: /artefacts retrieve");
        return true;
    }
}
