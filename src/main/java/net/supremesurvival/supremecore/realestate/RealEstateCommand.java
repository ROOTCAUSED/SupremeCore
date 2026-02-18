package net.supremesurvival.supremecore.realestate;

import net.supremesurvival.supremecore.commonUtils.fileHandler.ConfigUtility;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class RealEstateCommand implements CommandExecutor {
    private static final int PAGE_SIZE = 8;

    private final RealEstateManager manager = new RealEstateManager();
    private final Map<UUID, Long> lastViewUse = new HashMap<>();

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

        if (args[0].equalsIgnoreCase("list")) {
            String townFilter = null;
            int page = 1;

            if (args.length >= 2) {
                if (isInteger(args[1])) {
                    page = Math.max(1, Integer.parseInt(args[1]));
                } else {
                    townFilter = args[1];
                }
            }

            if (args.length >= 3 && isInteger(args[2])) {
                page = Math.max(1, Integer.parseInt(args[2]));
            }

            List<RealEstateListing> listings = manager.getListings(townFilter);
            if (listings.isEmpty()) {
                player.sendMessage(ChatColor.GRAY + "No for-sale plots found" + (townFilter != null ? " for town filter '" + townFilter + "'" : "") + ".");
                return true;
            }

            int totalPages = (int) Math.ceil((double) listings.size() / PAGE_SIZE);
            page = Math.min(page, Math.max(totalPages, 1));
            int start = (page - 1) * PAGE_SIZE;
            int end = Math.min(start + PAGE_SIZE, listings.size());

            player.sendMessage(ChatColor.GOLD + "=== Real Estate Listings (" + page + "/" + totalPages + ") ===");
            for (int i = start; i < end; i++) {
                RealEstateListing l = listings.get(i);
                player.sendMessage(
                        ChatColor.YELLOW + "#" + l.id() + ChatColor.DARK_GRAY + " • "
                                + ChatColor.AQUA + l.townName()
                                + ChatColor.DARK_GRAY + " • "
                                + ChatColor.GRAY + l.worldName()
                                + ChatColor.DARK_GRAY + " @ "
                                + ChatColor.WHITE + l.centerX() + ", " + l.centerZ()
                                + ChatColor.DARK_GRAY + " • "
                                + ChatColor.GREEN + "$" + String.format(Locale.US, "%.2f", l.price())
                );
            }

            player.sendMessage(ChatColor.GRAY + "Use /realestate view <id> to teleport for viewing.");
            return true;
        }

        if (args[0].equalsIgnoreCase("view")) {
            if (args.length < 2 || !isInteger(args[1])) {
                player.sendMessage(ChatColor.RED + "Usage: /realestate view <listingId>");
                return true;
            }

            int id = Integer.parseInt(args[1]);
            RealEstateListing listing = manager.getListingById(id);
            if (listing == null) {
                player.sendMessage(ChatColor.RED + "Listing #" + id + " not found. Use /realestate list first.");
                return true;
            }

            RealEstateSecurityPolicy policy = loadPolicy();
            if (!policy.allowedWorlds.isEmpty() && !policy.allowedWorlds.contains(listing.worldName().toLowerCase(Locale.ROOT))) {
                player.sendMessage(ChatColor.RED + "Viewing is disabled for that world.");
                return true;
            }

            if (policy.cooldownSeconds > 0 && !player.hasPermission("realestate.bypasscooldown")) {
                long now = System.currentTimeMillis();
                long last = lastViewUse.getOrDefault(player.getUniqueId(), 0L);
                long waitMs = (policy.cooldownSeconds * 1000L) - (now - last);
                if (waitMs > 0) {
                    long waitSeconds = (waitMs + 999L) / 1000L;
                    player.sendMessage(ChatColor.RED + "You must wait " + waitSeconds + "s before using /realestate view again.");
                    return true;
                }
                lastViewUse.put(player.getUniqueId(), now);
            }

            Location target = manager.resolveTeleportLocation(listing);
            if (target == null) {
                player.sendMessage(ChatColor.RED + "Could not resolve a safe viewing location for that listing.");
                return true;
            }

            player.teleport(target);
            player.sendMessage(ChatColor.GOLD + "Viewing listing #" + id + ChatColor.GRAY + " in " + ChatColor.AQUA + listing.townName());
            return true;
        }

        sendHelp(player);
        return true;
    }

    private RealEstateSecurityPolicy loadPolicy() {
        FileConfiguration config = ConfigUtility.getModuleConfig("RealEstate");
        int cooldownSeconds = Math.max(0, config.getInt("view.cooldown-seconds", 15));
        List<String> worlds = new ArrayList<>();

        ConfigurationSection section = config.getConfigurationSection("view");
        if (section != null) {
            worlds = section.getStringList("world-allowlist");
        }

        Set<String> allowed = new HashSet<>();
        for (String world : worlds) {
            if (world != null && !world.isBlank()) {
                allowed.add(world.toLowerCase(Locale.ROOT));
            }
        }

        return new RealEstateSecurityPolicy(cooldownSeconds, allowed);
    }

    private void sendHelp(Player player) {
        player.sendMessage(ChatColor.YELLOW + "/realestate list [town] [page]");
        player.sendMessage(ChatColor.YELLOW + "/realestate view <listingId>");
    }

    private boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private record RealEstateSecurityPolicy(int cooldownSeconds, Set<String> allowedWorlds) {}
}
