package net.supremesurvival.supremecore.commonUtils.artefacts;

import com.google.common.collect.Multimap;
import net.supremesurvival.supremecore.commonUtils.Logger;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

import static org.bukkit.attribute.Attribute.GENERIC_ATTACK_DAMAGE;

public class Artefact {

    ItemStack item;
    String itemName;
    List<String> lore;
    String rarity;

    public Artefact(ItemStack itemStack, List<String> loreList, String name, String rarityValue){
        item = itemStack;
        itemName = name;
        lore = loreList;
        rarity = rarityValue;
        ItemMeta itemMeta = item.getItemMeta();
        name = ChatColor.translateAlternateColorCodes('&',name);
        Iterator loreIterator = lore.iterator();
        List<String> tmpLore = new ArrayList<String>();
        while(loreIterator.hasNext()){
            tmpLore.add(ChatColor.translateAlternateColorCodes('&', (String)loreIterator.next()));
        }
        itemMeta.setDisplayName(name);
        itemMeta.setLore(tmpLore);
        AttributeModifier modifier = new AttributeModifier(UUID.randomUUID(),"generic.attackDamage",10, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND);
        itemMeta.addAttributeModifier(GENERIC_ATTACK_DAMAGE, modifier);
        item.setItemMeta(itemMeta);
    }

}
