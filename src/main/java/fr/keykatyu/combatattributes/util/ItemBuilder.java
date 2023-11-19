package fr.keykatyu.combatattributes.util;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.banner.Pattern;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.profile.PlayerProfile;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * ITEM BUILDER class utility
 * Getters and setters to create easily a custom itemstack
 * Imported from other projects, by KeyKatyu
 */
public class ItemBuilder {
    private final ItemStack is;

    public ItemBuilder(Material m) {
        this(m, 1);
    }

    public ItemBuilder(ItemStack is) {
        this.is = is;
    }

    public ItemBuilder(Material m, int amount) {
        is = new ItemStack(m, amount);
    }

    public ItemBuilder setMaterial(Material material) {
        is.setType(material);
        return this;
    }

    public ItemBuilder setName(String name) {
        ItemMeta im = is.getItemMeta();
        im.setDisplayName(name);
        is.setItemMeta(im);
        return this;
    }

    public ItemBuilder addBannerPattern(Pattern pattern) {
        BannerMeta bm = (BannerMeta) is.getItemMeta();
        bm.addPattern(pattern);
        is.setItemMeta(bm);
        return this;
    }

    public ItemBuilder setDurability(int durability) {
        ItemMeta meta = is.getItemMeta();
        ((Damageable) meta).setDamage(durability);
        is.setItemMeta(meta);
        return this;
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

    public ItemBuilder setLore(String... strings) {
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

    public ItemBuilder addUnsafeEnchant(Enchantment enchantment, int level) {
        is.addUnsafeEnchantment(enchantment, level);
        return this;
    }

    public ItemBuilder setSkullOwner(String owner) {
        try {
            SkullMeta im = (SkullMeta) is.getItemMeta();
            im.setOwningPlayer(Bukkit.getPlayer(owner));
            is.setItemMeta(im);
        } catch (ClassCastException expected) {
        }
        return this;
    }

    public ItemBuilder setSkull(String url) {
        SkullMeta sm = (SkullMeta) is.getItemMeta();
        PlayerProfile profile = Bukkit.createPlayerProfile(UUID.randomUUID());
        try {
            profile.getTextures().setSkin(new URL(url));
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        sm.setOwnerProfile(profile);
        is.setItemMeta(sm);
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