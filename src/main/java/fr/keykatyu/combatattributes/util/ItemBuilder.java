package fr.keykatyu.combatattributes.util;

import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * ITEM BUILDER class utility
 * Getters and setters to create easily a custom itemstack
 * Imported from other projects, by KeyKatyu
 */
public class ItemBuilder {
    private final ItemStack is;

    public ItemBuilder(ItemStack is) {
        this.is = is;
    }

    public ItemBuilder setLore(List<String> strings) {
        ItemMeta im = is.getItemMeta();
        List<String> lore = new ArrayList<>();
        for(String str : strings) {
            if(str != null) {
                lore.addAll(Arrays.asList(str.replaceAll("&", "§").split("\n")));
            }
        }
        im.setLore(lore);
        is.setItemMeta(im);
        return this;
    }

    public ItemBuilder setItemsFlags(ItemFlag... itemFlags) {
        ItemMeta im = is.getItemMeta();
        im.addItemFlags(itemFlags);
        is.setItemMeta(im);
        return this;
    }

    public ItemBuilder addNBTKey(NamespacedKey key, PersistentDataType dataType, Object object) {
        ItemMeta im = is.getItemMeta();
        im.getPersistentDataContainer().set(key, dataType, object);
        is.setItemMeta(im);
        return this;
    }

    public ItemBuilder addEnchant(Enchantment enchantment, int level) {
        is.addEnchantment(enchantment, level);
        return this;
    }

    public ItemBuilder addAttributeModifier(Attribute attribute, AttributeModifier attributeModifier) {
        ItemMeta im = is.getItemMeta();
        im.addAttributeModifier(attribute, attributeModifier);
        is.setItemMeta(im);
        return this;
    }

    public ItemBuilder updateAttributeModifierValue(Attribute attribute, String name, double value) {
        ItemMeta im = is.getItemMeta();
        for(AttributeModifier attributeModifier : im.getAttributeModifiers(attribute)) {
            if(attributeModifier.getName().equals(name)) {
                im.removeAttributeModifier(attribute, attributeModifier);
                im.addAttributeModifier(attribute, new AttributeModifier(attributeModifier.getUniqueId(),
                        name, value, attributeModifier.getOperation(), attributeModifier.getSlot()));

            }
        }
        is.setItemMeta(im);
        return this;
    }

    public ItemStack toItemStack() {
        return is;
    }

}