package net.supremesurvival.supremecore.artefacts;

import net.supremesurvival.supremecore.commonUtils.Logger;
import net.supremesurvival.supremecore.commonUtils.fileHandler.ConfigUtility;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ArtefactManager {
    public static List<Artefact> artefacts = new ArrayList<>();
    private static final String HANDLE = "Artefacts";

    public static void enable() {
        FileConfiguration config = ConfigUtility.getModuleConfig("Artefacts");
        artefacts.clear();

        if (!config.contains("Artefacts")) {
            Logger.sendMessage("No Artefacts section found in config.", Logger.LogType.WARN, HANDLE);
            return;
        }

        ConfigurationSection section = config.getConfigurationSection("Artefacts");
        if (section == null) {
            Logger.sendMessage("Artefacts section is null.", Logger.LogType.WARN, HANDLE);
            return;
        }

        for (String artefactKey : section.getKeys(false)) {
            String basePath = artefactKey + ".";

            String name = section.getString(basePath + "name", artefactKey);
            List<String> lore = section.getStringList(basePath + "lore");
            String rarity = section.getString(basePath + "rarity", "Unknown");
            String itemTypeRaw = section.getString(basePath + "itemType", "STONE");

            Material material = Material.matchMaterial(itemTypeRaw.toUpperCase(Locale.ROOT));
            if (material == null) {
                Logger.sendMessage("Invalid itemType for artefact '" + artefactKey + "': " + itemTypeRaw, Logger.LogType.ERR, HANDLE);
                continue;
            }

            int damage = section.getInt(basePath + "stats.damage", 0);
            ItemStack item = new ItemStack(material);
            Artefact artefact = new Artefact(item, lore, name, rarity, damage);
            artefacts.add(artefact);
        }

        Logger.sendMessage("Loaded " + artefacts.size() + " artefact(s).", Logger.LogType.INFO, HANDLE);
    }
}
