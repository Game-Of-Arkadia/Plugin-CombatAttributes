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
            sender.sendMessage(Util.prefix() + Main.getLang().get("messages.must-be-player"));
            return false;
        }

        ItemStack previousItem = player.getInventory().getItemInMainHand();
        if(Util.hasNetheriteKBResistanceToBeRemoved()) {
            CombatAttributesListener.removeNetheriteKbResistance(previousItem, player.getInventory(), player.getInventory().getHeldItemSlot());
        }

        if(!Util.isCustomItem(previousItem)) {
            sender.sendMessage(Util.prefix() + Main.getLang().get("messages.not-custom-item"));
            return false;
        }

        if(Util.isBlackListed(previousItem)) sender.sendMessage(Util.prefix() + Main.getLang().get("messages.bruteforce-update"));

        ItemStack newItem = new CombatItem(player.getInventory().getItemInMainHand().clone(), player, true).getUpdatedItem();
        player.getInventory().setItemInMainHand(newItem);
        TextComponent textComponent = new TextComponent();
        textComponent.setText(Util.prefix() + Main.getLang().get("messages.item-updated"));
        textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM,
                new Item(previousItem.getType().getKey().toString(), previousItem.getAmount(),
                        ItemTag.ofNbt(previousItem.getItemMeta().getAsString()))));
        player.spigot().sendMessage(textComponent);
        return true;
    }

}