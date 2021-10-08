package net.supremesurvival.supremecore.commonUtils.tomes;

import net.supremesurvival.supremecore.commonUtils.ConfigUtility;
import net.supremesurvival.supremecore.commonUtils.Logger;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TomeManager {
    public static ArrayList<Tome> tomes = new ArrayList<Tome>();
    public static Tome tome;
    public TomeManager(){
    }

    public static void enable(){
        //retrieve tomes from config
        FileConfiguration config = ConfigUtility.getModuleConfig("Tomes");
        if(config.contains("Tomes")){
            ConfigurationSection section = config.getConfigurationSection("Tomes");
            if(section != null){
                for(final String tomeKey : section.getKeys(false)){
                    String tomeTitle = section.getString(tomeKey + ".title");
                    String tomeAuthor = section.getString(tomeKey + ".author");
                    String pages = section.getString(tomeKey + ".pages");
                    String preamble = section.getString(tomeKey + ".preamble");
                    List<String> lore = section.getStringList(tomeKey + ".lore");
                    tome = new Tome(tomeAuthor, tomeTitle, pages, lore, preamble);
                    tomes.add(tome);
                    Logger.sendMessage("Registered Tome: " + tomeTitle, Logger.LogType.INFO,"Tomes");
                }
            }
        }
    }
    }

