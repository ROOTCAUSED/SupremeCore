package net.supremesurvival.supremecore.morality;

import net.supremesurvival.supremecore.commonUtils.Logger;
import net.supremesurvival.supremecore.commonUtils.fileHandler.ConfigUtility;
import net.supremesurvival.supremecore.commonUtils.fileHandler.FileHandler;
import net.supremesurvival.supremecore.commonUtils.placeholder.SupremePlaceholder;
import net.supremesurvival.supremecore.morality.player.MoralPlayer;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.raid.RaidFinishEvent;
import org.bukkit.event.raid.RaidTriggerEvent;

import java.io.*;
import java.time.Instant;
import java.util.*;

public class Morality implements Listener {
    private static final String HANDLE = "Morality";

    private static final Map<UUID, MoralPlayer> moralPlayers = new HashMap<>();
    private static final NavigableMap<Integer, String> boundsMap = new TreeMap<>();
    private static final Map<String, Integer> actionWeights = new HashMap<>();

    private static FileConfiguration moralityConfig;
    private static File dataFile;

    private static int minMorality = -16000;
    private static int maxMorality = 16000;
    private static boolean decayEnabled = true;
    private static int decayPointsPerDay = 25;

    public static void enable() {
        SupremePlaceholder.register();
        moralityConfig = ConfigUtility.getModuleConfig("Morality");

        loadBounds();
        loadSettings();
        loadActionWeights();

        moralPlayers.clear();
        dataFile = FileHandler.getDataFile("/Morality/playerdata.txt");
        loadData();

        Logger.sendMessage("Morality enabled. Loaded " + moralPlayers.size() + " player records.", Logger.LogType.INFO, HANDLE);
    }

    public static void disable() {
        saveData();
    }

    public static void reload() {
        saveData();
        enable();
    }

    private static void loadBounds() {
        boundsMap.clear();

        ConfigurationSection section = moralityConfig.getConfigurationSection("bounds");
        if (section == null) {
            section = moralityConfig.getConfigurationSection("moralitybounds"); // Backward compat
        }

        if (section == null) {
            Logger.sendMessage("No morality bounds found in config. Falling back to NEUTRAL(0).", Logger.LogType.WARN, HANDLE);
            boundsMap.put(0, "NEUTRAL");
            return;
        }

        for (String key : section.getKeys(false)) {
            int threshold = section.getInt(key);
            boundsMap.put(threshold, key.toUpperCase(Locale.ROOT));
        }

        if (boundsMap.isEmpty()) {
            boundsMap.put(0, "NEUTRAL");
        }
    }

    private static void loadSettings() {
        minMorality = moralityConfig.getInt("morality.min", -16000);
        maxMorality = moralityConfig.getInt("morality.max", 16000);

        decayEnabled = moralityConfig.getBoolean("morality.decay.enabled", true);
        decayPointsPerDay = moralityConfig.getInt("morality.decay.points-per-day", 25);
    }

    private static void loadActionWeights() {
        actionWeights.clear();

        ConfigurationSection section = moralityConfig.getConfigurationSection("actions");
        if (section == null) {
            actionWeights.put("raid-win", 1000);
            actionWeights.put("raid-trigger", -250);
            actionWeights.put("villager-kill", -50);
            actionWeights.put("kill-good-player", -150);
            actionWeights.put("kill-evil-player", 150);
            return;
        }

        for (String key : section.getKeys(false)) {
            actionWeights.put(key.toLowerCase(Locale.ROOT), section.getInt(key));
        }
    }

