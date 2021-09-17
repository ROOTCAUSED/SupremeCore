package net.supremesurvival.supremecore.commonUtils;

import net.supremesurvival.supremecore.SupremeCore;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;

public class ConfigUtility {
    SupremeCore plugin;

    public ConfigUtility(SupremeCore plugin){
        this.plugin = plugin;
    }

    private void loadCfg(){
        FileConfiguration config = this.plugin.getConfig();
        config.options().header("SupremeCore v" + this.plugin.getDescription().getVersion()+" Main configuration");
    }
    public void initCfg(){
        this.plugin.getConfig().options().copyDefaults(true);
        this.plugin.getServer().getConsoleSender().sendMessage(ChatColor.YELLOW + "[SupremeCore] [+] " + ChatColor.GRAY + "Config file loaded.");
        this.plugin.saveConfig();
        plugin.announcements = this.plugin.getConfig().getStringList("announcements");
        this.plugin.getServer().getConsoleSender().sendMessage("Announcements loaded");
    }
    public boolean isFile(String name, String directory)
    {
        File f = new File(directory, name);
        return f.exists();
    }
}
