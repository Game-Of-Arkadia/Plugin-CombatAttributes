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
package fr.keykatyu.combatattributes.command;

import fr.keykatyu.combatattributes.Main;
import fr.keykatyu.combatattributes.combat.CombatItem;
import fr.keykatyu.combatattributes.listener.CombatAttributesListener;
import fr.keykatyu.combatattributes.util.Util;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.ItemTag;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Item;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class FixAttributesCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player player)) {
            sender.sendMessage(Util.prefix() + Main.getLang().get("must-be-player"));
            return false;
        }

        ItemStack previousItem = player.getInventory().getItemInMainHand();
        if(Util.hasNetheriteKBResistanceToBeRemoved()) {
            CombatAttributesListener.removeNetheriteKbResistance(previousItem, player.getInventory(), player.getInventory().getHeldItemSlot());
        }

        if(!Util.isCustomItem(previousItem)) {
            sender.sendMessage(Util.prefix() + Main.getLang().get("not-custom-item"));
            return false;
        }

        if(Util.isBlackListed(previousItem)) sender.sendMessage(Util.prefix() + Main.getLang().get("bruteforce-update"));

        ItemStack newItem = new CombatItem(player.getInventory().getItemInMainHand().clone(), player, true).getUpdatedItem();
        player.getInventory().setItemInMainHand(newItem);
        TextComponent textComponent = new TextComponent();
        textComponent.setText(Util.prefix() + Main.getLang().get("item-updated"));
        textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM,
                new Item(previousItem.getType().getKey().toString(), previousItem.getAmount(),
                        ItemTag.ofNbt(previousItem.getItemMeta().getAsString()))));
        player.spigot().sendMessage(textComponent);
        return true;
    }

}