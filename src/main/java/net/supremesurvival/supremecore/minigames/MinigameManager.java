package net.supremesurvival.supremecore.minigames;

import net.supremesurvival.supremecore.SupremeCore;
import net.supremesurvival.supremecore.commonUtils.Logger;
import net.supremesurvival.supremecore.commonUtils.fileHandler.ConfigUtility;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class MinigameManager {
    private static final String HANDLE = "Minigames";

    private final SupremeCore plugin;
    private final Set<UUID> queue = new HashSet<>();
    private final Map<MinigameType, MinigameDefinition> definitions = new EnumMap<>(MinigameType.class);
    private final List<MinigameType> rotation = new ArrayList<>();

    private MinigameSession activeSession;
    private BukkitTask intervalTask;

    private int intervalMinutes;
    private int rotationIndex;
    private Location lobbyReturn;

    public MinigameManager(SupremeCore plugin) {
        this.plugin = plugin;
    }

    public void enable() {
        reload();
    }

    public void disable() {
        if (intervalTask != null) {
            intervalTask.cancel();
            intervalTask = null;
        }
        if (activeSession != null) {
            endActiveSession("Server shutting down");
        }
    }

    public void reload() {
        FileConfiguration config = ConfigUtility.getModuleConfig("Minigames");

        this.intervalMinutes = Math.max(1, config.getInt("interval-minutes", 15));
        this.rotationIndex = 0;

        loadLobby(config);
        loadDefinitions(config);

        if (intervalTask != null) {
            intervalTask.cancel();
        }

        long ticks = intervalMinutes * 60L * 20L;
        intervalTask = Bukkit.getScheduler().runTaskTimer(plugin, this::runScheduledTrigger, ticks, ticks);

        Logger.sendMessage("Minigames enabled with interval=" + intervalMinutes + "m and " + definitions.size() + " type(s)",
                Logger.LogType.INFO, HANDLE);
    }

    private void loadLobby(FileConfiguration config) {
        String worldName = config.getString("lobby.world", "world");
        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            world = Bukkit.getWorlds().isEmpty() ? null : Bukkit.getWorlds().get(0);
        }

        if (world == null) {
            lobbyReturn = null;
            return;
        }

        double x = config.getDouble("lobby.x", world.getSpawnLocation().getX());
        double y = config.getDouble("lobby.y", world.getSpawnLocation().getY());
        double z = config.getDouble("lobby.z", world.getSpawnLocation().getZ());
        float yaw = (float) config.getDouble("lobby.yaw", 0.0);
        float pitch = (float) config.getDouble("lobby.pitch", 0.0);
        lobbyReturn = new Location(world, x, y, z, yaw, pitch);
    }

    private void loadDefinitions(FileConfiguration config) {
        definitions.clear();
        rotation.clear();

        List<String> configuredOrder = config.getStringList("rotation-order");
        for (String raw : configuredOrder) {
            MinigameType type = MinigameType.fromString(raw);
            if (type != null && !rotation.contains(type)) {
                rotation.add(type);
            }
        }

        ConfigurationSection section = config.getConfigurationSection("types");
        if (section == null) return;

        for (String key : section.getKeys(false)) {
            MinigameType type = MinigameType.fromString(key);
            if (type == null) continue;

            ConfigurationSection t = section.getConfigurationSection(key);
            if (t == null) continue;

            int minPlayers = Math.max(1, t.getInt("min-players", 2));
            int durationSeconds = Math.max(30, t.getInt("duration-seconds", 600));
            Location spawn = parseLocation(t.getConfigurationSection("arena"));
            if (spawn == null) continue;

            definitions.put(type, new MinigameDefinition(type, minPlayers, durationSeconds, spawn));
            if (!rotation.contains(type)) {
                rotation.add(type);
            }
        }
    }

    private Location parseLocation(ConfigurationSection section) {
        if (section == null) return null;
        String worldName = section.getString("world", "world");
        World world = Bukkit.getWorld(worldName);
        if (world == null) return null;

        double x = section.getDouble("x", world.getSpawnLocation().getX());
        double y = section.getDouble("y", world.getSpawnLocation().getY());
        double z = section.getDouble("z", world.getSpawnLocation().getZ());
        float yaw = (float) section.getDouble("yaw", 0.0);
        float pitch = (float) section.getDouble("pitch", 0.0);

        return new Location(world, x, y, z, yaw, pitch);
    }

    private void runScheduledTrigger() {
        if (activeSession != null || rotation.isEmpty()) {
            return;
        }

        MinigameType type = rotation.get(rotationIndex % rotation.size());
        rotationIndex++;
        tryStart(type, false);
    }

    public boolean joinQueue(Player player) {
        if (activeSession != null && activeSession.getParticipants().contains(player.getUniqueId())) {
            return false;
        }
        return queue.add(player.getUniqueId());
    }

    public boolean leaveQueue(Player player) {
        return queue.remove(player.getUniqueId());
    }

    public int getQueueSize() {
        return queue.size();
    }

    public MinigameSession getActiveSession() {
        return activeSession;
    }

    public Collection<MinigameDefinition> getDefinitions() {
        return definitions.values();
    }

    public boolean forceStart(MinigameType type) {
        return tryStart(type, true);
    }

    public boolean cancelActive(String reason) {
        if (activeSession == null) {
            return false;
        }
        endActiveSession(reason);
        return true;
    }

    private boolean tryStart(MinigameType type, boolean forced) {
        if (activeSession != null) return false;

        MinigameDefinition definition = definitions.get(type);
        if (definition == null) {
            Logger.sendMessage("No definition for minigame type " + type, Logger.LogType.WARN, HANDLE);
            return false;
        }

        if (!forced && queue.size() < definition.minPlayers()) {
            broadcastToQueue(ChatColor.GRAY + "Not enough players for " + type + " (" + queue.size() + "/" + definition.minPlayers() + ")");
            return false;
        }

        Set<UUID> participants = new HashSet<>(queue);
        queue.clear();

        long now = System.currentTimeMillis();
        long endAt = now + (definition.durationSeconds() * 1000L);
        activeSession = new MinigameSession(type, participants, now, endAt);

        for (UUID id : participants) {
            Player p = Bukkit.getPlayer(id);
            if (p == null || !p.isOnline()) continue;
            p.teleport(definition.arenaSpawn());
            p.sendMessage(ChatColor.GOLD + "Minigame started: " + type + ChatColor.GRAY + " (" + definition.durationSeconds() + "s)");
        }

        BukkitTask ticker = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            if (activeSession == null) return;
            long remaining = Math.max(0L, (activeSession.getEndAtMillis() - System.currentTimeMillis()) / 1000L);

            for (UUID id : activeSession.getParticipants()) {
                Player p = Bukkit.getPlayer(id);
                if (p == null || !p.isOnline()) continue;
                p.sendActionBar(ChatColor.YELLOW + "Minigame: " + activeSession.getType() + ChatColor.DARK_GRAY + " | " + ChatColor.WHITE + remaining + "s");
            }

            if (remaining <= 0) {
                endActiveSession("Time expired");
            }
        }, 20L, 20L);

        activeSession.setTickerTask(ticker);
        return true;
    }

    private void endActiveSession(String reason) {
        MinigameSession session = activeSession;
        if (session == null) return;

        if (session.getTickerTask() != null) {
            session.getTickerTask().cancel();
        }

        for (UUID id : session.getParticipants()) {
            Player p = Bukkit.getPlayer(id);
            if (p == null || !p.isOnline()) continue;
            if (lobbyReturn != null) {
                p.teleport(lobbyReturn);
            }
            p.sendMessage(ChatColor.GREEN + "Minigame ended: " + session.getType() + ChatColor.GRAY + " (" + reason + ")");
        }

        activeSession = null;
    }

    private void broadcastToQueue(String message) {
        for (UUID id : queue) {
            Player p = Bukkit.getPlayer(id);
            if (p != null && p.isOnline()) {
                p.sendMessage(message);
            }
        }
    }

    public boolean setIntervalMinutes(int minutes) {
        minutes = Math.max(1, minutes);
        File file = new File("plugins/SupremeCore/Minigames/config.yml");
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        cfg.set("interval-minutes", minutes);
        try {
            cfg.save(file);
            return true;
        } catch (IOException ex) {
            Logger.sendMessage("Failed to save minigame interval: " + ex.getMessage(), Logger.LogType.ERR, HANDLE);
            return false;
        }
    }

    public boolean setThreshold(MinigameType type, int value) {
        value = Math.max(1, value);
        File file = new File("plugins/SupremeCore/Minigames/config.yml");
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        cfg.set("types." + type.name() + ".min-players", value);
        try {
            cfg.save(file);
            return true;
        } catch (IOException ex) {
            Logger.sendMessage("Failed to save minigame threshold: " + ex.getMessage(), Logger.LogType.ERR, HANDLE);
            return false;
        }
    }

    public boolean setDuration(MinigameType type, int seconds) {
        seconds = Math.max(30, seconds);
        File file = new File("plugins/SupremeCore/Minigames/config.yml");
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        cfg.set("types." + type.name() + ".duration-seconds", seconds);
        try {
            cfg.save(file);
            return true;
        } catch (IOException ex) {
            Logger.sendMessage("Failed to save minigame duration: " + ex.getMessage(), Logger.LogType.ERR, HANDLE);
            return false;
        }
    }
}
