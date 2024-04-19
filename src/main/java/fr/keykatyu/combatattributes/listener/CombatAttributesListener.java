
/*
 * CombatAttributes plugin to remove custom combat items attributes
 * Copyright (C) 2024 KeyKatyu (Antoine D.)
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package fr.keykatyu.combatattributes.listener;

import com.ssomar.score.api.executableitems.events.AddItemInPlayerInventoryEvent;
import fr.keykatyu.combatattributes.combat.CombatCalculator;
import fr.keykatyu.combatattributes.combat.CombatItem;
import fr.keykatyu.combatattributes.util.ItemBuilder;
import fr.keykatyu.combatattributes.util.Util;
import fr.keykatyu.mctranslation.api.Language;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterials;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.inventory.PrepareSmithingEvent;
import org.bukkit.event.player.PlayerLocaleChangeEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.SmithingInventory;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collection;
import java.util.UUID;

public class CombatAttributesListener implements Listener {

    /**
     * Update enchanted item in the enchantment table with the new stats lore
     * @param e The event
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onItemEnchantedTable(EnchantItemEvent e) {
        ItemStack is = e.getItem();
        if(Util.hasNetheriteKBResistanceToBeRemoved()) {
            removeNetheriteKbResistance(is, e.getInventory(), 0);
        }
        if(!Util.isCustomItem(is)) return;
        if(Util.isBlackListed(is)) return;
        ItemBuilder ib = new ItemBuilder(is);
        e.getEnchantsToAdd().forEach(ib::addEnchant);
        e.getInventory().setItem(0, new CombatItem(ib.toItemStack(), e.getEnchanter(), false).getUpdatedItem());
    }

    /**
     * Update enchanted item in the anvil with the new stats lore
     * @param e The event
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onItemEnchantedAnvil(PrepareAnvilEvent e) {
        if(e.getViewers().isEmpty()) return;
        Player player = (Player) e.getViewers().get(0);
        AnvilInventory inventory = e.getInventory();
        if(inventory.getItem(0) == null || inventory.getItem(1) == null) return;

        // Find book slot and item slot
        EnchantmentStorageMeta meta;
        int itemSlot;
        if(inventory.getItem(0).getItemMeta() != null
                && inventory.getItem(0).getItemMeta() instanceof EnchantmentStorageMeta enchantMeta) {
            meta = enchantMeta;
            itemSlot = 1;
        } else if(inventory.getItem(1) != null && inventory.getItem(1).getItemMeta() != null
                && inventory.getItem(1).getItemMeta() instanceof EnchantmentStorageMeta enchantMeta) {
            meta = enchantMeta;
            itemSlot = 0;
        } else {
            return;
        }
        ItemStack is = inventory.getItem(itemSlot).clone();
        if(Util.hasNetheriteKBResistanceToBeRemoved()) {
            removeNetheriteKbResistance(is, e.getInventory(), 2);
        }
        if(!Util.isCustomItem(is)) return;
        if(Util.isBlackListed(is)) return;
        if(!meta.hasStoredEnchants()) return;

        ItemBuilder ib = new ItemBuilder(is.clone());
        meta.getStoredEnchants().forEach((enchant, level) -> {
            if(enchant.canEnchantItem(is)) {
                ib.addEnchant(enchant, level);
            }
        });
        e.setResult(new CombatItem(ib.toItemStack(), player, false).getUpdatedItem());
    }

    /**
     * Called when a custom item from ExecutableItems plugin is given
     * @param e The event
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onExecutableItemGave(AddItemInPlayerInventoryEvent e) {
        ItemStack is = e.getItem().clone();
        Player player = e.getPlayer();
        if(Util.hasNetheriteKBResistanceToBeRemoved()) {
            removeNetheriteKbResistance(is, player.getInventory(), e.getSlot());
        }
        if(!Util.isCustomItem(is)) return;
        if(Util.isBlackListed(e.getItem())) return;
        CombatItem combatItem = new CombatItem(is, player, false);
        player.getInventory().setItem(e.getSlot(), combatItem.getUpdatedItem());
    }

    /**
     * Auto-update custom items when the player changes its Locale
     * @param e The event
     */
    @EventHandler
    public void onPlayerLocalChanged(PlayerLocaleChangeEvent e) {
        Player player = e.getPlayer();
        ItemStack[] items = player.getInventory().getContents();
        for (int i = 0; i < items.length; i++) {
            if(items[i] == null) continue;
            ItemStack is = items[i].clone();
            if(Util.hasNetheriteKBResistanceToBeRemoved()) {
                removeNetheriteKbResistance(is, player.getInventory(), i);
            }
            if(is.getItemMeta().hasAttributeModifiers() && !Util.isBlackListed(is)) {
                items[i] = new CombatItem(is, player, Language.fromLocale(e.getLocale()), false).getUpdatedItem();
            }
        }
        player.getInventory().setContents(items);
    }

    /**
     * Update item in smithing table with the new stats lore
     * @param e The event
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onItemSmithingTable(PrepareSmithingEvent e) {
        if(e.getViewers().isEmpty()) return;
        Player player = (Player) e.getViewers().get(0);
        SmithingInventory inventory = e.getInventory();
        if(inventory.getItem(0) == null || inventory.getItem(1) == null || inventory.getItem(2) == null) return;

        ItemStack is = inventory.getItem(3).clone();
        if(Util.hasNetheriteKBResistanceToBeRemoved()) {
            removeNetheriteKbResistance(is, e.getInventory(), 3);
        }
        if(!Util.isCustomItem(is)) return;
        if(Util.isBlackListed(is)) return;
        e.setResult(new CombatItem(is, player, true).getUpdatedItem());
    }

    /**
     * Automatically update item when moderator pick it
     * from creative inventory
     * @param e The event
     */
    @EventHandler
    public void onCreativeInventoryItemPickedEvent(InventoryCreativeEvent e) {
        if(!e.getAction().name().startsWith("PLACE")) return;
        ItemStack is = e.getCursor();
        Player player = (Player) e.getWhoClicked();
        if(Util.hasNetheriteKBResistanceToBeRemoved()) {
            removeNetheriteKbResistance(is, player.getInventory(), e.getSlot());
        }
        if(is.hasItemMeta() && is.getItemMeta().hasAttributeModifiers() && !Util.isBlackListed(is)) {
            player.getInventory().setItem(e.getSlot(), new CombatItem(is, player, Language.fromPlayer(player), false).getUpdatedItem());
        }
    }

    /**
     * Remove default netherite armor knockback resistance by adding an opposite custom
     * knockback resistance attribute modifier
     * @param is The itemStack
     * @param inv The inventory
     * @param slot The item slot
     */
    public static void removeNetheriteKbResistance(ItemStack is, Inventory inv, int slot) {
        if(CombatItem.Type.retrieveItemType(is) != CombatItem.Type.ARMOR_PIECE) return;
        ArmorItem itemArmor = (ArmorItem) CraftItemStack.asNMSCopy(is).getItem();
        if(itemArmor.getMaterial() != ArmorMaterials.NETHERITE) return;
        ItemMeta im = is.getItemMeta();

        // Return if kb has already been removed
        if(im.hasAttributeModifiers()) {
            Collection<AttributeModifier> attributeModifiers = im.getAttributeModifiers(Attribute.GENERIC_KNOCKBACK_RESISTANCE);
            if(attributeModifiers != null) {
                for(AttributeModifier attribute : attributeModifiers) {
                    if(attribute.getName().equalsIgnoreCase("remove_netheritekbresistance")) {
                        return;
                    }
                }
            }
        }

        double kbResistance = CombatCalculator.defaultValue(itemArmor, itemArmor.getEquipmentSlot(), Attributes.KNOCKBACK_RESISTANCE);
        if(kbResistance > 0) {
            im.addAttributeModifier(Attribute.GENERIC_KNOCKBACK_RESISTANCE,
                    new AttributeModifier(UUID.randomUUID(), "remove_netheritekbresistance", -kbResistance, AttributeModifier.Operation.ADD_NUMBER, is.getType().getEquipmentSlot()));
        }
        is.setItemMeta(im);
        inv.setItem(slot, is);
    }

}