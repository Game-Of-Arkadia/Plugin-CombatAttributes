package fr.keykatyu.combatattributes.util;

import fr.keykatyu.combatattributes.Main;
import org.bukkit.inventory.ItemStack;

public class Util {

    /**
     * Get the prefix for the chat messages
     * @return The prefix
     */
    public static String prefix() {
        return Config.getString("messages.prefix") + " ";
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

    /**
     * Get if the itemStack is blacklisted from the plugin
     * (items to NOT update)
     * @param itemStack The itemStack
     * @return True or false
     */
    public static boolean isBlackListed(ItemStack itemStack) {
        return Config.getMaterialList("blacklist").contains(itemStack.getType());
    }

    public static boolean hasNetheriteKBResistanceToBeRemoved() {
        return Config.getBoolean("config.remove-netherite-kb-resistance");
    }

}