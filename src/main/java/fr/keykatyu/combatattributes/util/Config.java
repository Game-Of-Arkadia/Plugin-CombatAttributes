package fr.keykatyu.combatattributes.util;

import fr.keykatyu.combatattributes.Main;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

/**
 * CONFIG class utility
 * Variable getters and setters
 * Imported from other projects, by KeyKatyu
 */
public class Config {

    public static List<String> getStringList(String path) {
        return Main.getInstance().getConfig().getStringList(path);
    }

    public static List<Material> getMaterialList(String path) {
        List<Material> materials = new ArrayList<>();
        for(String str : getStringList(path)) {
            materials.add(Material.matchMaterial(str));
        }
        return materials;
    }

    public static Boolean getBoolean(String path) {
        return Main.getInstance().getConfig().getBoolean(path);
    }

}