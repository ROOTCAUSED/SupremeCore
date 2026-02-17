package net.supremesurvival.supremecore.tomes;

import net.supremesurvival.supremecore.commonUtils.Logger;
import net.supremesurvival.supremecore.commonUtils.fileHandler.ConfigUtility;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;

public class TomeManager {
    public static ArrayList<Tome> tomes = new ArrayList<>();

    final static String handle = "Tome Manager";

    public static void enable() {
        FileConfiguration config = ConfigUtility.getModuleConfig("Tomes");
        tomes.clear();

        if (!config.contains("Tomes")) {
            Logger.sendMessage("No Tomes section found in config.", Logger.LogType.WARN, handle);
            return;
        }

        ConfigurationSection section = config.getConfigurationSection("Tomes");
        if (section == null) {
            Logger.sendMessage("Tomes section is null.", Logger.LogType.WARN, handle);
            return;
        }

        for (String tomeKey : section.getKeys(false)) {
            String basePath = tomeKey + ".";
            String tomeTitle = section.getString(basePath + "title", tomeKey);
            String tomeAuthor = section.getString(basePath + "author", "Unknown");
            String pages = section.getString(basePath + "pages", "");
            String preamble = section.getString(basePath + "preamble", "");
            List<String> lore = section.getStringList(basePath + "lore");

            try {
                Tome tome = new Tome(tomeAuthor, tomeTitle, pages, lore, preamble);
                tomes.add(tome);
                Logger.sendMessage("Registered Tome: " + tomeTitle, Logger.LogType.INFO, handle);
            } catch (Exception ex) {
                Logger.sendMessage("Failed to register tome '" + tomeKey + "': " + ex.getMessage(), Logger.LogType.ERR, handle);
            }
        }

        Logger.sendMessage("Loaded " + tomes.size() + " tome(s).", Logger.LogType.INFO, handle);
    }
}
