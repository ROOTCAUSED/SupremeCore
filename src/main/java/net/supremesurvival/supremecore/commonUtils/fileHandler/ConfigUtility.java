package net.supremesurvival.supremecore.commonUtils.fileHandler;

import net.supremesurvival.supremecore.SupremeCore;
import net.supremesurvival.supremecore.commonUtils.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.util.ArrayList;

public class ConfigUtility {
    static SupremeCore plugin;
    static String module;
    static ArrayList<String> moduleList = new ArrayList();

    final static String handle = "Config Utility";
    public ConfigUtility(SupremeCore plugin){
        ConfigUtility.plugin = plugin;
    }
    private void loadCfg(){
        FileConfiguration config = ConfigUtility.plugin.getConfig();
        config.options().header("SupremeCore v" + ConfigUtility.plugin.getDescription().getVersion()+" Main configuration");
    }
    public void initCfg(){
        ConfigUtility.plugin.getConfig().options().copyDefaults(true);
        ConfigUtility.plugin.getServer().getConsoleSender().sendMessage(ChatColor.YELLOW + "[SupremeCore] [+] " + ChatColor.GRAY + "Config file loaded.");
        ConfigUtility.plugin.saveConfig();
    }

    public static void initModuleCfg(String module){
        ConfigUtility.module = module;
        File fileConfig = new File("plugins/SupremeCore/" + module + "/config.yml");
        FileConfiguration ymlConfigFile = YamlConfiguration.loadConfiguration(fileConfig);
        try{
            if(!fileConfig.exists()){
                //retrieve resource file from jar for provided module path = /resources/MODULENAME/config.yml
                InputStream inputStream = plugin.getResource(module +"/config.yml");
                ymlConfigFile = YamlConfiguration.loadConfiguration(new InputStreamReader(inputStream));
                //save resourcefile to fileConfig created above
                ymlConfigFile.save(fileConfig);
                Logger.sendMessage("Created Config file for " + module, Logger.LogType.INFO, handle);
            }
        }catch (IOException exception) {
            Bukkit.getConsoleSender().sendMessage("Cannot Create Config.yml for " + handle);
        }
        moduleList.add(module);
    }


    public static FileConfiguration getModuleConfig(String module){
        if(ConfigUtility.isFile(module)){
            File fileConfig = new File("plugins/SupremeCore/" + module + "/config.yml");
            Logger.sendMessage("Retrieved config file for " + module, Logger.LogType.INFO, handle);
            return YamlConfiguration.loadConfiguration(fileConfig);
        }else{
            initModuleCfg(module);
        }
        File fileConfig = new File("plugins/SupremeCore/" + module + "/config.yml");
        FileConfiguration ymlConfigFile = YamlConfiguration.loadConfiguration(fileConfig);
        Logger.sendMessage("Retrieved config file for " + module, Logger.LogType.INFO, handle);
        return ymlConfigFile;
    }

    public static boolean isFile(String module)
    {
        File f = new File("plugins/SupremeCore/" + module + "/config.yml");
        return f.exists();
    }
}
