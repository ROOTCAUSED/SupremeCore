package net.supremesurvival.supremecore;
import net.supremesurvival.supremecore.commonUtils.ChatUtil;
import net.supremesurvival.supremecore.commonUtils.ConfigUtility;
import net.supremesurvival.supremecore.commonUtils.Logger;
import net.supremesurvival.supremecore.commonUtils.artefacts.ArtefactManager;
import net.supremesurvival.supremecore.commonUtils.artefacts.ArtefactsCommand;
import net.supremesurvival.supremecore.commonUtils.intervalAnnouncer.IntervalAnnouncer;
import net.supremesurvival.supremecore.commonUtils.morality.Morality;
import net.supremesurvival.supremecore.commonUtils.placeholder.SupremePlaceholder;
import net.supremesurvival.supremecore.commonUtils.tomes.TomeManager;
import net.supremesurvival.supremecore.commonUtils.tomes.TomesCommand;
import net.supremesurvival.supremecore.mobUtils.HorseInfo;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import net.supremesurvival.supremecore.mobUtils.MobLoot;
import com.sk89q.worldguard.*;
public final class SupremeCore extends JavaPlugin implements Listener {
    public ChatUtil chatUtil = new ChatUtil(this);
    public boolean placeholderAPI;
    public boolean townyAdvanced;
    public ConfigUtility configUtility = new ConfigUtility(this);
    IntervalAnnouncer intervalAnnouncer = new IntervalAnnouncer(this);
    @Override
    public void onEnable() {
        // Plugin startup logic
        super.onEnable();
        configUtility.initCfg();
        TomeManager.enable();
        ArtefactManager.enable();
        this.getCommand("Artefacts").setExecutor(new ArtefactsCommand());
        this.getCommand("Tomes").setExecutor(new TomesCommand());
        this.initHooks();
        this.initUtils();
        Logger.sendMessage((ChatColor.YELLOW + "[SupremeCore] [+] " + ChatColor.GRAY + "SupremeCore loaded - Shit just got real."), Logger.LogType.INFO, "SupremeCore");
        this.getServer().getPluginManager().registerEvents(new MobLoot(), this);
        Logger.sendMessage((ChatColor.YELLOW + "[SupremeCore] [+] " + ChatColor.GRAY + "SupremeNerf Gold Filter Loaded."), Logger.LogType.INFO, "SupremeCore");
        this.getCommand("HorseInfo").setExecutor(new HorseInfo());
        Morality.enable();
        this.getServer().getPluginManager().registerEvents(new Morality(), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        Morality.disable();
    }

    public void initHooks() {
        placeholderAPI = Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI");
        if (placeholderAPI) {
            Logger.sendMessage("PAPI hooked.", Logger.LogType.INFO, "SupremeCore");
        }
        townyAdvanced = Bukkit.getPluginManager().isPluginEnabled("Towny");
        if (townyAdvanced) {
            Logger.sendMessage("Towny hooked.", Logger.LogType.INFO, "SupremeCore");
        }
    }

    public void initUtils() {
        SupremePlaceholder.enable(this);
        this.getServer().getPluginManager().registerEvents(this, this);
        intervalAnnouncer.enable();

    }
}