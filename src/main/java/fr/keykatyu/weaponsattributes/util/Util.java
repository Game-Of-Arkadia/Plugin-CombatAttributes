package fr.keykatyu.weaponsattributes.util;

import fr.keykatyu.weaponsattributes.Main;

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

    public static boolean isInteger(double number){
        return Math.ceil(number) == Math.floor(number);
    }

}
