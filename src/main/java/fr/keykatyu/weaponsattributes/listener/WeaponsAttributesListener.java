package fr.keykatyu.weaponsattributes.listener;

import com.ssomar.score.api.executableitems.events.AddItemInPlayerInventoryEvent;
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
import org.bukkit.inventory.ItemStack;
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
     * Called when a custom item from ExecutableItems plugin is given
     * @param e The event
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onExecutableItemGave(AddItemInPlayerInventoryEvent e) {
        ItemStack is = e.getItem().clone();
        if(!Util.isCustomItem(is)) return;
        Weapon weapon = new Weapon(is, e.getPlayer());
        e.getPlayer().getInventory().setItem(e.getSlot(), weapon.getUpdatedItem());
    }

}