package net.supremesurvival.supremecore.mobUtils;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Iterator;
import java.util.List;

//This class allows us to censor the drops for mobs. This allows us to prevent gold farms.
//Specifically this class allows us to remove and censor specific drops (ITEMSTACKS) from specific (or all) mob drops on mob death.
//This class does NOT check if the killer was an instance of a player so as to also remove gold from non player killed mob drops.

public class MobLoot implements Listener {
public ItemStack item;
public Material loot;
public List <ItemStack> drop;

    @EventHandler
    public boolean mobDeath(EntityDeathEvent event){
        LivingEntity entity = event.getEntity();
        if (entity instanceof PigZombie) {
            checkLoot(event);
        }
        if (entity instanceof Drowned){
            checkLoot(event);
        }return true;
    }
    public boolean checkLoot(EntityDeathEvent event){
        Iterator<ItemStack> iter = event.getDrops().iterator();
        while (iter.hasNext()){
            item = iter.next();
            loot = item.getType();
            if(loot.equals(Material.GOLD_INGOT) || loot.equals(Material.GOLD_NUGGET) || loot.equals(Material.GOLDEN_SWORD)){
                iter.remove();
                Bukkit.getConsoleSender().sendMessage(loot.toString());
            }
            }
        Bukkit.getConsoleSender().sendMessage("nerfed loot");
        return true;
    }
}
