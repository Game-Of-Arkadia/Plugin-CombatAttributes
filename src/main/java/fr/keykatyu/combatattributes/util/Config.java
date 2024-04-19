
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