    private static void loadData() {
        if (dataFile == null || !dataFile.exists()) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(dataFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;

                // Supported formats:
                // uuid:morality
                // uuid:morality:lastUpdatedEpochSeconds
                String[] parts = line.split(":");
                if (parts.length < 2) continue;

                UUID uuid;
                try {
                    uuid = UUID.fromString(parts[0]);
                } catch (IllegalArgumentException ignored) {
                    continue;
                }

                int morality;
                try {
                    morality = Integer.parseInt(parts[1]);
                } catch (NumberFormatException ignored) {
                    morality = 0;
                }

                long lastUpdated = Instant.now().getEpochSecond();
                if (parts.length >= 3) {
                    try {
                        lastUpdated = Long.parseLong(parts[2]);
                    } catch (NumberFormatException ignored) {
                        // Keep now
                    }
                }

                morality = clamp(morality);
                String standing = resolveStanding(morality);
                moralPlayers.put(uuid, new MoralPlayer(uuid, morality, standing, lastUpdated));
            }
        } catch (IOException e) {
            Logger.sendMessage("Failed to load morality data: " + e.getMessage(), Logger.LogType.ERR, HANDLE);
        }
    }

    private static void saveData() {
        if (dataFile == null) return;

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(dataFile))) {
            for (MoralPlayer moralPlayer : moralPlayers.values()) {
                writer.write(moralPlayer.getUuid().toString());
                writer.write(":");
                writer.write(String.valueOf(moralPlayer.getMorality()));
                writer.write(":");
                writer.write(String.valueOf(moralPlayer.getLastUpdatedEpochSeconds()));
                writer.newLine();
            }
        } catch (IOException e) {
            Logger.sendMessage("Failed to save morality data: " + e.getMessage(), Logger.LogType.ERR, HANDLE);
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        MoralPlayer moralPlayer = getOrCreate(player.getUniqueId());
        applyDecay(moralPlayer);
        updateStanding(moralPlayer, false);
    }

    @EventHandler
    public void onRaidFinish(RaidFinishEvent event) {
        int amount = actionWeights.getOrDefault("raid-win", 0);
        for (Player winner : event.getWinners()) {
            adjustMorality(winner.getUniqueId(), amount, "Raid victory", true);
        }
    }

    @EventHandler
    public void onRaidTrigger(RaidTriggerEvent event) {
        int amount = actionWeights.getOrDefault("raid-trigger", 0);
        adjustMorality(event.getPlayer().getUniqueId(), amount, "Triggered a raid", true);
    }

    @EventHandler
    public void onVillagerDeath(EntityDeathEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof Villager villager)) return;

        Player killer = villager.getKiller();
        if (killer == null) return;

        int amount = actionWeights.getOrDefault("villager-kill", 0);
        adjustMorality(killer.getUniqueId(), amount, "Killed a villager", true);
    }

    @EventHandler
    public void onPlayerKill(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        Player killer = victim.getKiller();
        if (killer == null) return;

        MoralPlayer victimData = getOrCreate(victim.getUniqueId());
        String victimStanding = victimData.getStanding();

        int delta;
        if (isEvilStanding(victimStanding)) {
            delta = actionWeights.getOrDefault("kill-evil-player", 0);
        } else {
            delta = actionWeights.getOrDefault("kill-good-player", 0);
        }

        adjustMorality(killer.getUniqueId(), delta, "Killed " + victim.getName(), true);
    }

    private static boolean isEvilStanding(String standing) {
        return standing.equalsIgnoreCase("CHAOTIC")
                || standing.equalsIgnoreCase("CORRUPT")
                || standing.equalsIgnoreCase("DISHONOURABLE")
                || standing.equalsIgnoreCase("INFAMOUS")
                || standing.equalsIgnoreCase("RUTHLESS")
                || standing.equalsIgnoreCase("VILLAINOUS")
                || standing.equalsIgnoreCase("MALEVOLENT")
                || standing.equalsIgnoreCase("WRETCHED")
                || standing.equalsIgnoreCase("DIABOLICAL")
                || standing.equalsIgnoreCase("DEMONIC");
    }

    public static void adjustMorality(UUID uuid, int delta, String reason, boolean notifyPlayer) {
        MoralPlayer moralPlayer = getOrCreate(uuid);
        applyDecay(moralPlayer);

        int before = moralPlayer.getMorality();
        int after = clamp(before + delta);
        moralPlayer.setMorality(after);
        moralPlayer.setLastUpdatedEpochSeconds(Instant.now().getEpochSecond());

        String previousStanding = moralPlayer.getStanding();
        updateStanding(moralPlayer, notifyPlayer);

        Player player = Bukkit.getPlayer(uuid);
        if (notifyPlayer && player != null && player.isOnline() && delta != 0) {
            String sign = delta > 0 ? "+" : "";
            player.sendMessage("§7[§6Morality§7] " + reason + " §8(§f" + sign + delta + "§8) §7=> §f" + after);
            if (!previousStanding.equalsIgnoreCase(moralPlayer.getStanding())) {
                player.sendMessage("§7[§6Morality§7] Alignment changed: §f" + pretty(previousStanding) + " §7-> §f" + pretty(moralPlayer.getStanding()));
            }
        }
    }

    private static void applyDecay(MoralPlayer moralPlayer) {
        if (!decayEnabled || decayPointsPerDay <= 0) return;

        long now = Instant.now().getEpochSecond();
        long elapsedSeconds = now - moralPlayer.getLastUpdatedEpochSeconds();
        if (elapsedSeconds < 86400) return;

        long days = elapsedSeconds / 86400;
        int decayAmount = (int) (days * decayPointsPerDay);

        int morality = moralPlayer.getMorality();
        if (morality > 0) {
            morality = Math.max(0, morality - decayAmount);
        } else if (morality < 0) {
            morality = Math.min(0, morality + decayAmount);
        }

        moralPlayer.setMorality(clamp(morality));
        moralPlayer.setLastUpdatedEpochSeconds(now);
    }

    public static MoralPlayer getOrCreate(UUID uuid) {
        MoralPlayer moralPlayer = moralPlayers.get(uuid);
        if (moralPlayer != null) return moralPlayer;

        moralPlayer = new MoralPlayer(uuid, 0, resolveStanding(0), Instant.now().getEpochSecond());
        moralPlayers.put(uuid, moralPlayer);
        return moralPlayer;
    }

    private static void updateStanding(MoralPlayer moralPlayer, boolean notifyPlayer) {
        String previous = moralPlayer.getStanding();
        String resolved = resolveStanding(moralPlayer.getMorality());
        moralPlayer.setStanding(resolved);

        if (!notifyPlayer || previous.equalsIgnoreCase(resolved)) return;

        Player player = Bukkit.getPlayer(moralPlayer.getUuid());
        if (player != null && player.isOnline()) {
            player.sendMessage("§7[§6Morality§7] You are now considered §f" + pretty(resolved));
        }
    }

    private static String resolveStanding(int morality) {
        Map.Entry<Integer, String> entry = boundsMap.floorEntry(morality);
        if (entry == null) {
            return boundsMap.firstEntry().getValue();
        }
        return entry.getValue();
    }

    private static int clamp(int morality) {
        return Math.max(minMorality, Math.min(maxMorality, morality));
    }

    private static String pretty(String raw) {
        String lower = raw.toLowerCase(Locale.ROOT);
        return lower.substring(0, 1).toUpperCase(Locale.ROOT) + lower.substring(1);
    }

    public static String getMoralStanding(Player player) {
        if (player == null) return "NEUTRAL";
        MoralPlayer data = getOrCreate(player.getUniqueId());
        applyDecay(data);
        updateStanding(data, false);
        return data.getStanding();
    }

    public static int getMorality(Player player) {
        if (player == null) return 0;
        MoralPlayer data = getOrCreate(player.getUniqueId());
        applyDecay(data);
        return data.getMorality();
    }

    public static List<MoralPlayer> top(int limit) {
        List<MoralPlayer> values = new ArrayList<>(moralPlayers.values());
        values.sort(Comparator.comparingInt(MoralPlayer::getMorality).reversed());
        if (values.size() <= limit) return values;
        return values.subList(0, limit);
    }
}
