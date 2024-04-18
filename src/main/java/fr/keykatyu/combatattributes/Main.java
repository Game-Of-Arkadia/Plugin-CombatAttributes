package fr.keykatyu.combatattributes;

import fr.keykatyu.combatattributes.command.FixAttributesCommand;
import fr.keykatyu.combatattributes.listener.CombatAttributesListener;
import fr.keykatyu.combatattributes.util.Lang;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    private static Main instance;
    private static Lang lang;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        reloadConfig();
        getConfig().options().copyDefaults(true);
        saveConfig();

        // Setup lang files
        Lang.setupFiles();
        lang = new Lang(getConfig().getString("language"));

        getCommand("fix-attributes").setExecutor(new FixAttributesCommand());
        getServer().getPluginManager().registerEvents(new CombatAttributesListener(), this);
    }

    public static Lang getLang() {
        return lang;
    }

    public static Main getInstance() {
        return instance;
    }

}