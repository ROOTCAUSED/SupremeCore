package net.supremesurvival.supremecore.sanguine;

import net.supremesurvival.supremecore.SupremeCore;
import net.supremesurvival.supremecore.commonUtils.fileHandler.ConfigUtility;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Vampire implements Listener, CommandExecutor {
    private final SupremeCore plugin;
    private final Map<UUID, VampirismState> vampireStates = new HashMap<>();

    private FileConfiguration sanguineConfig;
    private File moduleFile;
    private BukkitTask sunTask;

    private long feedIntervalMillis;
    private double sunDamage;
    private long sunTickInterval;
    private String feedMessage;
    private String starveMessage;

    public Vampire(SupremeCore plugin) {
        this.plugin = plugin;
    }

    public void enable() {
        this.sanguineConfig = ConfigUtility.getModuleConfig("Sanguine");
        this.moduleFile = new File("plugins/SupremeCore/Sanguine/config.yml");

        long feedIntervalMillis = Math.max(20L, sanguineConfig.getLong("feed-interval-ticks", 72000L));
        this.feedIntervalMillis = feedIntervalMillis * 50L;
        this.sunDamage = Math.max(0.5, sanguineConfig.getDouble("sun-damage", 1.0));
        this.sunTickInterval = Math.max(20L, sanguineConfig.getLong("sun-damage-interval-ticks", 40L));
        this.feedMessage = colorize(sanguineConfig.getString("messages.feed", "&cYou feed on the villager's blood. The curse subsides... for now."));
        this.starveMessage = colorize(sanguineConfig.getString("messages.starve", "&4Your hunger gnaws at you. The sun now burns your skin."));

        loadStates();
        startSunTask();
    }

    public void disable() {
        if (sunTask != null) {
            sunTask.cancel();
            sunTask = null;
        }
        saveStates();
    }

    private void startSunTask() {
        this.sunTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                VampirismState state = vampireStates.get(player.getUniqueId());
                if (state == null || !state.isVampire) {
                    continue;
                }
                if (player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR) {
                    continue;
                }

                if (isStarving(state) && isInDirectSunlight(player)) {
                    player.damage(sunDamage);
                    if (!state.starveMessageSent) {
                        player.sendMessage(starveMessage);
                        state.starveMessageSent = true;
                    }
                }
            }
        }, sunTickInterval, sunTickInterval);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        vampireStates.putIfAbsent(uuid, new VampirismState(false, 0L, false));
    }

    @EventHandler
    public void onFeed(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        Entity clicked = event.getRightClicked();

        if (!(clicked instanceof Villager villager)) {
            return;
        }

        VampirismState state = vampireStates.get(player.getUniqueId());
        if (state == null || !state.isVampire) {
            return;
        }

        if (!villager.isSleeping()) {
            return;
        }

        state.lastFedMillis = currentTimeMillis();
        state.starveMessageSent = false;
        player.sendMessage(feedMessage);
        saveStates();
    }

    @EventHandler
    public void onCombust(EntityCombustEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        if (hasSunProtection(player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onSunDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        if (event.getCause() != EntityDamageEvent.DamageCause.FIRE && event.getCause() != EntityDamageEvent.DamageCause.FIRE_TICK) {
            return;
        }
        if (hasSunProtection(player)) {
            event.setCancelled(true);
        }
    }

    private boolean hasSunProtection(Player player) {
        VampirismState state = vampireStates.get(player.getUniqueId());
        return state != null && state.isVampire && !isStarving(state);
    }

    private boolean isStarving(VampirismState state) {
        long elapsed = currentTimeMillis() - state.lastFedMillis;
        return elapsed >= feedIntervalMillis;
    }

    private boolean isInDirectSunlight(Player player) {
        World world = player.getWorld();
        long time = world.getTime();
        boolean daytime = time >= 0 && time < 12300;
        if (!daytime || world.hasStorm()) {
            return false;
        }
        return player.getLocation().getBlock().getLightFromSky() >= 12 && world.getHighestBlockYAt(player.getLocation()) <= player.getLocation().getBlockY();
    }

    private long currentTimeMillis() {
        return System.currentTimeMillis();
    }

    private void loadStates() {
        vampireStates.clear();
        if (!sanguineConfig.isConfigurationSection("players")) {
            return;
        }

        for (String uuidString : sanguineConfig.getConfigurationSection("players").getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(uuidString);
                String path = "players." + uuidString;
                boolean isVampire = sanguineConfig.getBoolean(path + ".is-vampire", false);
                long lastFed = sanguineConfig.getLong(path + ".last-fed-millis", 0L);
                boolean notified = sanguineConfig.getBoolean(path + ".starve-message-sent", false);
                vampireStates.put(uuid, new VampirismState(isVampire, lastFed, notified));
            } catch (IllegalArgumentException ignored) {
                // Ignore bad UUID entries.
            }
        }
    }

    private void saveStates() {
        sanguineConfig.set("players", null);
        for (Map.Entry<UUID, VampirismState> entry : vampireStates.entrySet()) {
            String base = "players." + entry.getKey();
            VampirismState state = entry.getValue();
            sanguineConfig.set(base + ".is-vampire", state.isVampire);
            sanguineConfig.set(base + ".last-fed-millis", state.lastFedMillis);
            sanguineConfig.set(base + ".starve-message-sent", state.starveMessageSent);
        }

        try {
            sanguineConfig.save(moduleFile);
        } catch (IOException e) {
            plugin.getLogger().warning("Failed to save Sanguine config: " + e.getMessage());
        }
    }

    public void setVampire(Player player, boolean vampire) {
        VampirismState state = vampireStates.computeIfAbsent(player.getUniqueId(), id -> new VampirismState(false, 0L, false));
        state.isVampire = vampire;
        state.lastFedMillis = currentTimeMillis();
        state.starveMessageSent = false;
        saveStates();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: /vampire <curse|cure|status> <player>");
            return true;
        }

        String sub = args[0].toLowerCase();
        Player target = Bukkit.getPlayerExact(args[1]);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Player not found.");
            return true;
        }

        switch (sub) {
            case "curse" -> {
                setVampire(target, true);
                target.sendMessage(colorize("&5A dark curse takes hold... you are now a vampire."));
                sender.sendMessage(ChatColor.GREEN + target.getName() + " is now cursed with vampirism.");
            }
            case "cure" -> {
                setVampire(target, false);
                target.sendMessage(colorize("&aThe curse is lifted. You are no longer a vampire."));
                sender.sendMessage(ChatColor.GREEN + target.getName() + " is no longer a vampire.");
            }
            case "status" -> {
                VampirismState state = vampireStates.get(target.getUniqueId());
                boolean isVampire = state != null && state.isVampire;
                boolean starving = isVampire && isStarving(state);
                sender.sendMessage(ChatColor.YELLOW + target.getName() + " vampire=" + isVampire + ", starving=" + starving);
            }
            default -> sender.sendMessage(ChatColor.RED + "Usage: /vampire <curse|cure|status> <player>");
        }

        return true;
    }

    private String colorize(String msg) {
        return ChatColor.translateAlternateColorCodes('&', msg);
    }

    private static class VampirismState {
        boolean isVampire;
        long lastFedMillis;
        boolean starveMessageSent;

        VampirismState(boolean isVampire, long lastFedMillis, boolean starveMessageSent) {
            this.isVampire = isVampire;
            this.lastFedMillis = lastFedMillis;
            this.starveMessageSent = starveMessageSent;
        }
    }
}
