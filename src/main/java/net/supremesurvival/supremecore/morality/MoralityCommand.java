package net.supremesurvival.supremecore.morality;

import net.supremesurvival.supremecore.morality.player.MoralPlayer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MoralityCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0 || args[0].equalsIgnoreCase("status")) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage("Usage: /morality status <player>");
                return true;
            }

            Player target = player;
            if (args.length >= 2 && sender.hasPermission("morality.admin")) {
                Player parsed = Bukkit.getPlayerExact(args[1]);
                if (parsed != null) target = parsed;
            }

            sender.sendMessage("§7[§6Morality§7] §f" + target.getName() + " §7=> §f"
                    + Morality.getMorality(target) + " §8(§f" + Morality.getMoralStanding(target) + "§8)");
            return true;
        }

        if (args[0].equalsIgnoreCase("top")) {
            int limit = 10;
            if (args.length >= 2) {
                try {
                    limit = Math.max(1, Math.min(25, Integer.parseInt(args[1])));
                } catch (NumberFormatException ignored) {
                }
            }

            List<MoralPlayer> top = Morality.top(limit);
            sender.sendMessage("§7[§6Morality§7] §fTop " + top.size() + " players:");
            int i = 1;
            for (MoralPlayer moralPlayer : top) {
                String name = Bukkit.getOfflinePlayer(moralPlayer.getUuid()).getName();
                if (name == null) name = moralPlayer.getUuid().toString();
                sender.sendMessage("§8" + i + ". §f" + name + " §7- §f" + moralPlayer.getMorality()
                        + " §8(§f" + moralPlayer.getStanding() + "§8)");
                i++;
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("morality.admin")) {
                sender.sendMessage("§cYou do not have permission.");
                return true;
            }
            Morality.reload();
            sender.sendMessage("§7[§6Morality§7] Reloaded morality config and data.");
            return true;
        }

        if (args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("set")) {
            if (!sender.hasPermission("morality.admin")) {
                sender.sendMessage("§cYou do not have permission.");
                return true;
            }

            if (args.length < 3) {
                sender.sendMessage("Usage: /morality " + args[0] + " <player> <amount>");
                return true;
            }

            Player target = Bukkit.getPlayerExact(args[1]);
            if (target == null) {
                sender.sendMessage("§cPlayer not found online.");
                return true;
            }

            int amount;
            try {
                amount = Integer.parseInt(args[2]);
            } catch (NumberFormatException ex) {
                sender.sendMessage("§cAmount must be a number.");
                return true;
            }

            if (args[0].equalsIgnoreCase("set")) {
                int current = Morality.getMorality(target);
                Morality.adjustMorality(target.getUniqueId(), amount - current, "Admin set", true);
            } else {
                Morality.adjustMorality(target.getUniqueId(), amount, "Admin adjustment", true);
            }

            sender.sendMessage("§7[§6Morality§7] Updated §f" + target.getName() + "§7.");
            return true;
        }

        sender.sendMessage("§7Usage: /morality <status|top|add|set|reload>");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            completions.add("status");
            completions.add("top");
            if (sender.hasPermission("morality.admin")) {
                completions.add("add");
                completions.add("set");
                completions.add("reload");
            }
            return filter(completions, args[0]);
        }

        if (args.length == 2 && (args[0].equalsIgnoreCase("status") || args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("set"))) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                completions.add(p.getName());
            }
            return filter(completions, args[1]);
        }

        return completions;
    }

    private List<String> filter(List<String> options, String query) {
        String lower = query.toLowerCase(Locale.ROOT);
        return options.stream().filter(s -> s.toLowerCase(Locale.ROOT).startsWith(lower)).toList();
    }
}
