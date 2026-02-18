package net.supremesurvival.supremecore.loot;

import org.bukkit.NamespacedKey;
import org.bukkit.block.BlockState;
import org.bukkit.block.TileState;
import org.bukkit.persistence.PersistentDataType;

public class LootChest {
    private final TileState tileState;
    private final NamespacedKey injectedKey;

    public LootChest(BlockState state, NamespacedKey injectedKey) {
        if (!(state instanceof TileState tile)) {
            throw new IllegalArgumentException("LootChest requires a TileState");
        }
        this.tileState = tile;
        this.injectedKey = injectedKey;
    }

    public boolean hasInjectedLoot() {
        Byte value = tileState.getPersistentDataContainer().get(injectedKey, PersistentDataType.BYTE);
        return value != null && value == (byte) 1;
    }

    public void markInjected() {
        tileState.getPersistentDataContainer().set(injectedKey, PersistentDataType.BYTE, (byte) 1);
        tileState.update(true, false);
    }
}
