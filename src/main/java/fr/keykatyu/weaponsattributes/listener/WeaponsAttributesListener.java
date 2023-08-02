package fr.keykatyu.weaponsattributes.listener;

import fr.keykatyu.weaponsattributes.object.Weapon;
import fr.keykatyu.weaponsattributes.util.ItemBuilder;
import fr.keykatyu.weaponsattributes.util.Util;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

public class WeaponsAttributesListener implements Listener {

    /**
     * Update enchanted item in the enchantment table with the new stats lore
     * @param e The event
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onItemEnchantedTable(EnchantItemEvent e) {
        if(!Util.isCustomItem(e.getItem())) return;
        ItemBuilder ib = new ItemBuilder(e.getItem());
        e.getEnchantsToAdd().forEach(ib::addEnchant);
        e.getInventory().setItem(0, new Weapon(ib.toItemStack(), e.getEnchanter()).getUpdatedItem());
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
        if(!Util.isCustomItem(inventory.getItem(itemSlot))) return;
        if(!meta.hasStoredEnchants()) return;

        ItemBuilder ib = new ItemBuilder(inventory.getItem(itemSlot).clone());
        meta.getStoredEnchants().forEach(ib::addEnchant);
        e.setResult(new Weapon(ib.toItemStack(), player).getUpdatedItem());
    }


    /**
     * Update all items in the inventory with the new stats lore on a double click
     * @param e The event
     */
    /*
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDoubleClickInventoryEvent(InventoryClickEvent e) {
        if(!(e.getClickedInventory() instanceof PlayerInventory inv)) return;
        if(!e.getClick().equals(ClickType.DOUBLE_CLICK)) return;
        ItemStack[] items = inv.getContents();
        for (int i = 0; i < items.length; i++) if(items[i] != null) items[i] = new Weapon(items[i], (Player) e.getWhoClicked()).getUpdatedItem();
        inv.setContents(items);
    }
    */

}