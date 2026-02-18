package net.supremesurvival.supremecore.landmarks;

import com.sk89q.worldedit.math.BlockVector3;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

import static net.supremesurvival.supremecore.landmarks.PlayerListeners.landmarksDiscovered;

public class LandmarkCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        if (args.length == 0) {
            player.sendMessage("/landmarks list");
            player.sendMessage("/landmarks discovered");
            return true;
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("list")) {
            for (Landmark landmark : LandmarkManager.landmarkList) {
                player.sendMessage(ChatColor.YELLOW + landmark.getTitle() + ChatColor.GRAY + " [" + landmark.getType() + "]");
            }
            return true;
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("discovered")) {
            List<String> discovered = landmarksDiscovered.getOrDefault(player.getUniqueId(), Collections.emptyList());
            if (discovered.isEmpty()) {
                player.sendMessage(ChatColor.GRAY + "You have not discovered any landmarks yet.");
                return true;
            }

            player.sendMessage(ChatColor.GOLD + "=== Discovered Landmarks Journal ===");
            for (String id : discovered) {
                Landmark landmark = LandmarkManager.getLandmarkById(id);
                if (landmark == null) {
                    continue;
                }
                BlockVector3 c = landmark.getCenter();
                player.sendMessage(
                        ChatColor.YELLOW + landmark.getTitle()
                                + ChatColor.DARK_GRAY + " • "
                                + ChatColor.GRAY + landmark.getType()
                                + ChatColor.DARK_GRAY + " • "
                                + ChatColor.AQUA + landmark.getWorldName()
                                + ChatColor.DARK_GRAY + " @ "
                                + ChatColor.WHITE + c.getBlockX() + ", " + c.getBlockY() + ", " + c.getBlockZ()
                );
            }
            return true;
        }

        player.sendMessage("Usage: /landmarks <list|discovered>");
        return true;
    }
}
