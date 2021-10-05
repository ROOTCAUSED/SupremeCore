package net.supremesurvival.supremecore.commonUtils.artefacts;

import org.bukkit.Material;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.UUID;

public class Artefact {
    ItemStack item;
    ItemMeta itemMeta;
    public Artefact(ItemStack artefact){
        item = artefact;
        itemMeta = item.getItemMeta();

    }

}
