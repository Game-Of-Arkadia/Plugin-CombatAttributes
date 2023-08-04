package fr.keykatyu.weaponsattributes.util;

import fr.keykatyu.weaponsattributes.Main;
import org.bukkit.inventory.ItemStack;

public class Util {

    /**
     * Get the prefix for the chat messages
     * @return The prefix
     */
    public static String prefix() {
        return "§7[§f§lWeaponsAttributes§7] ";
    }

    /**
     * Send a console message with the plugin prefix
     * @param msg The message to send
     */
    public static void console(String msg) {
        Main.getInstance().getServer().getConsoleSender().sendMessage(prefix() + msg);
    }

    /**
     * Verify if the ItemStack is a custom item
     * @param itemStack The item
     * @return True if it's a custom item, false otherwise
     */
    public static boolean isCustomItem(ItemStack itemStack) {
        if(!itemStack.hasItemMeta()) return false;
        return itemStack.getItemMeta().hasAttributeModifiers();
    }

}