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