package net.supremesurvival.supremecore.landmarks;

import com.sk89q.worldedit.math.BlockVector3;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.*;

import static net.supremesurvival.supremecore.landmarks.PlayerListeners.landmarksDiscovered;
import static net.supremesurvival.supremecore.landmarks.PlayerListeners.landmarksDiscoveredAt;

public class LandmarkCommand implements CommandExecutor {
    private static final int PAGE_SIZE = 6;

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

        if (args.length == 1 && args[0].equalsIgnoreCase("list")) {
            for (Landmark landmark : LandmarkManager.landmarkList) {
                player.sendMessage(ChatColor.YELLOW + landmark.getTitle() + ChatColor.GRAY + " [" + landmark.getType() + "]");
            }
            return true;
        }

        if (args.length >= 1 && args[0].equalsIgnoreCase("discovered")) {
            List<String> discovered = landmarksDiscovered.getOrDefault(player.getUniqueId(), Collections.emptyList());
            if (discovered.isEmpty()) {
                player.sendMessage(ChatColor.GRAY + "You have not discovered any landmarks yet.");
                return true;
            }

            int page = 1;
            if (args.length >= 2) {
                try {
                    page = Math.max(1, Integer.parseInt(args[1]));
                } catch (NumberFormatException ignored) {
                    page = 1;
                }
            }

            int totalPages = (int) Math.ceil((double) discovered.size() / PAGE_SIZE);
            page = Math.min(page, Math.max(totalPages, 1));

            int start = (page - 1) * PAGE_SIZE;
            int end = Math.min(start + PAGE_SIZE, discovered.size());

            player.sendMessage(ChatColor.GOLD + "=== Discovered Landmarks Journal (" + page + "/" + totalPages + ") ===");
            Map<String, Long> discoveredAtMap = landmarksDiscoveredAt.getOrDefault(player.getUniqueId(), Collections.emptyMap());

            for (int i = start; i < end; i++) {
                String id = discovered.get(i);
                Landmark landmark = LandmarkManager.getLandmarkById(id);
                if (landmark == null) {
                    continue;
                }
                BlockVector3 c = landmark.getCenter();
                long ts = discoveredAtMap.getOrDefault(id, 0L);
                String discoveredAtTxt = ts > 0 ? formatTimestamp(ts) : "unknown";

                player.sendMessage(
                        ChatColor.YELLOW + landmark.getTitle()
                                + ChatColor.DARK_GRAY + " • "
                                + ChatColor.GRAY + landmark.getType()
                                + ChatColor.DARK_GRAY + " • "
                                + ChatColor.AQUA + landmark.getWorldName()
                                + ChatColor.DARK_GRAY + " @ "
                                + ChatColor.WHITE + c.getBlockX() + ", " + c.getBlockY() + ", " + c.getBlockZ()
                                + ChatColor.DARK_GRAY + " • "
                                + ChatColor.LIGHT_PURPLE + discoveredAtTxt
                );
            }

            if (totalPages > 1) {
                player.sendMessage(ChatColor.GRAY + "Use /landmarks discovered <page> to view more.");
            }
            return true;
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("nearest")) {
            Landmark nearest = getNearestLandmark(player.getLocation(), player.getWorld().getName());
            if (nearest == null) {
                player.sendMessage(ChatColor.GRAY + "No landmarks found in this world.");
                return true;
            }

            double distance = player.getLocation().distance(toLocation(player, nearest.getCenter()));
            BlockVector3 c = nearest.getCenter();
            player.sendMessage(
                    ChatColor.GOLD + "Nearest landmark: " + ChatColor.YELLOW + nearest.getTitle()
                            + ChatColor.DARK_GRAY + " • " + ChatColor.GRAY + nearest.getType()
                            + ChatColor.DARK_GRAY + " • " + ChatColor.AQUA + String.format(Locale.US, "%.1f", distance) + "m"
                            + ChatColor.DARK_GRAY + " @ " + ChatColor.WHITE + c.getBlockX() + ", " + c.getBlockY() + ", " + c.getBlockZ()
            );
            return true;
        }

        player.sendMessage("Usage: /landmarks <list|discovered [page]|nearest>");
        return true;
    }

    private void sendHelp(Player player) {
        player.sendMessage("/landmarks list");
        player.sendMessage("/landmarks discovered [page]");
        player.sendMessage("/landmarks nearest");
    }

    private Landmark getNearestLandmark(Location from, String worldName) {
        Landmark nearest = null;
        double best = Double.MAX_VALUE;
        for (Landmark landmark : LandmarkManager.landmarkList) {
            if (!landmark.getWorldName().equalsIgnoreCase(worldName)) continue;
            double dist = from.distance(toLocation(from.getWorld().getName(), landmark.getCenter(), from.getWorld().getName()));
            if (dist < best) {
                best = dist;
                nearest = landmark;
            }
        }
        return nearest;
    }

    private Location toLocation(Player player, BlockVector3 vec) {
        return new Location(player.getWorld(), vec.getBlockX(), vec.getBlockY(), vec.getBlockZ());
    }

    private Location toLocation(String ignored, BlockVector3 vec, String worldName) {
        return new Location(Objects.requireNonNull(org.bukkit.Bukkit.getWorld(worldName)), vec.getBlockX(), vec.getBlockY(), vec.getBlockZ());
    }

    private String formatTimestamp(long ts) {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return fmt.format(new Date(ts));
    }
}
