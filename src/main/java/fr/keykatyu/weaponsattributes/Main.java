package fr.keykatyu.weaponsattributes;

import fr.keykatyu.weaponsattributes.command.FixAttributesCommand;
import fr.keykatyu.weaponsattributes.listener.WeaponsAttributesListener;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    private static Main INSTANCE;
    public static final NamespacedKey TAG = new NamespacedKey(NamespacedKey.MINECRAFT, "update");

    @Override
    public void onEnable() {
        INSTANCE = this;
        getCommand("fix-attributes").setExecutor(new FixAttributesCommand());
        getServer().getPluginManager().registerEvents(new WeaponsAttributesListener(), this);
    }

    public static Main getInstance() {
        return INSTANCE;
    }

}