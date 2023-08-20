package fr.keykatyu.combatattributes.listener;

import com.ssomar.score.api.executableitems.events.AddItemInPlayerInventoryEvent;
import fr.keykatyu.combatattributes.object.CombatItem;
import fr.keykatyu.combatattributes.util.ItemBuilder;
import fr.keykatyu.combatattributes.util.Util;
import fr.keykatyu.mctranslation.Language;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.player.PlayerLocaleChangeEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

public class CombatAttributesListener implements Listener {

    /**
     * Update enchanted item in the enchantment table with the new stats lore
     * @param e The event
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onItemEnchantedTable(EnchantItemEvent e) {
        if(!Util.isCustomItem(e.getItem())) return;
        if(Util.isBlackListed(e.getItem())) return;
        ItemBuilder ib = new ItemBuilder(e.getItem());
        e.getEnchantsToAdd().forEach(ib::addEnchant);
        e.getInventory().setItem(0, new CombatItem(ib.toItemStack(), e.getEnchanter()).getUpdatedItem());
    }

    /**
     * Update enchanted item in the anvil with the new stats lore
     * @param e The event
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onItemEnchantedAnvil(PrepareAnvilEvent e) {
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
        ItemStack item = inventory.getItem(itemSlot);
        if(!Util.isCustomItem(item)) return;
        if(Util.isBlackListed(item)) return;
        if(!meta.hasStoredEnchants()) return;

        ItemBuilder ib = new ItemBuilder(item.clone());
        meta.getStoredEnchants().forEach(ib::addEnchant);
        e.setResult(new CombatItem(ib.toItemStack(), player).getUpdatedItem());
    }

    /**
     * Called when a custom item from ExecutableItems plugin is given
     * @param e The event
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onExecutableItemGave(AddItemInPlayerInventoryEvent e) {
        ItemStack is = e.getItem().clone();
        if(!Util.isCustomItem(is)) return;
        if(Util.isBlackListed(e.getItem())) return;
        CombatItem combatItem = new CombatItem(is, e.getPlayer());
        e.getPlayer().getInventory().setItem(e.getSlot(), combatItem.getUpdatedItem());
    }

    /**
     * Auto-update custom items when the player changes its Locale
     * @param e The event
     */
    @EventHandler
    public void onPlayerLocalChanged(PlayerLocaleChangeEvent e) {
        ItemStack[] items = e.getPlayer().getInventory().getContents();
        for (int i = 0; i < items.length; i++) {
            ItemStack is = items[i];
            if(is != null && is.hasItemMeta() && is.getItemMeta().hasAttributeModifiers()) {
                items[i] = new CombatItem(items[i], e.getPlayer(), Language.fromLocale(e.getLocale())).getUpdatedItem();
            }
        }
        e.getPlayer().getInventory().setContents(items);
    }

}