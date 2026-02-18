package net.supremesurvival.supremecore;
import net.supremesurvival.supremecore.commonUtils.ChatUtil;
import net.supremesurvival.supremecore.commonUtils.fileHandler.ConfigUtility;
import net.supremesurvival.supremecore.commonUtils.Logger;
import net.supremesurvival.supremecore.artefacts.ArtefactManager;
import net.supremesurvival.supremecore.artefacts.ArtefactsCommand;
import net.supremesurvival.supremecore.commonUtils.fileHandler.FileHandler;
import net.supremesurvival.supremecore.commonUtils.intervalAnnouncer.IntervalAnnouncer;
import net.supremesurvival.supremecore.landmarks.LandmarkCommand;
import net.supremesurvival.supremecore.landmarks.LandmarkManager;
import net.supremesurvival.supremecore.landmarks.PlayerListeners;
import net.supremesurvival.supremecore.morality.Morality;
import net.supremesurvival.supremecore.commonUtils.placeholder.SupremePlaceholder;
import net.supremesurvival.supremecore.tomes.TomeManager;
import net.supremesurvival.supremecore.tomes.TomesCommand;
import net.supremesurvival.supremecore.mobUtils.HorseInfo;
import net.supremesurvival.supremecore.sanguine.Vampire;
import net.supremesurvival.supremecore.realestate.RealEstateCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import net.supremesurvival.supremecore.mobUtils.MobLoot;

public final class SupremeCore extends JavaPlugin implements Listener {
    public ChatUtil chatUtil = new ChatUtil(this);
    public boolean placeholderAPI;
    public boolean townyAdvanced;
    public ConfigUtility configUtility = new ConfigUtility(this);
    IntervalAnnouncer intervalAnnouncer = new IntervalAnnouncer(this);
    FileHandler fileHandler = new FileHandler(this);
    Vampire vampire = new Vampire(this);
    @Override
    public void onEnable() {
        // Plugin startup logic
        super.onEnable();
        configUtility.initCfg();
        TomeManager.enable();
        ArtefactManager.enable();
        LandmarkManager.enable();
        this.getCommand("Artefacts").setExecutor(new ArtefactsCommand());
        this.getCommand("Tomes").setExecutor(new TomesCommand());
        this.initHooks();
        this.initUtils();
        Logger.sendMessage((ChatColor.YELLOW + "[SupremeCore] [+] " + ChatColor.GRAY + "SupremeCore loaded - Shit just got real."), Logger.LogType.INFO, "SupremeCore");
        this.getServer().getPluginManager().registerEvents(new MobLoot(), this);
        Logger.sendMessage((ChatColor.YELLOW + "[SupremeCore] [+] " + ChatColor.GRAY + "SupremeNerf Gold Filter Loaded."), Logger.LogType.INFO, "SupremeCore");
        this.getCommand("HorseInfo").setExecutor(new HorseInfo());
        this.getCommand("Landmarks").setExecutor(new LandmarkCommand());
        this.getCommand("Vampire").setExecutor(vampire);
        this.getCommand("RealEstate").setExecutor(new RealEstateCommand());
        Morality.enable();
        this.getServer().getPluginManager().registerEvents(new Morality(), this);
        this.getServer().getPluginManager().registerEvents(vampire, this);
        vampire.enable();
        this.getServer().getPluginManager().registerEvents(new PlayerListeners(), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        Morality.disable();
        vampire.disable();
        LandmarkManager.disable();
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