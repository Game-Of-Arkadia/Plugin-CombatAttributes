package fr.keykatyu.weaponsattributes;

import fr.keykatyu.weaponsattributes.listener.WeaponsAttributesListener;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    private static Main INSTANCE;

    @Override
    public void onEnable() {
        INSTANCE = this;
        getServer().getPluginManager().registerEvents(new WeaponsAttributesListener(), this);
    }

    public static Main getInstance() {
        return INSTANCE;
    }

}