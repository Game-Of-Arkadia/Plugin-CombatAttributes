package fr.keykatyu.weaponsattributes;

import fr.keykatyu.weaponsattributes.command.FixAttributesCommand;
import fr.keykatyu.weaponsattributes.listener.WeaponsAttributesListener;
import fr.keykatyu.weaponsattributes.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    private static Main INSTANCE;
    private static Plugin executableItems;

    @Override
    public void onEnable() {
        INSTANCE = this;

        if(!setupExecutableItems()) {
            Util.console("§cPlugin disabled due to not ExecutableItems dependency found !");
            getServer().getPluginManager().disablePlugin(this);
        }

        getCommand("fix-attributes").setExecutor(new FixAttributesCommand());
        getServer().getPluginManager().registerEvents(new WeaponsAttributesListener(), this);
    }

    private boolean setupExecutableItems() {
        executableItems = Bukkit.getPluginManager().getPlugin("ExecutableItems");
        return executableItems != null && executableItems.isEnabled();
    }

    public static Plugin getExecutableItems() {
        return executableItems;
    }

    public static Main getInstance() {
        return INSTANCE;
    }

}