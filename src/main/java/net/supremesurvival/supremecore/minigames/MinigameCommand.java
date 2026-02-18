package net.supremesurvival.supremecore.minigames;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MinigameCommand implements CommandExecutor, TabCompleter {
    private final MinigameManager manager;

    public MinigameCommand(MinigameManager manager) {
        this.manager = manager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sendUsage(sender);
            return true;
        }

        String sub = args[0].toLowerCase(Locale.ROOT);

        switch (sub) {
            case "join" -> {
                if (!(sender instanceof Player player)) {
                    sender.sendMessage("Only players can join the queue.");
                    return true;
                }
                boolean joined = manager.joinQueue(player);
                if (joined) {
                    sender.sendMessage(ChatColor.GREEN + "Joined the minigame queue. Queue size: " + manager.getQueueSize());
                } else {
                    sender.sendMessage(ChatColor.YELLOW + "You are already queued.");
                }
                return true;
            }
            case "leave" -> {
                if (!(sender instanceof Player player)) {
                    sender.sendMessage("Only players can leave the queue.");
                    return true;
                }
                boolean left = manager.leaveQueue(player);
                sender.sendMessage(left
                        ? ChatColor.GREEN + "You left the minigame queue."
                        : ChatColor.YELLOW + "You are not in queue.");
                return true;
            }
            case "status" -> {
                MinigameSession session = manager.getActiveSession();
                if (session == null) {
                    sender.sendMessage(ChatColor.GRAY + "No active minigame. Queue size: " + manager.getQueueSize());
                } else {
                    sender.sendMessage(ChatColor.GOLD + "Active: " + session.getType() + ChatColor.GRAY + " Players: " + session.getParticipants().size());
                }
                return true;
            }
            case "forcestart" -> {
                if (!sender.hasPermission("minigame.admin")) {
                    sender.sendMessage(ChatColor.RED + "No permission.");
                    return true;
                }
                MinigameType type = resolveType(args, 1);
                if (type == null) {
                    sender.sendMessage(ChatColor.RED + "Usage: /minigame forcestart <type>");
                    return true;
                }
                boolean started = manager.forceStart(type);
                sender.sendMessage(started ? ChatColor.GREEN + "Forced start attempted for " + type : ChatColor.RED + "Could not start minigame.");
                return true;
            }
            case "cancel" -> {
                if (!sender.hasPermission("minigame.admin")) {
                    sender.sendMessage(ChatColor.RED + "No permission.");
                    return true;
                }
                boolean cancelled = manager.cancelActive("Cancelled by admin");
                sender.sendMessage(cancelled ? ChatColor.GREEN + "Minigame cancelled." : ChatColor.YELLOW + "No active minigame.");
                return true;
            }
            case "config" -> {
                if (!sender.hasPermission("minigame.admin")) {
                    sender.sendMessage(ChatColor.RED + "No permission.");
                    return true;
                }
                return handleConfig(sender, args);
            }
            default -> {
                sendUsage(sender);
                return true;
            }
        }
    }

    private boolean handleConfig(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: /minigame config <reload|interval|threshold|duration>");
            return true;
        }

        switch (args[1].toLowerCase(Locale.ROOT)) {
            case "reload" -> {
                manager.reload();
                sender.sendMessage(ChatColor.GREEN + "Minigame config reloaded.");
                return true;
            }
            case "interval" -> {
                if (args.length < 3) {
                    sender.sendMessage(ChatColor.RED + "Usage: /minigame config interval <minutes>");
                    return true;
                }
                Integer minutes = parseInt(args[2]);
                if (minutes == null) {
                    sender.sendMessage(ChatColor.RED + "Minutes must be a number.");
                    return true;
                }
                if (!manager.setIntervalMinutes(minutes)) {
                    sender.sendMessage(ChatColor.RED + "Failed to save interval.");
                    return true;
                }
                manager.reload();
                sender.sendMessage(ChatColor.GREEN + "Minigame interval updated to " + minutes + "m.");
                return true;
            }
            case "threshold" -> {
                if (args.length < 4) {
                    sender.sendMessage(ChatColor.RED + "Usage: /minigame config threshold <type> <players>");
                    return true;
                }
                MinigameType type = MinigameType.fromString(args[2]);
                Integer players = parseInt(args[3]);
                if (type == null || players == null) {
                    sender.sendMessage(ChatColor.RED + "Invalid type or player count.");
                    return true;
                }
                if (!manager.setThreshold(type, players)) {
                    sender.sendMessage(ChatColor.RED + "Failed to save threshold.");
                    return true;
                }
                manager.reload();
                sender.sendMessage(ChatColor.GREEN + "Threshold updated for " + type + ".");
                return true;
            }
            case "duration" -> {
                if (args.length < 4) {
                    sender.sendMessage(ChatColor.RED + "Usage: /minigame config duration <type> <seconds>");
                    return true;
                }
                MinigameType type = MinigameType.fromString(args[2]);
                Integer seconds = parseInt(args[3]);
                if (type == null || seconds == null) {
                    sender.sendMessage(ChatColor.RED + "Invalid type or duration.");
                    return true;
                }
                if (!manager.setDuration(type, seconds)) {
                    sender.sendMessage(ChatColor.RED + "Failed to save duration.");
                    return true;
                }
                manager.reload();
                sender.sendMessage(ChatColor.GREEN + "Duration updated for " + type + ".");
                return true;
            }
            default -> {
                sender.sendMessage(ChatColor.RED + "Usage: /minigame config <reload|interval|threshold|duration>");
                return true;
            }
        }
    }

    private MinigameType resolveType(String[] args, int index) {
        if (args.length <= index) return null;
        return MinigameType.fromString(args[index]);
    }

    private Integer parseInt(String raw) {
        try {
            return Integer.parseInt(raw);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private void sendUsage(CommandSender sender) {
        sender.sendMessage(ChatColor.YELLOW + "/minigame join");
        sender.sendMessage(ChatColor.YELLOW + "/minigame leave");
        sender.sendMessage(ChatColor.YELLOW + "/minigame status");
        if (sender.hasPermission("minigame.admin")) {
            sender.sendMessage(ChatColor.YELLOW + "/minigame forcestart <type>");
            sender.sendMessage(ChatColor.YELLOW + "/minigame cancel");
            sender.sendMessage(ChatColor.YELLOW + "/minigame config <reload|interval|threshold|duration>");
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> out = new ArrayList<>();

        if (args.length == 1) {
            out.add("join");
            out.add("leave");
            out.add("status");
            if (sender.hasPermission("minigame.admin")) {
                out.add("forcestart");
                out.add("cancel");
                out.add("config");
            }
            return filter(out, args[0]);
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("config") && sender.hasPermission("minigame.admin")) {
            out.add("reload");
            out.add("interval");
            out.add("threshold");
            out.add("duration");
            return filter(out, args[1]);
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("forcestart") && sender.hasPermission("minigame.admin")) {
            for (MinigameType type : MinigameType.values()) {
                out.add(type.name().toLowerCase(Locale.ROOT));
            }
            return filter(out, args[1]);
        }

        if (args.length == 3 && args[0].equalsIgnoreCase("config")
                && (args[1].equalsIgnoreCase("threshold") || args[1].equalsIgnoreCase("duration"))) {
            for (MinigameType type : MinigameType.values()) {
                out.add(type.name().toLowerCase(Locale.ROOT));
            }
            return filter(out, args[2]);
        }

        return out;
    }

    private List<String> filter(List<String> options, String query) {
        String q = query.toLowerCase(Locale.ROOT);
        return options.stream().filter(s -> s.toLowerCase(Locale.ROOT).startsWith(q)).toList();
    }
}
