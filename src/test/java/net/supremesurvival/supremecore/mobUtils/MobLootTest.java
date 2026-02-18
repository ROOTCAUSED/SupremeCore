package net.supremesurvival.supremecore.mobUtils;

import org.bukkit.Material;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MobLootTest {

    @Test
    void removesGoldFarmDrops() {
        MobLoot mobLoot = new MobLoot();

        ItemStack ingot = mock(ItemStack.class);
        when(ingot.getType()).thenReturn(Material.GOLD_INGOT);

        ItemStack nugget = mock(ItemStack.class);
        when(nugget.getType()).thenReturn(Material.GOLD_NUGGET);

        ItemStack sword = mock(ItemStack.class);
        when(sword.getType()).thenReturn(Material.GOLDEN_SWORD);

        ItemStack flesh = mock(ItemStack.class);
        when(flesh.getType()).thenReturn(Material.ROTTEN_FLESH);

        List<ItemStack> drops = new ArrayList<>();
        drops.add(ingot);
        drops.add(nugget);
        drops.add(sword);
        drops.add(flesh);

        EntityDeathEvent event = mock(EntityDeathEvent.class);
        when(event.getDrops()).thenReturn(drops);

        mobLoot.checkLoot(event);

        assertEquals(1, drops.size());
        assertEquals(Material.ROTTEN_FLESH, drops.get(0).getType());
    }
}
