package net.supremesurvival.supremecore.artefacts;

import org.bukkit.ChatColor;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.bukkit.attribute.Attribute.GENERIC_ATTACK_DAMAGE;

public class Artefact {

    private final ItemStack item;
    private final String itemName;
    private final List<String> lore;
    private final String rarity;

    public Artefact(ItemStack itemStack, List<String> loreList, String name, String rarityValue, int damage) {
        this.item = itemStack;
        this.itemName = name;
        this.lore = loreList;
        this.rarity = rarityValue;

        ItemMeta itemMeta = item.getItemMeta();
        if (itemMeta == null) {
            return;
        }

        String translatedName = ChatColor.translateAlternateColorCodes('&', name);
        List<String> translatedLore = new ArrayList<>();
        for (String loreLine : lore) {
            translatedLore.add(ChatColor.translateAlternateColorCodes('&', loreLine));
        }

        itemMeta.setDisplayName(translatedName);
        itemMeta.setLore(translatedLore);

        if (damage > 0) {
            AttributeModifier modifier = new AttributeModifier(
                    UUID.randomUUID(),
                    "generic.attackDamage",
                    damage,
                    AttributeModifier.Operation.ADD_NUMBER,
                    EquipmentSlot.HAND
            );
            itemMeta.addAttributeModifier(GENERIC_ATTACK_DAMAGE, modifier);
        }

        item.setItemMeta(itemMeta);
    }

    public ItemStack getItem() {
        return item.clone();
    }

    public String getItemName() {
        return itemName;
    }

    public String getRarity() {
        return rarity;
    }
}
