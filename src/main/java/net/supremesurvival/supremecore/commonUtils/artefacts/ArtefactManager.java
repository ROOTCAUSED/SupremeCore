package net.supremesurvival.supremecore.commonUtils.artefacts;

import net.supremesurvival.supremecore.commonUtils.ConfigUtility;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ArtefactManager {
    public static List<Artefact> artefacts = new ArrayList<Artefact>();
    public static void enable(){
        FileConfiguration config = ConfigUtility.getModuleConfig("Artefacts");
        if(config.contains("Artefacts")){
            ConfigurationSection section = config.getConfigurationSection("Artefacts");
            if(!(section==null)){
                for(final String artefactKey : section.getKeys(false)){
                    String name = section.getString(artefactKey + ".name");
                    List<String> lore = section.getStringList(artefactKey + ".lore");
                    String rarity = section.getString(artefactKey + ".rarity");
                    ItemStack item = new ItemStack(Material.getMaterial(section.getString(artefactKey + ".itemType")));
                    Artefact artefact = new Artefact(item,lore,name,rarity);
                    String artefactType = section.getString(artefactKey + ".type");
                    if((!(artefactType==null)) && (artefactType.equals("weapon"))){
                        int damage = section.getInt(artefactType+".stats.damage");

                    }
                    artefacts.add(artefact);
                }
            }
        }
    }
}
