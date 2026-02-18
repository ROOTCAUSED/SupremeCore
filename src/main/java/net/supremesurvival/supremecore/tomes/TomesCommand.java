package net.supremesurvival.supremecore.tomes;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TomesCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        if (args.length == 0) {
            player.sendMessage("/tomes retrieve");
            player.sendMessage("/tomes add [key]");
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

        if (args.length >= 1 && args[0].equalsIgnoreCase("add")) {
            if (!player.hasPermission("tomes.add")) {
                player.sendMessage("You do not have permission to use this command.");
                return true;
            }

            ItemStack inHand = player.getInventory().getItemInMainHand();
            if (inHand.getType() != Material.WRITTEN_BOOK) {
                player.sendMessage("Hold a written book in your main hand.");
                return true;
            }

            if (!(inHand.getItemMeta() instanceof BookMeta meta)) {
                player.sendMessage("That item does not have valid book metadata.");
                return true;
            }

            String key = (args.length >= 2) ? sanitizeKey(args[1]) : sanitizeKey(meta.getTitle());
            if (key == null || key.isEmpty()) {
                player.sendMessage("Could not derive a key. Use: /tomes add <key>");
                return true;
            }

            String title = meta.getTitle() == null || meta.getTitle().isBlank() ? key : meta.getTitle();
            String author = meta.getAuthor() == null || meta.getAuthor().isBlank() ? player.getName() : meta.getAuthor();
            String preamble = "Imported from in-game written book";
            String pages = String.join("\n", meta.getPages());
            List<String> lore = meta.hasLore() && meta.getLore() != null
                    ? new ArrayList<>(meta.getLore())
                    : new ArrayList<>(List.of("&7Imported tome", "&e[Common]"));

            File configFile = new File("plugins/SupremeCore/Tomes/config.yml");
            FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);

            if (!config.contains("Tomes")) {
                config.createSection("Tomes");
            }

            String basePath = "Tomes." + key;
            if (config.contains(basePath)) {
                player.sendMessage("A tome with key '" + key + "' already exists. Use a different key.");
                return true;
            }

            config.set(basePath + ".author", author);
            config.set(basePath + ".title", title);
            config.set(basePath + ".preamble", preamble);
            config.set(basePath + ".pages", pages);
            config.set(basePath + ".lore", lore);

            try {
                config.save(configFile);
            } catch (IOException e) {
                player.sendMessage("Failed to save tome: " + e.getMessage());
                return true;
            }

            TomeManager.enable();
            player.sendMessage("Added tome '" + title + "' as key '" + key + "' and reloaded TomeManager.");
            return true;
        }

        player.sendMessage("Usage: /tomes retrieve");
        player.sendMessage("Usage: /tomes add [key]");
        return true;
    }

    private String sanitizeKey(String input) {
        if (input == null) {
            return "";
        }
        return input
                .trim()
                .replaceAll("\\s+", "_")
                .replaceAll("[^a-zA-Z0-9_-]", "");
    }
}
