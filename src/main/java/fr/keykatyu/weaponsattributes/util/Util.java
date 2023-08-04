package fr.keykatyu.weaponsattributes.util;

import fr.keykatyu.weaponsattributes.Main;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import org.bukkit.craftbukkit.v1_20_R1.inventory.CraftItemStack;
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
        net.minecraft.world.item.ItemStack nmsItem = CraftItemStack.asNMSCopy(itemStack);
        NBTTagCompound nbtTagCompound = nmsItem.v();
        if(nbtTagCompound == null) return false;
        NBTTagList list = nbtTagCompound.c("Tags", 8);
        if(list == null) return false;
        return list.toString().contains("customItem");
    }

    /**
     * Add the tag to the item to make it a custom item
     * @param is The item
     */
    public static ItemStack setCustomItem(ItemStack is) {
        net.minecraft.world.item.ItemStack nmsItem = CraftItemStack.asNMSCopy(is);
        NBTTagCompound nbtTagCompound = nmsItem.v();
        if(nbtTagCompound == null) nbtTagCompound = new NBTTagCompound();
        NBTTagList list = nbtTagCompound.c("Tags", 8);
        list.b(list.size(), NBTTagString.a("customItem"));
        nbtTagCompound.a("Tags", list);
        nmsItem.c(nbtTagCompound);
        return CraftItemStack.asBukkitCopy(nmsItem);
    }

}