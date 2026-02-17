package net.supremesurvival.supremecore.tomes;

import net.supremesurvival.supremecore.commonUtils.Logger;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.map.MinecraftFont;

import java.util.ArrayList;
import java.util.List;

// Lore tome wrapper with defensive item cloning.
public class Tome {
    private final ItemStack tome;
    private final BookMeta tomeMeta;

    final static String handle = "Tome";

    public Tome(String author, String title, String message, List<String> lore, String preamble) {
        this.tome = new ItemStack(Material.WRITTEN_BOOK);
        this.tomeMeta = (BookMeta) tome.getItemMeta();

        if (this.tomeMeta == null) {
            throw new IllegalStateException("Unable to create WRITTEN_BOOK meta");
        }

        Logger.sendMessage("Creating Tome: " + title, Logger.LogType.INFO, handle);

        this.tomeMeta.setAuthor(author);
        this.tomeMeta.setTitle(title);
        addPages(getPages(message), preamble);

        List<String> tempLore = new ArrayList<>();
        for (String loreLine : lore) {
            tempLore.add(ChatColor.translateAlternateColorCodes('&', loreLine));
        }
        this.tomeMeta.setLore(tempLore);

        this.tome.setItemMeta(this.tomeMeta);
        Logger.sendMessage("Tome: " + title + " created", Logger.LogType.INFO, handle);
    }

    public String getName() {
        return this.tomeMeta.getTitle();
    }

    public ItemStack getItem() {
        return this.tome.clone();
    }

    public String coverPage(String preamble) {
        return "\n\n" + tomeMeta.getTitle() + "\n\n" + preamble + "\n\nBy\n\n" + tomeMeta.getAuthor();
    }

    private void addPages(List<String> pages, String preamble) {
        this.tomeMeta.addPage(coverPage(preamble));
        for (String page : pages) {
            this.tomeMeta.addPage(page);
        }
    }

    private static List<String> getPages(String rawText) {
        if (rawText == null) {
            rawText = "";
        }
        rawText = "\n" + rawText;
        List<String> pages = new ArrayList<>();

        List<String> lines = getLines(rawText);
        StringBuilder pageText = new StringBuilder();
        for (int i = 1; i < lines.size(); i++) {
            pageText.append(lines.get(i));
            if (i != 1 && i % 14 == 0) {
                pages.add(pageText.toString());
                pageText = new StringBuilder();
            }
        }
        if (!pageText.isEmpty()) {
            pages.add(pageText.toString());
        }
        return pages;
    }

    private static List<String> getLines(String rawText) {
        final MinecraftFont font = new MinecraftFont();
        final int maxLineWidth = font.getWidth("LLLLLLLLLLLLLLLLLLL"); // ~113px
        List<String> lines = new ArrayList<>();
        try {
            for (String section : rawText.split("\n")) {
                if (section.equals("")) {
                    lines.add("\n");
                } else {
                    String[] words = ChatColor.stripColor(section).split(" ");
                    String line = "";
                    for (String word : words) {
                        if (line.isEmpty()) {
                            line = word;
                            continue;
                        }
                        int spaces = 0;
                        if (font.getWidth(" ") == 2) {
                            spaces = 1;
                            for (int i = 0; i < line.length(); ++i) {
                                if (line.charAt(i) == ' ') {
                                    spaces++;
                                }
                            }
                        }
                        if (font.getWidth(line + " " + word) + spaces > maxLineWidth) {
                            lines.add(line + '\n');
                            line = word;
                            continue;
                        }

                        line += " " + word;
                    }
                    if (!line.equals("")) {
                        lines.add(line + "\n");
                    }
                }
            }
        } catch (IllegalArgumentException exception) {
            lines.clear();
            Logger.sendMessage(exception.toString(), Logger.LogType.INFO, handle);
        }
        return lines;
    }
}
