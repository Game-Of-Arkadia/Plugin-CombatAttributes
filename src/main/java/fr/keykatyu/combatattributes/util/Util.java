
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
package fr.keykatyu.combatattributes.util;

import fr.keykatyu.combatattributes.Main;
import org.bukkit.inventory.ItemStack;

public class Util {

    /**
     * Get the prefix for the chat messages
     * @return The prefix
     */
    public static String prefix() {
        return Main.getLang().get("prefix") + " ";
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
        return Config.getBoolean("remove-netherite-kb-resistance");
    }

}