package net.supremesurvival.supremecore.loot;

import net.supremesurvival.supremecore.SupremeCore;
import net.supremesurvival.supremecore.artefacts.Artefact;
import net.supremesurvival.supremecore.artefacts.ArtefactManager;
import net.supremesurvival.supremecore.commonUtils.Logger;
import net.supremesurvival.supremecore.commonUtils.fileHandler.ConfigUtility;
import net.supremesurvival.supremecore.tomes.Tome;
import net.supremesurvival.supremecore.tomes.TomeManager;
import org.bukkit.NamespacedKey;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.loot.Lootable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LootManager implements Listener {
    private static final String HANDLE = "Loot";

    private final SupremeCore plugin;
    private final Random random = new Random();
    private final NamespacedKey injectedKey;

    private double tomeChance;
    private double artefactChance;
    private int maxRollsPerChest;
    private boolean requireLootTable;

    public LootManager(SupremeCore plugin) {
        this.plugin = plugin;
        this.injectedKey = new NamespacedKey(plugin, "loot_injected");
    }

    public void enable() {
        FileConfiguration config = ConfigUtility.getModuleConfig("Loot");
        this.tomeChance = clampChance(config.getDouble("injection.tome-chance", 0.12));
        this.artefactChance = clampChance(config.getDouble("injection.artefact-chance", 0.04));
        this.maxRollsPerChest = Math.max(1, config.getInt("injection.max-rolls", 1));
        this.requireLootTable = config.getBoolean("injection.require-loot-table", true);

        Logger.sendMessage("Loot module enabled. tomeChance=" + tomeChance + ", artefactChance=" + artefactChance,
                Logger.LogType.INFO, HANDLE);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onInventoryOpen(InventoryOpenEvent event) {
        if (!(event.getPlayer() instanceof Player)) {
            return;
        }

        Inventory inventory = event.getInventory();
        if (!(inventory.getHolder() instanceof Chest chest)) {
            return;
        }

        BlockState state = chest.getBlock().getState();
        if (!(state instanceof Lootable lootable)) {
            return;
        }

        if (requireLootTable && lootable.getLootTable() == null) {
            return;
        }

        LootChest lootChest = new LootChest(state, injectedKey);
        if (lootChest.hasInjectedLoot()) {
            return;
        }

        injectRandomLoot(inventory);
        lootChest.markInjected();
    }

    private void injectRandomLoot(Inventory inventory) {
        List<ItemStack> rewards = new ArrayList<>();

        for (int i = 0; i < maxRollsPerChest; i++) {
            if (random.nextDouble() <= tomeChance) {
                Tome tome = randomTome();
                if (tome != null) {
                    rewards.add(tome.getItem());
                }
            }
            if (random.nextDouble() <= artefactChance) {
                Artefact artefact = randomArtefact();
                if (artefact != null) {
                    rewards.add(artefact.getItem());
                }
            }
        }

        for (ItemStack reward : rewards) {
            inventory.addItem(reward);
        }
    }

    private Tome randomTome() {
        if (TomeManager.tomes.isEmpty()) {
            return null;
        }
        return TomeManager.tomes.get(random.nextInt(TomeManager.tomes.size()));
    }

    private Artefact randomArtefact() {
        if (ArtefactManager.artefacts.isEmpty()) {
            return null;
        }
        return ArtefactManager.artefacts.get(random.nextInt(ArtefactManager.artefacts.size()));
    }

    private double clampChance(double value) {
        if (value < 0.0) return 0.0;
        return Math.min(value, 1.0);
    }
}
